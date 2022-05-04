package io.github.redstoneparadox.betterenchantmentboosting.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.TagKey;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentPowerRegistry {
	private static final Map<BlockState, Double> STATE_REGISTRY = new HashMap<>();
	private static final Map<TagKey<Block>, Double> TAG_REGISTRY = new HashMap<>();

	public static void registerState(BlockState state, double power) {
		STATE_REGISTRY.put(state, power);
	}

	public static void registerTag(TagKey<Block> tagKey, double power) {
		TAG_REGISTRY.put(tagKey, power);
	}

	public static double getPower(BlockState state) {
		if (STATE_REGISTRY.containsKey(state)) return STATE_REGISTRY.get(state);

		for (Map.Entry<TagKey<Block>, Double> entry: TAG_REGISTRY.entrySet()) {
			if (state.isIn(entry.getKey())) return entry.getValue();
		}

		return 0;
	}

	public static boolean isRegistered(BlockState state) {
		return STATE_REGISTRY.containsKey(state);
	}
}
