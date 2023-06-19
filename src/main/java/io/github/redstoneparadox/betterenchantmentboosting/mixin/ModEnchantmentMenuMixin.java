package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import com.google.common.collect.Lists;
import fuzs.easymagic.EasyMagic;
import fuzs.easymagic.config.ServerConfig;
import fuzs.easymagic.network.S2CEnchantingDataMessage;
import fuzs.easymagic.world.inventory.ModEnchantmentMenu;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantingUtil;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.function.BiConsumer;

@Pseudo
@Mixin(value = ModEnchantmentMenu.class)
public abstract class ModEnchantmentMenuMixin extends EnchantmentScreenHandler {
	@Shadow(remap = false)
	@Final
	private RandomGenerator random;
	@Shadow(remap = false)
	@Final
	private PlayerEntity player;
	@Shadow(remap = false)
	@Final
	private Property enchantmentSeed;

	public ModEnchantmentMenuMixin(int syncId, PlayerInventory playerInventory) {
		super(syncId, playerInventory);
	}

	@Shadow
	private int getEnchantingPower(World level, BlockPos pos) {
		return 0;
	}

	@Shadow
	private void updateLevels(ItemStack itemstack, World world, BlockPos pos, int power) {

	}

	@Shadow
	private void createClues(ItemStack itemstack) {

	}

	@Inject(method = "getEnchantingPower", at = @At("HEAD"), cancellable = true)
	private void getEnchantingPower(World level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		List<BlockPos> boosterPositions = EnchantingUtil.search(level, pos);
		double power = EnchantingUtil.getPower(level, boosterPositions);

		cir.setReturnValue((int) Math.round(power));
	}

	@ModifyArgs(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
	private void onContentChange_CallRun(Args args, Inventory inventory) {
		ItemStack enchantedStack = inventory.getStack(0);
		args.set(0, (BiConsumer<World, BlockPos>) (world, pos) -> {
			int power = EasyMagic.CONFIG.get(ServerConfig.class).maxEnchantingPower == 0 ? 15 : getEnchantingPower(world, pos) * 15 / EasyMagic.CONFIG.get(ServerConfig.class).maxEnchantingPower;
			random.setSeed(enchantmentSeed.get());
			updateLevels(enchantedStack, world, pos, power);
			createClues(enchantedStack);
			sendContentUpdates();
			sendEnchantingData(enchantedStack, world, pos);
		});
	}

	// This is just straight up taken from the target class.
	private void sendEnchantingData(ItemStack enchantedStack, World world, BlockPos pos) {
		final ServerConfig.EnchantmentHint enchantmentHint = EasyMagic.CONFIG.get(ServerConfig.class).enchantmentHint;
		List<EnchantmentLevelEntry> firstSlotData = this.getEnchantmentHint(enchantedStack, 0, world, pos, enchantmentHint);
		List<EnchantmentLevelEntry> secondSlotData = this.getEnchantmentHint(enchantedStack, 1, world, pos, enchantmentHint);
		List<EnchantmentLevelEntry> thirdSlotData = this.getEnchantmentHint(enchantedStack, 2, world, pos, enchantmentHint);
		EasyMagic.NETWORK.sendTo(new S2CEnchantingDataMessage(this.syncId, firstSlotData, secondSlotData, thirdSlotData), (ServerPlayerEntity) this.player);
	}

	// This too, lol.
	private List<EnchantmentLevelEntry> getEnchantmentHint(ItemStack enchantedItem, int enchantSlot, World world, BlockPos pos, ServerConfig.EnchantmentHint enchantmentHint) {
		List<BlockPos> boosterPositions = EnchantingUtil.search(world, pos);

		return switch (enchantmentHint) {
			case NONE -> Lists.newArrayList();
			case SINGLE -> {
				List<EnchantmentLevelEntry> enchantmentData = EnchantingUtil.generateEnchantments(
						enchantedItem,
						enchantSlot,
						enchantmentPower[enchantSlot],
						random,
						enchantmentSeed.get(),
						world,
						boosterPositions
				);
				if (enchantmentData.isEmpty()) yield Lists.newArrayList();
				yield Lists.newArrayList(enchantmentData.get(this.random.nextInt(enchantmentData.size())));
			}
			case ALL -> EnchantingUtil.generateEnchantments(
					enchantedItem,
					enchantSlot,
					enchantmentPower[enchantSlot],
					random,
					enchantmentSeed.get(),
					world,
					boosterPositions
			);
		};
	}
}
