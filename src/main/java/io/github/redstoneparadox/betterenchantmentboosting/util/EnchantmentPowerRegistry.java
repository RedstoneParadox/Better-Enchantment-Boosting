package io.github.redstoneparadox.betterenchantmentboosting.util;

import net.minecraft.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentPowerRegistry {
	private static final Map<BlockState, Double> REGISTRY = new HashMap<>();

	public static void register(BlockState state, double power) {
		REGISTRY.put(state, power);
	}

	public static double getPower(BlockState state) {
		if (REGISTRY.containsKey(state)) return REGISTRY.get(state);
		return 0;
	}

	public static boolean isRegistered(BlockState state) {
		return REGISTRY.containsKey(state);
	}
}
