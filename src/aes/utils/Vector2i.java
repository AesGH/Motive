package aes.utils;

import net.minecraftforge.common.ForgeDirection;

public class Vector2i {
	public final int x;
	public final int y;

	public Vector2i() {
		this.x = this.y = 0;
	}

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/*
	 * public Vector2i(Vector2f vector) { this((int) vector.x, (int) vector.y);
	 * }
	 */
	public Vector2i(Vector2i vector) {
		this(vector.x, vector.y);
	}

	public Vector2i add(Vector2i value) {
		return new Vector2i(this.x + value.x, this.y + value.y);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != getClass())
			return false;

		final Vector2i other = (Vector2i) obj;
		return other.x == this.x && other.y == this.y;
	}

	@Override
	public int hashCode() {
		return ((Object) this.x).hashCode() * 23 ^ ((Object) this.y).hashCode() * 37;
	}

	public Vector2i increment(ForgeDirection direction) {
		return new Vector2i(this.x + direction.offsetX, this.y + direction.offsetY);
	}

	public boolean isEmpty() {
		return this.x == 0 && this.y == 0;
	}

	public Vector2i normalise() {
		return new Vector2i(this.x != 0 ? this.x / Math.abs(this.x) : 0, this.y != 0 ? this.y / Math.abs(this.y) : 0);
	}

	@Override
	public String toString() {
		return this.x + "," + this.y;
	}
}
