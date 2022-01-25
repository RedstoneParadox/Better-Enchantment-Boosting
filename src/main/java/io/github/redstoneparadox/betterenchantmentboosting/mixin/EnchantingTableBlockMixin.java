package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.search.SearchArea;
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
import java.util.Random;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
	@Inject(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockWithEntity;randomDisplayTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"), cancellable = true)
	private void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
		SearchArea area = new SearchArea();
		Box bounds = new Box(pos.add(-3, -1, -3), pos.add(3, 2, 3));
		area.setGrowthPredicate(AbstractBlock.AbstractBlockState::isAir);
		area.setSearchPredicate(blockState -> blockState.isOf(Blocks.BOOKSHELF));
		List<BlockPos> bookshelfPositions = area.search(world, pos, bounds);

		for (BlockPos bookshelfPos: bookshelfPositions) {
			int i = bookshelfPos.getX();
			int k = bookshelfPos.getY();
			int j = bookshelfPos.getZ();

			world.addParticle(
					ParticleTypes.ENCHANT,
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 2.0,
					(double)pos.getZ() + 0.5,
					(double)((float)i + random.nextFloat()) - 0.5,
					(float)k - random.nextFloat() - 1.0f,
					(double)((float)j + random.nextFloat()) - 0.5);
		}
	}
}
