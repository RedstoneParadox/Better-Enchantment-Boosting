package io.github.redstoneparadox.betterenchantmentboosting.booster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosterType;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosters;

public record InventoryEnchantingBooster(float power) implements EnchantingBooster {
	public static final Codec<InventoryEnchantingBooster> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.FLOAT.fieldOf("power").forGetter(InventoryEnchantingBooster::power)
			).apply(instance, InventoryEnchantingBooster::new)
	);
	public static final EnchantingBoosterType TYPE = EnchantingBoosters.register(
			new Identifier(BetterEnchantmentBoosting.MODID, "inventory"),
			CODEC
	);

	@Override
	public float getEnchantingBoost(World world, BlockState blockState, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);

		if (!(blockEntity instanceof Inventory inventory)) {
			String blockID = Registries.BLOCK.getId(blockState.getBlock()).toString();
			BetterEnchantmentBoosting.LOGGER.error("block " + blockID + " does not have an inventory and should not be used with the inventory enchanting booster.");
			return 0;
		}

		int size = inventory.size();
		int books = 0;

		for (int i = 0; i < size; i++) {
			Item item = inventory.getStack(i).getItem();

			if (item == Items.BOOK || item == Items.ENCHANTED_BOOK) {
				books += 1;
			}
		}

		return power*((float) books /size);
	}

	@Override
	public EnchantingBoosterType getType() {
		return TYPE;
	}
}
