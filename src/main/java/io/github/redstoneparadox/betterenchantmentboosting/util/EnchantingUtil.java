package io.github.redstoneparadox.betterenchantmentboosting.util;

import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;

import java.util.List;
import java.util.Optional;

public class EnchantingUtil {
	private static final TagKey<Block> ENCHANTMENT_POWER_TRANSMITTER = TagKey.of(Registries.BLOCK.getKey(), new Identifier("minecraft:enchantment_power_transmitter"));

	public static List<BlockPos> search(World world, BlockPos origin) {
		int distance = BetterEnchantmentBoosting.CONFIG.bounds().distance();
		int height = BetterEnchantmentBoosting.CONFIG.bounds().height();
		int depth = BetterEnchantmentBoosting.CONFIG.bounds().depth();
		Box bounds = new Box(origin.add(-distance, depth, -distance), origin.add(distance, height, distance));
		SearchArea area = new SearchArea();

		area.setGrowthPredicate(state ->
				state.isIn(ENCHANTMENT_POWER_TRANSMITTER)
		);
		area.setSearchPredicate(state ->
				BlockContentRegistries.ENCHANTING_BOOSTERS.get(state.getBlock()).isPresent()
		);

		return area.search(world, origin, bounds);
	}

	public static double getPower(World world, List<BlockPos> positions) {
		double power = 0;

		for (BlockPos bookshelfPos: positions) {
			BlockState state2 = world.getBlockState(bookshelfPos);
			Optional<EnchantingBooster> booster = BlockContentRegistries.ENCHANTING_BOOSTERS.get(state2.getBlock());

			if (booster.isPresent()) {
				power += booster.get().getEnchantingBoost(world, state2, bookshelfPos);
			}
		}

		return power;
	}
}