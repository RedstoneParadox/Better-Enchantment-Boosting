package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentTableBooster;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractCandleBlock.class)
public abstract class AbstractCandleBlockMixin implements EnchantmentTableBooster {
	@Shadow @Final public static BooleanProperty LIT;

	@Override
	public double getPower(World world, BlockState state) {
		double basePower = BetterEnchantmentBoosting.CONFIG.powerPerCandle();

		if (!state.get(LIT)) return 0;
		else if (state.isIn(BlockTags.CANDLE_CAKES)) return basePower;
		else if (state.isIn(BlockTags.CANDLES)) return state.get(CandleBlock.CANDLES) * basePower;
		else return 0;
	}
}
