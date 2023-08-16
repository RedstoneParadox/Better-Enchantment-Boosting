package io.github.redstoneparadox.betterenchantmentboosting.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosterType;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosters;

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
		BooleanProperty activation = (BooleanProperty) state
				.getBlock()
				.getStateManager()
				.getProperties()
				.stream()
				.filter(property2 -> property2.getName().equals(activationProperty))
				.toList()
				.get(0);
		IntProperty count = (IntProperty) state
				.getBlock()
				.getStateManager()
				.getProperties()
				.stream()
				.filter(property2 -> property2.getName().equals(countProperty))
				.toList()
				.get(0);

		if (state.get(activation)) {
			return power * state.get(count);
		}

		return 0;
	}

	@Override
	public EnchantingBoosterType getType() {
		return TYPE;
	}
}
