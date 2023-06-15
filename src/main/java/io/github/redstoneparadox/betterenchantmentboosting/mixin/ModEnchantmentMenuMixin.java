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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(value = ModEnchantmentMenu.class, remap = false)
public abstract class ModEnchantmentMenuMixin extends EnchantmentScreenHandler {
	@Shadow
	@Final
	private RandomGenerator random;
	@Shadow
	@Final
	private PlayerEntity player;

	public ModEnchantmentMenuMixin(int syncId, PlayerInventory playerInventory) {
		super(syncId, playerInventory);
	}

	@Inject(method = "getEnchantingPower", at = @At("HEAD"), cancellable = true)
	private void getEnchantingPower(World level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		List<BlockPos> boosterPositions = EnchantingUtil.search(level, pos);
		double power = EnchantingUtil.getPower(level, boosterPositions);

		cir.setReturnValue((int) Math.round(power));
	}

	@Redirect(method = "lambda$slotsChanged$2", at = @At(value = "INVOKE", target = "Lfuzs/easymagic/world/inventory/ModEnchantmentMenu;sendEnchantingData(Lnet/minecraft/item/ItemStack;)V"))
	private void slotsChangedLambdaRedirect(ModEnchantmentMenu instance, ItemStack enchantedItem, ItemStack stack, World world, BlockPos pos) {
		sendEnchantingData(enchantedItem, world, pos);
	}

	// This is just straight up taken from the target class.
	private void sendEnchantingData(ItemStack enchantedItem, World world, BlockPos pos) {
		final ServerConfig.EnchantmentHint enchantmentHint = EasyMagic.CONFIG.get(ServerConfig.class).enchantmentHint;
		List<EnchantmentLevelEntry> firstSlotData = this.getEnchantmentHint(enchantedItem, 0, world, pos, enchantmentHint);
		List<EnchantmentLevelEntry> secondSlotData = this.getEnchantmentHint(enchantedItem, 1, world, pos, enchantmentHint);
		List<EnchantmentLevelEntry> thirdSlotData = this.getEnchantmentHint(enchantedItem, 2, world, pos, enchantmentHint);
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
						seed.get(),
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
					seed.get(),
					world,
					boosterPositions
			);
		};
	}
}
