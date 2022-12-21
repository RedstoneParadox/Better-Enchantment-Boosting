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
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public record BetterEnchantmentBoostingConfig(boolean candleBoosting, double powerPerCandle, BoundsConfig bounds) {
	public static final Codec<BetterEnchantmentBoostingConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							Codec.BOOL.fieldOf("candle_boosting").forGetter(config -> config.candleBoosting),
							Codec.DOUBLE.fieldOf("power_per_candle").forGetter(config -> config.powerPerCandle),
							BoundsConfig.CODEC.fieldOf("bounds").forGetter(config -> config.bounds)
					)
					.apply(instance, BetterEnchantmentBoostingConfig::new)

	);

	public static BetterEnchantmentBoostingConfig load() {
		File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "betterenchantmentboosting.json");

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

		var bounds = new BoundsConfig(3, 2, -1);
		var config = new BetterEnchantmentBoostingConfig(false, 0.25, bounds);

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
