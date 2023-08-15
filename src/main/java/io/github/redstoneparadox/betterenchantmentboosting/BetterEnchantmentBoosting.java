package io.github.redstoneparadox.betterenchantmentboosting;

import io.github.redstoneparadox.betterenchantmentboosting.config.BetterEnchantmentBoostingConfig;
import io.github.redstoneparadox.betterenchantmentboosting.data.ActivationEnchantingBooster;
import io.github.redstoneparadox.betterenchantmentboosting.util.CandleBooster;
import io.github.redstoneparadox.betterenchantmentboosting.util.ChiseledBookshelfBooster;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosterType;

public final class BetterEnchantmentBoosting implements ModInitializer {
	public static final String MODID = "betterenchantmentboosting";
	public static final BetterEnchantmentBoostingConfig CONFIG = BetterEnchantmentBoostingConfig.load();

	@Override
	public void onInitialize(ModContainer mod) {
		BlockContentRegistries.ENCHANTING_BOOSTERS.put(
				TagKey.of(Registries.BLOCK.getKey(), new Identifier("minecraft:candles")),
				new CandleBooster(0.25f)
		);
		BlockContentRegistries.ENCHANTING_BOOSTERS.put(
				TagKey.of(Registries.BLOCK.getKey(), new Identifier("minecraft:candle_cakes")),
				new ActivationEnchantingBooster("lit", 0.25f)
		);
		BlockContentRegistries.ENCHANTING_BOOSTERS.put(
				Blocks.CHISELED_BOOKSHELF,
				new ChiseledBookshelfBooster(1.0f)
		);
	}
}
