package aes.utils;

import net.minecraftforge.common.ForgeDirection;

public class Vector3d {
	public double x;
	public double y;
	public double z;

	public Vector3d() {
	}

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d(Vector3d vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3d(Vector3f vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3d(Vector3i vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3d add(Vector3d value) {
		return new Vector3d(this.x + value.x, this.y + value.y, this.z + value.z);
	}

	public Vector3d crossProduct(Vector3d value) {
		return new Vector3d(this.y * value.z - this.z * value.y, this.z * value.x - this.x * value.z, this.x * value.y - this.y * value.x);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != getClass())
			return false;

		final Vector3d other = (Vector3d) obj;
		return other.x == this.x && other.y == this.y && other.z == this.z;
	}

	@Override
	public int hashCode() {
		return ((Object) this.x).hashCode() * 23 ^ ((Object) this.y).hashCode() * 37 ^ ((Object) this.z).hashCode() * 73;
	}

	public Vector3d increment(ForgeDirection direction) {
		return new Vector3d(this.x + direction.offsetX, this.y + direction.offsetY, this.z + direction.offsetZ);
	}

	public Vector3d invert() {
		return new Vector3d(-this.x, -this.y, -this.z);
	}

	public boolean isEmpty() {
		return this.x == 0 && this.y == 0 && this.z == 0;
	}

	public Vector3d normalise() {
		return new Vector3d(this.x != 0 ? this.x / Math.abs(this.x) : 0, this.y != 0 ? this.y / Math.abs(this.y) : 0, this.z != 0 ? this.z / Math.abs(this.z)
				: 0);
	}

	public Vector3d subtract(Vector3d value) {
		return new Vector3d(this.x - value.x, this.y - value.y, this.z - value.z);
	}

	@Override
	public String toString() {
		return this.x + "," + this.y + "," + this.z;
	}
}