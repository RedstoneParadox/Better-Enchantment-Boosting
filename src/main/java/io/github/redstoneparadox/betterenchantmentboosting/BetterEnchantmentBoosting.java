package io.github.redstoneparadox.betterenchantmentboosting;

import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentPowerRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;

public class BetterEnchantmentBoosting implements ModInitializer {
	@Override
	public void onInitialize() {
		EnchantmentPowerRegistry.register(Blocks.BOOKSHELF.getDefaultState(), 1.0f);
		EnchantmentPowerRegistry.register(Blocks.CANDLE.getDefaultState().with(CandleBlock.CANDLES, 4), 1.0f);

		Block[] candleBlocks = new Block[] {
				Blocks.CANDLE,
				Blocks.WHITE_CANDLE,
				Blocks.ORANGE_CANDLE,
				Blocks.MAGENTA_CANDLE,
				Blocks.LIGHT_BLUE_CANDLE,
				Blocks.YELLOW_CANDLE,
				Blocks.LIME_CANDLE,
				Blocks.PINK_CANDLE,
				Blocks.GRAY_CANDLE,
				Blocks.LIGHT_GRAY_CANDLE,
				Blocks.CYAN_CANDLE,
				Blocks.PURPLE_CANDLE,
				Blocks.BLUE_CANDLE,
				Blocks.BROWN_CANDLE,
				Blocks.GREEN_CANDLE,
				Blocks.RED_CANDLE,
				Blocks.BLACK_CANDLE
		};

		for (Block candleBlock: candleBlocks) {
			EnchantmentPowerRegistry.register(
					candleBlock
							.getDefaultState()
							.with(CandleBlock.CANDLES, 4)
							.with(CandleBlock.LIT, true),
					1.0f
			);
			EnchantmentPowerRegistry.register(
					candleBlock
							.getDefaultState()
							.with(CandleBlock.CANDLES, 3)
							.with(CandleBlock.LIT, true),
					0.75f
			);
			EnchantmentPowerRegistry.register(
					candleBlock
							.getDefaultState()
							.with(CandleBlock.CANDLES, 2)
							.with(CandleBlock.LIT, true),
					0.5f
			);
			EnchantmentPowerRegistry.register(
					candleBlock
							.getDefaultState()
							.with(CandleBlock.CANDLES, 1)
							.with(CandleBlock.LIT, true),
					0.25f
			);
		}
	}
}
