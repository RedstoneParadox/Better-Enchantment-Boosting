package io.github.redstoneparadox.betterenchantmentboosting.search;

public class SearchNode {
	protected final int x;
	protected final int y;
	protected final int z;
	protected boolean dead = false;

	public SearchNode(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
