package io.github.redstoneparadox.betterenchantmentboosting.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BoundsConfig(int distance, int height, int depth) {
	public static final Codec<BoundsConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							Codec.INT.fieldOf("distance").forGetter(config -> config.distance),
							Codec.INT.fieldOf("height").forGetter(config -> config.height),
							Codec.INT.fieldOf("depth").forGetter(config -> config.depth)
					)
					.apply(instance, BoundsConfig::new)
	);
}
