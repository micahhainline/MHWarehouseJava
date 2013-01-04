package com.hainline.warehouse;

public final class Box {
	private final String name;
	private final int volumeInSquareMeters;
	private final int hazmatFlags;

	public Box(String name, int volumeInSquareMeters) {
		this(name, volumeInSquareMeters, HazmatFlags.NONE);
	}

	public Box(String name, int volumeInSquareMeters, int hazmatFlags) {
		this.name = name;
		this.volumeInSquareMeters = volumeInSquareMeters;
		this.hazmatFlags = hazmatFlags;
	}

	public String getName() {
		return name;
	}

	public int getVolumeInSquareMeters() {
		return volumeInSquareMeters;
	}

	public int getHazmatFlags() {
		return hazmatFlags;
	}

	@Override
	public String toString() {
		return name;
	}
}
