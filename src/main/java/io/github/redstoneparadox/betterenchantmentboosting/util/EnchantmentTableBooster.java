package io.github.redstoneparadox.betterenchantmentboosting.util;

import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public interface EnchantmentTableBooster {
	double getPower(World world, BlockState state);
}
