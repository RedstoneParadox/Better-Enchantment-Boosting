package io.github.redstoneparadox.betterenchantmentboosting.util;

import net.minecraft.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentPowerRegistry {
	private static final Map<BlockState, Float> REGISTRY = new HashMap<>();

	public static void register(BlockState state, float power) {
		REGISTRY.put(state, power);
	}

	public static float getPower(BlockState state) {
		if (REGISTRY.containsKey(state)) return REGISTRY.get(state);
		return 0;
	}

	public static boolean isRegistered(BlockState state) {
		return REGISTRY.containsKey(state);
	}
}
