package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentTableBooster;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin implements EnchantmentTableBooster {
	@Override
	public double getPower(World world, BlockState state) {
		return 1.0f;
	}
}
