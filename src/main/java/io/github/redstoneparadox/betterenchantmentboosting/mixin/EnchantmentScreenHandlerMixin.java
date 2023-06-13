package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantingUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin extends ScreenHandler {
	@Shadow @Final private RandomGenerator random;
	@Shadow @Final private Property seed;
	@Shadow @Final public int[] enchantmentPower;
	@Shadow @Final public int[] enchantmentId;
	@Shadow @Final public int[] enchantmentLevel = new int[]{-1, -1, -1};

	protected EnchantmentScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@ModifyArgs(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
	private void onContentChanged_CallRun(Args args, Inventory inventory) {
		ItemStack stack = inventory.getStack(0);
		args.set(0, (BiConsumer<World, BlockPos>) (world, pos) -> bookshelfSearch(stack, world, pos));
	}

	@Redirect(method = "method_17410", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/EnchantmentScreenHandler;generateEnchantments(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentLevelEntry> redirectGenerateEnchantmentsCall(EnchantmentScreenHandler instance, ItemStack stack, int slot, int level, ItemStack itemStack, int int1, PlayerEntity playerEntity, int int2, ItemStack itemStack2, World world, BlockPos pos) {
		List<BlockPos> boosterPositions = EnchantingUtil.search(world, pos);

		return EnchantingUtil.generateEnchantments(
				stack,
				slot,
				enchantmentPower[slot],
				random,
				seed.get(),
				world,
				boosterPositions
		);
	}

	private void bookshelfSearch(ItemStack stack, World world, BlockPos pos) {
		List<BlockPos> boosterPositions = EnchantingUtil.search(world, pos);
		double power = EnchantingUtil.getPower(world, boosterPositions);

		random.setSeed(seed.get());
		for (int slot = 0; slot < 3; ++slot) {
			enchantmentPower[slot] = EnchantmentHelper.calculateRequiredExperienceLevel(random, slot, (int) power, stack);
			enchantmentId[slot] = -1;
			enchantmentLevel[slot] = -1;
			if (enchantmentPower[slot] >= slot + 1) continue;
			enchantmentPower[slot] = 0;
		}
		for (int slot = 0; slot < 3; ++slot) {
			List<EnchantmentLevelEntry> list = EnchantingUtil.generateEnchantments(
					stack,
					slot,
					enchantmentPower[slot],
					random,
					seed.get(),
					world,
					boosterPositions
			);
			if (enchantmentPower[slot] <= 0 || list.isEmpty()) continue;
			EnchantmentLevelEntry enchantmentLevelEntry = list.get(random.nextInt(list.size()));
			enchantmentId[slot] = Registries.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment);
			enchantmentLevel[slot] = enchantmentLevelEntry.level;
		}
		sendContentUpdates();
	}
}
