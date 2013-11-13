package aes.utils;

import net.minecraftforge.common.ForgeDirection;

public class Vector3f {
	public float x;
	public float y;
	public float z;

	public Vector3f() {
	}

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(Vector3i vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3f add(Vector3f value) {
		return new Vector3f(this.x + value.x, this.y + value.y, this.z + value.z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != getClass())
			return false;

		final Vector3f other = (Vector3f) obj;
		return other.x == this.x && other.y == this.y && other.z == this.z;
	}

	@Override
	public int hashCode() {
		return ((Object) this.x).hashCode() * 23 ^ ((Object) this.y).hashCode() * 37 ^ ((Object) this.z).hashCode() * 73;
	}

	public Vector3f increment(ForgeDirection direction) {
		return new Vector3f(this.x + direction.offsetX, this.y + direction.offsetY, this.z + direction.offsetZ);
	}

	public Vector3f normalise() {
		return new Vector3f(this.x != 0 ? this.x / Math.abs(this.x) : 0, this.y != 0 ? this.y / Math.abs(this.y) : 0, this.z != 0 ? this.z / Math.abs(this.z)
				: 0);
	}

	@Override
	public String toString() {
		return this.x + "," + this.y + "," + this.z;
	}
}