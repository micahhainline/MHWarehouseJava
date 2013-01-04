package com.hainline.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Room {
	private final int volumeInSquareMeters;
	private final boolean stairs;
	private final int hazmatFlags;
	private final List<Box> boxes;

	public Room(int volumeInSquareMeters) {
		this(volumeInSquareMeters, false);
	}

	public Room(int volumeInSquareMeters, boolean stairs) {
		this(volumeInSquareMeters, stairs, HazmatFlags.NONE);
	}

	public Room(int volumeInSquareMeters, boolean stairs, int hazmatFlags) {
		this.volumeInSquareMeters = volumeInSquareMeters;
		this.stairs = stairs;
		this.hazmatFlags = hazmatFlags;
		this.boxes = new ArrayList<Box>();
	}

	public List<Box> getBoxes() {
		return Collections.unmodifiableList(boxes);
	}

	public void addBox(Box box) {
		boxes.add(box);
	}

	public int getVolumeInSquareMeters() {
		return volumeInSquareMeters;
	}

	public boolean hasStairs() {
		return stairs;
	}

	public int getHazmatFlags() {
		return hazmatFlags;
	}
}
