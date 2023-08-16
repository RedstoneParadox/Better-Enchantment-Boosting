package io.github.redstoneparadox.betterenchantmentboosting.util;

import com.google.common.collect.Lists;
import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EnchantingUtil {
	private static final TagKey<Block> ENCHANTMENT_POWER_TRANSMITTER = TagKey.of(Registries.BLOCK.getKey(), new Identifier("minecraft:enchantment_power_transmitter"));
	private static final TagKey<Block> ENCHANTMENT_INFLUENCERS = TagKey.of(Registries.BLOCK.getKey(), new Identifier(BetterEnchantmentBoosting.MODID, "enchantment_influencers"));

	public static List<BlockPos> search(World world, BlockPos origin) {
		int horizontalBlocksFromTable = BetterEnchantmentBoosting.CONFIG.searchAreaConfig().horizontalBlocksFromTable();
		int blocksAboveTable = BetterEnchantmentBoosting.CONFIG.searchAreaConfig().blocksAboveTable();
		int blocksBelowTable = BetterEnchantmentBoosting.CONFIG.searchAreaConfig().blocksBelowTable();
		Box bounds = new Box(origin.add(-horizontalBlocksFromTable, -blocksBelowTable, -horizontalBlocksFromTable), origin.add(horizontalBlocksFromTable, blocksAboveTable, horizontalBlocksFromTable));
		SearchArea area = new SearchArea();

		area.setGrowthPredicate(state ->
				state.isIn(ENCHANTMENT_POWER_TRANSMITTER)
		);
		area.setSearchPredicate(state ->
				BlockContentRegistries.ENCHANTING_BOOSTERS.get(state.getBlock()).isPresent()
		);

		return area.search(world, origin, bounds);
	}

	public static double getPower(World world, List<BlockPos> boosterPositions) {
		double power = 0;

		for (BlockPos boosterPosition: boosterPositions) {
			BlockState boosterState = world.getBlockState(boosterPosition);
			Optional<EnchantingBooster> booster = BlockContentRegistries.ENCHANTING_BOOSTERS.get(boosterState.getBlock());

			if (booster.isPresent()) {
				power += booster.get().getEnchantingBoost(world, boosterState, boosterPosition);
			}
		}

		return power;
	}

	public static List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int power, RandomGenerator random, int seed, World world, List<BlockPos> boosterPositions) {
		random.setSeed(seed + slot);
		List<EnchantmentLevelEntry> baseEntries = getEntriesFromInfluencers(stack, power, random, world, boosterPositions);
		List<EnchantmentLevelEntry> list = generateEnchantments(random, stack, power, false, baseEntries);
		if (stack.isOf(Items.BOOK) && list.size() > 1) {
			list.remove(random.nextInt(list.size()));
		}

		return list;
	}

	public static List<EnchantmentLevelEntry> getEntriesFromInfluencers(ItemStack stack, int power, RandomGenerator random, World world, List<BlockPos> boosterPositions) {
		Map<Enchantment, Integer> map = new HashMap<>();
		List<EnchantmentLevelEntry> bonusEntries = new ArrayList<>();

		if (!BetterEnchantmentBoosting.CONFIG.influencingConfig().enabled()) return bonusEntries;

		for (BlockPos boosterPosition: boosterPositions) {
			BlockState boosterState = world.getBlockState(boosterPosition);

			if (boosterState.isIn(ENCHANTMENT_INFLUENCERS)) {
				BlockEntity blockEntity = world.getBlockEntity(boosterPosition);

				if (!(blockEntity instanceof Inventory inventory)) {
					String blockID = Registries.BLOCK.getId(boosterState.getBlock()).toString();
					BetterEnchantmentBoosting.LOGGER.error("Block '" + blockID + "' does not have an inventory despite being in #betterenchantmentboosting:enchantment_influencers.");
					continue;
				}

				for (int i = 0; i < 6; i++) {
					ItemStack bookStack = inventory.getStack(i);

					if (bookStack.isOf(Items.ENCHANTED_BOOK)) {
						Map<Enchantment, Integer> bookEnchantmentMap = EnchantmentHelper.get(bookStack);
						List<Enchantment> possibleEnchantments = new ArrayList<>();

						if (bookEnchantmentMap.isEmpty()) {
							BetterEnchantmentBoosting.LOGGER.warn("Attempted to influence enchanting table with empty enchanted book.");
							continue;
						}

						for (Enchantment enchantment : bookEnchantmentMap.keySet()) {
							if (!enchantment.type.isAcceptableItem(stack.getItem())
									|| (enchantment.isTreasure() && !BetterEnchantmentBoosting.CONFIG.influencingConfig().allowTreasure())
									|| (enchantment.isCursed() && !BetterEnchantmentBoosting.CONFIG.influencingConfig().allowCurses())
							) map.remove(enchantment);
							else possibleEnchantments.add(enchantment);
						}

						if (possibleEnchantments.isEmpty()) {
							continue;
						}

						Enchantment enchantment = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));
						int enchantmentLevel = bookEnchantmentMap.get(enchantment);

						if (!map.containsKey(enchantment)) map.put(enchantment, 0);
						if (map.get(enchantment) < enchantmentLevel) map.put(enchantment, enchantmentLevel);
					}
				}
			}
		}

		Item item = stack.getItem();
		int enchantability = item.getEnchantability();

		map.forEach((enchantment, maxLevel) -> {
			int power2 = power + 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);
			int entryLevel = 1;

			for(int level = enchantment.getMaxLevel(); level > enchantment.getMinLevel() - 1; --level) {
				if (power2 >= enchantment.getMinPower(level) && power2 <= enchantment.getMaxPower(level)) {
					entryLevel = level;
					break;
				}
			}

			entryLevel = Math.min(entryLevel, maxLevel);

			int power3 = power + 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);
			int entryCount = enchantability * power3/10;

			for (int i = 0; i < entryCount; i++) bonusEntries.add(new EnchantmentLevelEntry(enchantment, entryLevel));
		});

		return bonusEntries;
	}

	public static List<EnchantmentLevelEntry> generateEnchantments(RandomGenerator random, ItemStack stack, int power, boolean treasureAllowed, List<EnchantmentLevelEntry> bonusEntries) {
		List<EnchantmentLevelEntry> chosenEntries = Lists.newArrayList();
		Item item = stack.getItem();
		int enchantability = item.getEnchantability();
		if (enchantability > 0) {
			power += 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			power = MathHelper.clamp(Math.round((float) power + (float) power * f), 1, Integer.MAX_VALUE);
			List<EnchantmentLevelEntry> possibleEntries = EnchantmentHelper.getPossibleEntries(power, stack, treasureAllowed);
			possibleEntries.addAll(bonusEntries);
			if (!possibleEntries.isEmpty()) {
				Weighting.getRandomItem(random, possibleEntries).ifPresent(chosenEntries::add);

				while (random.nextInt(50) <= power) {
					if (!chosenEntries.isEmpty()) {
						EnchantmentHelper.removeConflicts(possibleEntries, Util.getLast(chosenEntries));
					}

					if (possibleEntries.isEmpty()) {
						break;
					}

					Weighting.getRandomItem(random, possibleEntries).ifPresent(chosenEntries::add);
					power /= 2;
				}
			}

		}
		return chosenEntries;
	}
}
