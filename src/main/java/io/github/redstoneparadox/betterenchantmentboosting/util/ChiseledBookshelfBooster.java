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

public record ChiseledBookshelfBooster(float fullPower) implements EnchantingBooster {
	public static final Codec<ChiseledBookshelfBooster> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.FLOAT.fieldOf("full_power").forGetter(ChiseledBookshelfBooster::fullPower)
			).apply(instance, ChiseledBookshelfBooster::new)
	);
	public static final EnchantingBoosterType TYPE = EnchantingBoosters.register(
			new Identifier(BetterEnchantmentBoosting.MODID, "chiseled_bookshelf"),
			CODEC
	);
	@Override
	public float getEnchantingBoost(World world, BlockState state, BlockPos pos) {
		if (!state.contains(Properties.SLOT_0_OCCUPIED) ||
				!state.contains(Properties.SLOT_1_OCCUPIED) ||
				!state.contains(Properties.SLOT_2_OCCUPIED) ||
				!state.contains(Properties.SLOT_3_OCCUPIED) ||
				!state.contains(Properties.SLOT_4_OCCUPIED) ||
				!state.contains(Properties.SLOT_5_OCCUPIED)
		) return 0;

		int filledSlots = 0;
		if (state.get(Properties.SLOT_0_OCCUPIED)) filledSlots += 1;
		if (state.get(Properties.SLOT_1_OCCUPIED)) filledSlots += 1;
		if (state.get(Properties.SLOT_2_OCCUPIED)) filledSlots += 1;
		if (state.get(Properties.SLOT_3_OCCUPIED)) filledSlots += 1;
		if (state.get(Properties.SLOT_4_OCCUPIED)) filledSlots += 1;
		if (state.get(Properties.SLOT_5_OCCUPIED)) filledSlots += 1;

		if (filledSlots == 0) return 0;

		return fullPower * ((float) filledSlots /6);
	}

	@Override
	public EnchantingBoosterType getType() {
		return TYPE;
	}
}
