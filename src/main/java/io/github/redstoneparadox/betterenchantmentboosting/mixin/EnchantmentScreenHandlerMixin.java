package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.search.SearchArea;
import net.minecraft.block.AbstractBlock;
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

		SearchArea area = new SearchArea();
		Box bounds = new Box(pos.add(-3, -1, -3), pos.add(3, 2, 3));
		area.setGrowthPredicate(AbstractBlock.AbstractBlockState::isAir);
		area.setSearchPredicate(state -> state.isOf(Blocks.BOOKSHELF));
		List<BlockPos> bookshelvesList = area.search(world, pos, bounds);
		int bookshelves = bookshelvesList.size();

		random.setSeed(seed.get());
		for (int j = 0; j < 3; ++j) {
			self.enchantmentPower[j] = EnchantmentHelper.calculateRequiredExperienceLevel(random, j, bookshelves, stack);
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