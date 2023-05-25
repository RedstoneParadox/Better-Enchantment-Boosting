package io.github.redstoneparadox.betterenchantmentboosting.mixin;

import fuzs.easymagic.world.inventory.ModEnchantmentMenu;
import io.github.redstoneparadox.betterenchantmentboosting.util.EnchantmentTableBooster;
import io.github.redstoneparadox.betterenchantmentboosting.util.SearchUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(value = ModEnchantmentMenu.class, remap = false)
public abstract class ModEnchantmentMenuMixin {
	@Inject(method = "getEnchantingPower", at = @At("HEAD"), cancellable = true)
	private void getEnchantingPower(World level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		List<BlockPos> bookshelfPositions = SearchUtil.search(level, pos);
		double power = EnchantmentTableBooster.getPower(level, bookshelfPositions);

		cir.setReturnValue((int) Math.round(power));
	}
}
