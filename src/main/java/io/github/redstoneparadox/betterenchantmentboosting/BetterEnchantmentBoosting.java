package io.github.redstoneparadox.betterenchantmentboosting;

import io.github.redstoneparadox.betterenchantmentboosting.config.BetterEnchantmentBoostingConfig;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentPowerRegistry;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchUtil;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;

public class BetterEnchantmentBoosting implements ModInitializer {
	public static final String MODID = "betterenchatnmentboosting";
	public static final BetterEnchantmentBoostingConfig CONFIG = BetterEnchantmentBoostingConfig.load();

	@Override
	public void onInitialize() {
		EnchantmentPowerRegistry.register(Blocks.BOOKSHELF.getDefaultState(), 1.0f);
		EnchantmentPowerRegistry.registerDeferred((registryFunction -> {
			for (Block block: Registry.BLOCK.stream().toList()) {
				BlockState state = block.getDefaultState();

				if (state.isIn(SearchUtil.Tags.BOOKSHELVES)) {
					registryFunction.register(state, 1.0);
				} else if (state.isIn(BlockTags.CANDLES) && block instanceof CandleBlock && CONFIG.candleBoosting()) {
					registryFunction.register(
							block
									.getDefaultState()
									.with(CandleBlock.CANDLES, 4)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 4
					);
					registryFunction.register(
							block
									.getDefaultState()
									.with(CandleBlock.CANDLES, 3)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 3
					);
					registryFunction.register(
							block
									.getDefaultState()
									.with(CandleBlock.CANDLES, 2)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 2
					);
					registryFunction.register(
							block
									.getDefaultState()
									.with(CandleBlock.CANDLES, 1)
									.with(CandleBlock.LIT, true),
							CONFIG.powerPerCandle() * 1
					);
				}
			}
		}));
	}

}
