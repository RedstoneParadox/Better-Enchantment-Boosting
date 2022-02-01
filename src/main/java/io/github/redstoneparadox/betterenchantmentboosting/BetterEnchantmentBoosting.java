package io.github.redstoneparadox.betterenchantmentboosting;

import io.github.redstoneparadox.betterenchantmentboosting.config.BetterEnchantmentBoostingConfig;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentPowerRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class BetterEnchantmentBoosting implements ModInitializer {
	public static final String MODID = "betterenchatnmentboosting";
	public static final BetterEnchantmentBoostingConfig CONFIG = BetterEnchantmentBoostingConfig.load();

	@Override
	public void onInitialize() {
		EnchantmentPowerRegistry.register(Blocks.BOOKSHELF.getDefaultState(), 1.0f);
		EnchantmentPowerRegistry.register(Blocks.CANDLE.getDefaultState().with(CandleBlock.CANDLES, 4), 1.0f);

		Tag<Block> bookshelves = BlockTags.getTagGroup().getTag(new Identifier("c:bookshelves"));

		if (bookshelves != null) {
			for (Block bookshelf: bookshelves.values()) {
				BlockState state = bookshelf.getDefaultState();

				if (!EnchantmentPowerRegistry.isRegistered(state)) {
					EnchantmentPowerRegistry.register(state, 1.0);
				}
			}
		}

		if (CONFIG.candleBoosting()) {
			Tag<Block> candles = BlockTags.CANDLES;

			for (Block candleBlock: candles.values()) {
				if (candleBlock instanceof CandleBlock) {
					EnchantmentPowerRegistry.register(
							candleBlock
									.getDefaultState()
									.with(CandleBlock.CANDLES, 4)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 4
					);
					EnchantmentPowerRegistry.register(
							candleBlock
									.getDefaultState()
									.with(CandleBlock.CANDLES, 3)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 3
					);
					EnchantmentPowerRegistry.register(
							candleBlock
									.getDefaultState()
									.with(CandleBlock.CANDLES, 2)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 2
					);
					EnchantmentPowerRegistry.register(
							candleBlock
									.getDefaultState()
									.with(CandleBlock.CANDLES, 1)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 1
					);
				}
			}
		}
	}

	public static class Tags {
		public static final Tag<Block> NON_BOOKSHELF_BLOCKING = BlockTags.getTagGroup().getTag(new Identifier(MODID, "non_bookshelf_blocking"));
	}
}
