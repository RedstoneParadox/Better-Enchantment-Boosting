package io.github.redstoneparadox.betterenchantmentboosting;

import io.github.redstoneparadox.betterenchantmentboosting.booster.ActivationCountEnchantingBooster;
import io.github.redstoneparadox.betterenchantmentboosting.booster.ActivationEnchantingBooster;
import io.github.redstoneparadox.betterenchantmentboosting.booster.InventoryEnchantingBooster;
import io.github.redstoneparadox.betterenchantmentboosting.config.BetterEnchantmentBoostingConfig;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;

public final class BetterEnchantmentBoosting implements ModInitializer {
	public static final String MODID = "betterenchantmentboosting";
	public static final BetterEnchantmentBoostingConfig CONFIG = BetterEnchantmentBoostingConfig.load();
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@Override
	public void onInitialize(ModContainer mod) {
		BlockContentRegistries.ENCHANTING_BOOSTERS.put(
				TagKey.of(Registries.BLOCK.getKey(), new Identifier(MODID, "groupable_candles")),
				new ActivationCountEnchantingBooster("lit", "candles", 0.25f)
		);
		BlockContentRegistries.ENCHANTING_BOOSTERS.put(
				TagKey.of(Registries.BLOCK.getKey(), new Identifier("minecraft:candle_cakes")),
				new ActivationEnchantingBooster("lit", 0.25f)
		);
		BlockContentRegistries.ENCHANTING_BOOSTERS.put(
				Blocks.CHISELED_BOOKSHELF,
				new InventoryEnchantingBooster(1.0f)
		);
	}
}
