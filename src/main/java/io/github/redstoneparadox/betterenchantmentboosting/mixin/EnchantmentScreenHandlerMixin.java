package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantingUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {
	@Unique ItemStack onContentChangedStack = ItemStack.EMPTY;
	@Shadow @Final private RandomGenerator random;
	@Shadow @Final private Property seed;

	@Shadow
	protected abstract List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level);

	@Inject(method = "onContentChanged", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
	private void onContentChanged_GetStack(Inventory inventory, CallbackInfo ci) {
		onContentChangedStack = inventory.getStack(0);
	}

	@ModifyArgs(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
	private void onContentChanged_CallRun(Args args) {
		args.set(0, (BiConsumer<World, BlockPos>) this::bookshelfSearch);
	}

	private void bookshelfSearch(World world, BlockPos pos) {
		EnchantmentScreenHandler self = (EnchantmentScreenHandler)(Object) this;
		ItemStack stack = onContentChangedStack;
		onContentChangedStack = ItemStack.EMPTY;

		List<BlockPos> boosterPositions = EnchantingUtil.search(world, pos);
		double power = EnchantingUtil.getPower(world, boosterPositions);

		random.setSeed(seed.get());
		for (int slot = 0; slot < 3; ++slot) {
			self.enchantmentPower[slot] = EnchantmentHelper.calculateRequiredExperienceLevel(random, slot, (int) power, stack);
			self.enchantmentId[slot] = -1;
			self.enchantmentLevel[slot] = -1;
			if (self.enchantmentPower[slot] >= slot + 1) continue;
			self.enchantmentPower[slot] = 0;
		}
		for (int slot = 0; slot < 3; ++slot) {
			List<EnchantmentLevelEntry> list = EnchantingUtil.generateEnchantments(
					stack,
					slot,
					self.enchantmentPower[slot],
					random,
					seed.get(),
					world,
					boosterPositions
			);
			if (self.enchantmentPower[slot] <= 0 || list == null || list.isEmpty()) continue;
			EnchantmentLevelEntry enchantmentLevelEntry = list.get(random.nextInt(list.size()));
			self.enchantmentId[slot] = Registries.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment);
			self.enchantmentLevel[slot] = enchantmentLevelEntry.level;
		}
		self.sendContentUpdates();
	}

	private List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level, List<BlockPos> boosterPositions) {
		random.setSeed(seed.get() + slot);
		List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, stack, level, false);
		if (stack.isOf(Items.BOOK) && list.size() > 1) {
			list.remove(random.nextInt(list.size()));
		}

		return list;
	}
}
