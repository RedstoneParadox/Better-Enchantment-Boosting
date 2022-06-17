package io.github.redstoneparadox.betterenchantmentboosting.util;

import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class SearchUtil {
	public static List<BlockPos> search(World world, BlockPos origin) {
		int distance = BetterEnchantmentBoosting.CONFIG.bounds().distance();
		int height = BetterEnchantmentBoosting.CONFIG.bounds().height();
		int depth = BetterEnchantmentBoosting.CONFIG.bounds().depth();
		Box bounds = new Box(origin.add(-distance, depth, -distance), origin.add(distance, height, distance));
		SearchArea area = new SearchArea();

		area.setGrowthPredicate(state -> state.isIn(Tags.NON_BOOKSHELF_BLOCKING));
		area.setSearchPredicate(state -> state.isIn(Tags.ENCHANTMENT_BOOSTING));

		return area.search(world, origin, bounds);
	}

	public static class Tags {
		public static final TagKey<Block> ENCHANTMENT_BOOSTING = TagKey.of(Registry.BLOCK_KEY, new Identifier(BetterEnchantmentBoosting.MODID, "enchantment_boosting"));
		public static final TagKey<Block> NON_BOOKSHELF_BLOCKING = TagKey.of(Registry.BLOCK_KEY, new Identifier(BetterEnchantmentBoosting.MODID, "non_bookshelf_blocking"));
	}
}
