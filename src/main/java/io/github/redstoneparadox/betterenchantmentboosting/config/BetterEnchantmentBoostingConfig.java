package io.github.redstoneparadox.betterenchantmentboosting.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public record BetterEnchantmentBoostingConfig(EnchantmentInfluencingConfig influencingConfig, SearchAreaConfig searchAreaConfig) {
	public static final Codec<BetterEnchantmentBoostingConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							EnchantmentInfluencingConfig.CODEC.fieldOf("enchantment_influencing").forGetter(config -> config.influencingConfig),
							SearchAreaConfig.CODEC.fieldOf("search_area").forGetter(config -> config.searchAreaConfig)
					)
					.apply(instance, BetterEnchantmentBoostingConfig::new)

	);

	public record EnchantmentInfluencingConfig(boolean enabled, boolean allowTreasure, boolean allowCurses) {
		public static final Codec<EnchantmentInfluencingConfig> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.BOOL.fieldOf("enabled").forGetter(config -> config.enabled),
						Codec.BOOL.fieldOf("allow_treasure").forGetter(config -> config.allowTreasure),
						Codec.BOOL.fieldOf("allow_curses").forGetter(config -> config.allowCurses)
				)
						.apply(instance, EnchantmentInfluencingConfig::new)
		);
	}

	public record SearchAreaConfig(int horizontalBlocksFromTable, int blocksAboveTable, int blocksBelowTable) {
		public static final Codec<SearchAreaConfig> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
								Codec.INT.fieldOf("horizontal_blocks_from_table").forGetter(config -> config.horizontalBlocksFromTable),
								Codec.INT.fieldOf("blocks_above_table").forGetter(config -> config.blocksAboveTable),
								Codec.INT.fieldOf("blocks_below_table").forGetter(config -> config.blocksBelowTable)
						)
						.apply(instance, (distance, height, depth) ->
								new SearchAreaConfig(Math.abs(distance), Math.abs(height), Math.abs(depth))
						)
		);
	}

	public static BetterEnchantmentBoostingConfig load() {
		File file = new File(QuiltLoader.getConfigDir().toFile(), "betterenchantmentboosting.json");

		if (file.exists()) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(Files.asCharSource(file, Charsets.UTF_8).read()));
				jsonReader.setLenient(true);
				Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonReader));
				var result = CODEC.decode(dynamic).result();

				jsonReader.close();

				if (result.isPresent()) {
					return result.get().getFirst();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		var influencing = new EnchantmentInfluencingConfig(false, false, false);
		var bounds = new SearchAreaConfig(3, 2, -1);
		var config = new BetterEnchantmentBoostingConfig(influencing, bounds);

		var result = CODEC.encodeStart(JsonOps.INSTANCE, config).result();

		result.ifPresent(json -> {
			try {
				var exists = true;

				if (!file.exists()) {
					exists = file.createNewFile();
				}

				if (exists) {
					var jsonWriter = new JsonWriter(new BufferedWriter(new FileWriter(file)));
					jsonWriter.setLenient(true);
					Streams.write(json, jsonWriter);
					jsonWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		return config;
	}
}
