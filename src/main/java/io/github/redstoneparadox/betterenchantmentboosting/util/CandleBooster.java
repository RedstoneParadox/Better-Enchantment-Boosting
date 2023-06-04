package io.github.redstoneparadox.betterenchantmentboosting.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosterType;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosters;

public record CandleBooster(float powerPerCandle) implements EnchantingBooster {
	public static final Codec<CandleBooster> CODEC = RecordCodecBuilder.create(instance ->
					instance.group(
							Codec.FLOAT.fieldOf("power_per_candle").forGetter(CandleBooster::powerPerCandle)
					).apply(instance, CandleBooster::new)
			);
	public static final EnchantingBoosterType TYPE = EnchantingBoosters.register(
			new Identifier(BetterEnchantmentBoosting.MODID, "candle_booster"),
			CODEC
	);


	@Override
	public float getEnchantingBoost(World world, BlockState state, BlockPos pos) {
		if (!state.contains(Properties.LIT)) return 0;

		if (state.contains(Properties.CANDLES)) {
			return powerPerCandle * state.get(Properties.CANDLES);
		}

		return powerPerCandle; // Assume that it's a single-candle block
	}

	@Override
	public EnchantingBoosterType getType() {
		return TYPE;
	}
}
