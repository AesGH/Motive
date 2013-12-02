package aes.motive.tileentity;

public class ConnectionMask {
	private final int value;

	public ConnectionMask() {
		this.value = 0;
	}

	public ConnectionMask(boolean connectedLeft, boolean connectedRight, boolean connectedUp, boolean connectedDown, boolean connectedBack) {
		this.value = (connectedLeft ? 1 : 0) | (connectedRight ? 1 : 0) << 1 | (connectedUp ? 1 : 0) << 2 | (connectedDown ? 1 : 0) << 3
				| (connectedBack ? 1 : 0) << 4;
	}

	public ConnectionMask(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public boolean isConnectedBack() {
		return (getValue() & 16) == 16;
	}

	public boolean isConnectedDown() {
		return (getValue() & 8) == 8;
	}

	public boolean isConnectedLeft() {
		return (getValue() & 1) == 1;
	}

	public boolean isConnectedRight() {
		return (getValue() & 2) == 2;
	}

	public boolean isConnectedUp() {
		return (getValue() & 4) == 4;
	}
}
