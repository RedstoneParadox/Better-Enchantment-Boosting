package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentPowerRegistry;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchArea;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
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
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {
	@Unique ItemStack onContentChangedStack = ItemStack.EMPTY;
	@Shadow @Final private Random random;
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

		int distance = BetterEnchantmentBoosting.CONFIG.bounds().distance();
		int height = BetterEnchantmentBoosting.CONFIG.bounds().height();
		int depth = BetterEnchantmentBoosting.CONFIG.bounds().depth();
		SearchArea area = new SearchArea();
		Box bounds = new Box(pos.add(-distance, depth, -distance), pos.add(distance, height, distance));
		area.setGrowthPredicate(state -> state.isIn(BetterEnchantmentBoosting.Tags.NON_BOOKSHELF_BLOCKING));
		area.setSearchPredicate(EnchantmentPowerRegistry::isRegistered);
		List<BlockPos> bookshelfPositions = area.search(world, pos, bounds);
		float power = 0;

		for (BlockPos bookshelfPos: bookshelfPositions) {
			BlockState state = world.getBlockState(bookshelfPos);
			power += EnchantmentPowerRegistry.getPower(state);
		}

		random.setSeed(seed.get());
		for (int j = 0; j < 3; ++j) {
			self.enchantmentPower[j] = EnchantmentHelper.calculateRequiredExperienceLevel(random, j, (int) power, stack);
			self.enchantmentId[j] = -1;
			self.enchantmentLevel[j] = -1;
			if (self.enchantmentPower[j] >= j + 1) continue;
			self.enchantmentPower[j] = 0;
		}
		for (int j = 0; j < 3; ++j) {
			List<EnchantmentLevelEntry> list;
			if (self.enchantmentPower[j] <= 0 || (list = generateEnchantments(stack, j, self.enchantmentPower[j])) == null || list.isEmpty()) continue;
			EnchantmentLevelEntry enchantmentLevelEntry = list.get(random.nextInt(list.size()));
			self.enchantmentId[j] = Registry.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment);
			self.enchantmentLevel[j] = enchantmentLevelEntry.level;
		}
		self.sendContentUpdates();
	}
}
