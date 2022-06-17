package io.github.redstoneparadox.betterenchantmentboosting.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface EnchantmentTableBooster {
	double getPower(World world, BlockState state);

	static double getPower(World world, List<BlockPos> positions) {
		double power = 0;

		for (BlockPos bookshelfPos: positions) {
			BlockState state2 = world.getBlockState(bookshelfPos);
			if (state2.getBlock() instanceof EnchantmentTableBooster) {
				power += ((EnchantmentTableBooster)state2.getBlock()).getPower(world, state2);
			}
		}

		return power;
	}
}
