package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantingUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableBlockMixin {
	@Inject(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockWithEntity;randomDisplayTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/random/RandomGenerator;)V"), cancellable = true)
	private void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random, CallbackInfo ci) {
		List<BlockPos> boosterPositions = EnchantingUtil.search(world, pos);

		for (BlockPos boosterPosition: boosterPositions) {
			BlockState boosterState = world.getBlockState(boosterPosition);
			Optional<EnchantingBooster> booster = BlockContentRegistries.ENCHANTING_BOOSTERS.get(boosterState.getBlock());

			if (booster.isEmpty()) continue;
			if (booster.get().getEnchantingBoost(world, boosterState, boosterPosition) == 0) continue;
			if (random.nextInt(16) != 0) continue;

			int i = boosterPosition.getX() - pos.getX();
			int k = boosterPosition.getY() - pos.getY();
			int j = boosterPosition.getZ() - pos.getZ();

			world.addParticle(
					ParticleTypes.ENCHANT,
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 2.0,
					(double)pos.getZ() + 0.5,
					(double)((float)i + random.nextFloat()) - 0.5,
					(float)k - random.nextFloat() - 1.0f,
					(double)((float)j + random.nextFloat()) - 0.5
			);
		}

		ci.cancel();
	}
}
