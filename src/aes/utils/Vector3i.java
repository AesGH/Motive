package aes.utils;

import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.ForgeDirection;

public class Vector3i {
	public final int x;
	public final int y;
	public final int z;

	public Vector3i() {
		this.x = this.y = this.z = 0;
	}

	public Vector3i(ChunkCoordinates coordinates) {
		this(coordinates.posX, coordinates.posY, coordinates.posZ);
	}

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i(Vector3f vector) {
		this((int) vector.x, (int) vector.y, (int) vector.z);
	}

	public Vector3i(Vector3i vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3i add(Vector3i value) {
		return new Vector3i(this.x + value.x, this.y + value.y, this.z + value.z);
	}

	public float distanceTo(Vector3i other) {
		final double d1 = Math.sqrt((other.x - this.x) * (other.x - this.x) + (other.z - this.z) * (other.z - this.z));
		final double d2 = Math.sqrt(d1 * d1 + (other.y - this.y) * (other.y - this.y));
		return (float) d2;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector3i))
			// if (obj.getClass() != getClass())
			return false;

		final Vector3i other = (Vector3i) obj;
		return other.x == this.x && other.y == this.y && other.z == this.z;
	}

	public String getDirection() {
		String result = "";
		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (direction.offsetX == this.x && this.x != 0 || direction.offsetY == this.y && this.y != 0 || direction.offsetZ == this.z && this.z != 0) {
				if (!result.isEmpty()) {
					result += "+";
				}
				result += direction.name().toLowerCase();
			}
		}
		return result;
	}

	public ForgeDirection getForgeDirection() {
		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (direction.offsetX == this.x && direction.offsetY == this.y && direction.offsetZ == this.z)
				return direction;
		}

		return ForgeDirection.UNKNOWN;
	}

	@Override
	public int hashCode() {
		return this.x * 23 ^ this.y * 37 ^ this.z * 73;
	}

	public Vector3i increment(ForgeDirection direction) {
		return new Vector3i(this.x + direction.offsetX, this.y + direction.offsetY, this.z + direction.offsetZ);
	}

	public Vector3i increment(ForgeDirection direction, int count) {
		return new Vector3i(this.x + count * direction.offsetX, this.y + count * direction.offsetY, this.z + count * direction.offsetZ);
	}

	public boolean isEmpty() {
		return this.x == 0 && this.y == 0 && this.z == 0;
	}

	public Vector3i negate() {
		return new Vector3i(-this.x, -this.y, -this.z);
	}

	public Vector3i normalise() {
		return new Vector3i(this.x != 0 ? this.x / Math.abs(this.x) : 0, this.y != 0 ? this.y / Math.abs(this.y) : 0, this.z != 0 ? this.z / Math.abs(this.z)
				: 0);
	}

	@Override
	public String toString() {
		return this.x + "," + this.y + "," + this.z;
	}
}