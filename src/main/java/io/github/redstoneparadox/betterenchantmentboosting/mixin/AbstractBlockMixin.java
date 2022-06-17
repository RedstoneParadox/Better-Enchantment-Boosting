package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentTableBooster;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchUtil;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlockMixin.class)
public abstract class AbstractBlockMixin implements EnchantmentTableBooster {
	@Override
	public double getPower(World world, BlockState state) {
		if (state.isIn(SearchUtil.Tags.BOOKSHELVES)) return 1.0;
		return 0;
	}
}
