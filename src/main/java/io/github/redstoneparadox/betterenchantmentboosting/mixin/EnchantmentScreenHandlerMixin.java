package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentPowerRegistry;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchArea;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Random;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {
	@Shadow @Final private Random random;
	@Shadow @Final private Property seed;

	@Shadow
	protected abstract List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level);

	/**
	 * @author RedstoneParadox
	 *
	 * @reason No good way to just inject this code.
	 */
	@Overwrite
	private void method_17411(ItemStack stack, World world, BlockPos pos) {
		EnchantmentScreenHandler self = (EnchantmentScreenHandler)(Object) this;

		int distance = BetterEnchantmentBoosting.CONFIG.bounds().distance();
		int height = BetterEnchantmentBoosting.CONFIG.bounds().height();
		int depth = BetterEnchantmentBoosting.CONFIG.bounds().depth();
		SearchArea area = new SearchArea();
		Box bounds = new Box(pos.add(-distance, depth, -distance), pos.add(distance, height, distance));
		area.setGrowthPredicate(AbstractBlock.AbstractBlockState::isAir);
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
