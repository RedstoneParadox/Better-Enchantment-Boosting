package io.github.redstoneparadox.betterenchantmentboosting.search;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SearchArea {
	private GrowthPredicate growthPredicate = block -> false;
	private SearchPredicate searchPredicate = block -> false;
	private final Set<SearchNode> nodes = new HashSet<>();

	public void setGrowthPredicate(GrowthPredicate growthPredicate) {
		this.growthPredicate = growthPredicate;
	}

	public void setSearchPredicate(SearchPredicate searchPredicate) {
		this.searchPredicate = searchPredicate;
	}

	public int search(World world, BlockPos origin, Box bounds) {
		boolean terminate = false;
		int matches = 0;

		nodes.clear();
		nodes.add(new SearchNode(0, 0, 0));

		while (true) {
			List<SearchNode> newNodes = new ArrayList<>();

			for (SearchNode node: nodes) {
				if (!node.dead) {
					for (int x = -1; x <= 1; x++) {
						for (int y = -1; y <= 1; y++) {
							for (int z = -1; z<=1; z++) {
								boolean positionOccupied = false;
								int nxtX = x + node.x;
								int nxtY = y + node.y;
								int nxtZ = z + node.z;

								for (SearchNode node2: nodes) {
									if (node2.x == nxtX && node2.y == nxtY && node2.z == nxtZ) {
										positionOccupied = true;
										break;
									}
								}

								if (!positionOccupied) {
									BlockPos blockPos = origin.add(nxtX, nxtY, nxtZ);

									if (bounds.contains(Vec3d.ofCenter(blockPos))) {
										BlockState state = world.getBlockState(blockPos);

										if (searchPredicate.isMatch(state)) {
											matches += 1;
										} else if (growthPredicate.canGrow(state)) {
											newNodes.add(new SearchNode(nxtX, nxtY, nxtZ));
										}
									}
								}
							}
						}
					}
				}

				node.dead = true;
			}

			if (newNodes.isEmpty()) {
				break;
			}

			nodes.addAll(newNodes);
		}

		return matches;
	}

	public interface GrowthPredicate {
		boolean canGrow(BlockState state);
	}

	public interface SearchPredicate {
		boolean isMatch(BlockState state);
	}
}
