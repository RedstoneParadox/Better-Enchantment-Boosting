package io.github.redstoneparadox.betterenchantmentboosting.search;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SearchArea {
	private GrowthPredicate growthPredicate = block -> false;
	private SearchPredicate searchPredicate = block -> false;

	public void setGrowthPredicate(GrowthPredicate growthPredicate) {
		this.growthPredicate = growthPredicate;
	}

	public void setSearchPredicate(SearchPredicate searchPredicate) {
		this.searchPredicate = searchPredicate;
	}

	public List<BlockPos> search(World world, BlockPos origin, Box bounds) {
		List<BlockPos> matches = new ArrayList<>();
		int minX = (int) (bounds.minX - origin.getX());
		int minY = (int) (bounds.minY - origin.getY());
		int minZ = (int) (bounds.minZ - origin.getZ());
		int maxX = (int) (bounds.maxX - origin.getX());
		int maxY = (int) (bounds.maxY - origin.getY());
		int maxZ = (int) (bounds.maxZ - origin.getZ());
		CellArray cells = new CellArray(minX, minY, minZ, maxX, maxY, maxZ);

		System.out.println("Growing!");
		while (true) {
			int growingCount = 0;

			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					for (int z = minZ; z <= maxZ; z++) {
						if (cells.get(x, y, z) != CellState.GROWING) continue;

						for (Direction direction: Direction.values()) {
							int nxtX = direction.getOffsetX() + x;
							int nxtY = direction.getOffsetY() + y;
							int nxtZ = direction.getOffsetZ() + z;

							BlockPos blockPos = origin.add(nxtX, nxtY, nxtZ);

							if (bounds.contains(Vec3d.ofCenter(blockPos)) && cells.get(nxtX, nxtY, nxtZ) == CellState.EMPTY) {
								BlockState state = world.getBlockState(blockPos);

								if (searchPredicate.isMatch(state)) {
									cells.set(nxtX, nxtY, nxtZ, CellState.GROWN);
									matches.add(blockPos);
								} else if (!growthPredicate.canGrow(state)) {
									cells.set(nxtX, nxtY, nxtZ, CellState.GROWN);
								} else {
									cells.set(nxtX, nxtY, nxtZ, CellState.GROWING);
									growingCount += 1;
								}
							}
						}

						// Cell no longer needs to grow in future iterations
						cells.set(x, y, z, CellState.GROWN);
					}
				}
			}

			System.out.println("growingCount = " + growingCount);
			if (growingCount == 0) break;
		}

		return matches;
	}

	public interface GrowthPredicate {
		boolean canGrow(BlockState state);
	}

	public interface SearchPredicate {
		boolean isMatch(BlockState state);
	}

	private enum CellState {
		EMPTY,
		GROWING,
		GROWN
	}

	private static class CellArray {
		final int minX;
		final int minY;
		final int minZ;
		List<List<List<CellState>>> cellStates = new ArrayList<>();

		private CellArray(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;

			for (int x = minX; x <= maxX; x++) {
				List<List<CellState>> yList = new ArrayList<>();

				for (int y = minY; y <= maxY; y++) {
					List<CellState> zList = new ArrayList<>();

					for (int z = minZ; z <= maxZ; z++) {
						if (x == 0 && y == 0 && z == 0) zList.add(CellState.GROWING);
						else zList.add(CellState.EMPTY);
					}

					yList.add(zList);
				}
				cellStates.add(yList);
			}
		}

		private CellState get(int x, int y, int z) {
			return cellStates.get(x - minX).get(y - minY).get(z - minZ);
		}

		private void set(int x, int y, int z, CellState cellState) {
			cellStates.get(x - minX).get(y - minY).set(z - minZ, cellState);
		}
	}
}
