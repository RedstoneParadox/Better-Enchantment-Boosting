package io.github.redstoneparadox.betterenchantmentboosting.booster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosterType;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosters;

import java.util.List;

public record ActivationEnchantingBooster(String activationProperty, float power) implements EnchantingBooster {
	public static final Codec<ActivationEnchantingBooster> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						Codec.STRING.fieldOf("activation_property").forGetter(ActivationEnchantingBooster::activationProperty),
						Codec.FLOAT.fieldOf("power").forGetter(ActivationEnchantingBooster::power)
				).apply(instance, ActivationEnchantingBooster::new)
			);
	public static final EnchantingBoosterType TYPE = EnchantingBoosters.register(
			new Identifier(BetterEnchantmentBoosting.MODID, "activation"),
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

		if (properties.isEmpty()) {
			String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
			BetterEnchantmentBoosting.LOGGER.error("Activation enchanting booster '" + blockID + "' does not have property '" + activationProperty + "'!");
			return 0;
		}

		Property<?> property = properties.get(0);

		if (!(property instanceof BooleanProperty boolProperty)) {
			String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
			BetterEnchantmentBoosting.LOGGER.error("Property '" + activationProperty + "' for activation enchanting booster '" + blockID + "' is not a boolean property!");
			return 0;
		}

		if (state.get(boolProperty)) {
			return power;
		}

		return 0;
	}

	@Override
	public EnchantingBoosterType getType() {
		return TYPE;
	}
}
