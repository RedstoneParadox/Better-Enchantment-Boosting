package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentPowerRegistry;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchArea;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
	@Inject(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockWithEntity;randomDisplayTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), cancellable = true)
	private void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
		int distance = BetterEnchantmentBoosting.CONFIG.bounds().distance();
		int height = BetterEnchantmentBoosting.CONFIG.bounds().height();
		int depth = BetterEnchantmentBoosting.CONFIG.bounds().depth();
		SearchArea area = new SearchArea();
		Box bounds = new Box(pos.add(-distance, depth, -distance), pos.add(distance, height, distance));
		area.setGrowthPredicate(state2 -> Objects.requireNonNull(BetterEnchantmentBoosting.Tags.NON_BOOKSHELF_BLOCKING).contains(state2.getBlock()));
		area.setSearchPredicate(EnchantmentPowerRegistry::isRegistered);
		List<BlockPos> bookshelfPositions = area.search(world, pos, bounds);

		float power = 0;

		for (BlockPos bookshelfPos: bookshelfPositions) {
			BlockState state2 = world.getBlockState(bookshelfPos);
			power += EnchantmentPowerRegistry.getPower(state2);
		}

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
