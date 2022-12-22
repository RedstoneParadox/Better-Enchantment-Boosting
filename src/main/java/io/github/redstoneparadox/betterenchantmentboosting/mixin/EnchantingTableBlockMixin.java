package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentTableBooster;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableBlockMixin {
	@Inject(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockWithEntity;randomDisplayTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/random/RandomGenerator;)V"), cancellable = true)
	private void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random, CallbackInfo ci) {
		List<BlockPos> bookshelfPositions = SearchUtil.search(world, pos);
		double power = EnchantmentTableBooster.getPower(world, bookshelfPositions);

		if (power < 1.0) {
			ci.cancel();
			return;
		}

		for (BlockPos bookshelfPos: bookshelfPositions) {
			if (random.nextInt(16) != 0) continue;

			int i = bookshelfPos.getX() - pos.getX();
			int k = bookshelfPos.getY() - pos.getY();
			int j = bookshelfPos.getZ() - pos.getZ();

			world.addParticle(
					ParticleTypes.ENCHANT,
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 2.0,
					(double)pos.getZ() + 0.5,
					(double)((float)i + random.nextFloat()) - 0.5,
					(float)k - random.nextFloat() - 1.0f,
					(double)((float)j + random.nextFloat()) - 0.5);
		}

		ci.cancel();
	}
}
