package io.github.redstoneparadox.betterenchantmentboosting.booster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosterType;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosters;

import java.util.Collection;
import java.util.List;

public record ActivationCountEnchantingBooster(String activationProperty, String countProperty, float power) implements EnchantingBooster {
	public static final Codec<ActivationCountEnchantingBooster> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						Codec.STRING.fieldOf("activation_property").forGetter(ActivationCountEnchantingBooster::activationProperty),
						Codec.STRING.fieldOf("count_property").forGetter(ActivationCountEnchantingBooster::countProperty),
						Codec.FLOAT.fieldOf("power").forGetter(ActivationCountEnchantingBooster::power)
				).apply(instance, ActivationCountEnchantingBooster::new)
			);
	public static final EnchantingBoosterType TYPE = EnchantingBoosters.register(
			new Identifier(BetterEnchantmentBoosting.MODID, "activation_count"),
			CODEC
	);

	@Override
	public float getEnchantingBoost(World world, BlockState state, BlockPos pos) {
		List<Property<?>> properties = state
				.getBlock()
				.getStateManager()
				.getProperties()
				.stream()
				.filter(property2 -> property2.getName().equals(activationProperty))
				.toList();
		List<Property<?>> properties2 = state
				.getBlock()
				.getStateManager()
				.getProperties()
				.stream()
				.filter(property2 -> property2.getName().equals(countProperty))
				.toList();

		if (properties.isEmpty()) {
			String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
			BetterEnchantmentBoosting.LOGGER.error("Enchanting booster '" + blockID + "' does not have property '" + activationProperty + "'!");
			return 0;
		}

		if (properties2.isEmpty()) {
			String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
			BetterEnchantmentBoosting.LOGGER.error("Enchanting booster '" + blockID + "' does not have property '" + countProperty + "'!");
			return 0;
		}

		Property<?> property = properties.get(0);

		if (!(property instanceof BooleanProperty boolProperty)) {
			String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
			BetterEnchantmentBoosting.LOGGER.error("Property '" + activationProperty + "' for enchanting booster '" + blockID + "' is not a boolean property!");
			return 0;
		}

		Property<?> property2 = properties2.get(0);

		if (!(property2 instanceof IntProperty intProperty)) {
			String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
			BetterEnchantmentBoosting.LOGGER.error("Property '" + countProperty + "' for enchanting booster '" + blockID + "' is not an int property!");
			return 0;
		}

		if (state.get(boolProperty)) {
			List<Integer> values = intProperty.getValues().stream().sorted().toList();
			int min = values.get(0);
			int max = values.get(values.size() - 1);
			int value = state.get(intProperty);

			return power * (value - min + 1)/(max - min + 1);
		}

		return 0;
	}

	@Override
	public EnchantingBoosterType getType() {
		return TYPE;
	}
}
