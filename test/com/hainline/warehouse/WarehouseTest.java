package com.hainline.warehouse;

import static com.hainline.warehouse.HazmatFlags.CHEMICAL;
import static com.hainline.warehouse.HazmatFlags.NONE;
import static com.hainline.warehouse.HazmatFlags.NUCLEAR;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

public class WarehouseTest extends TestCase {
	private static void assertEqualContents(Collection<?> expected, Collection<?> actual) {
		String message = "expected " + expected + " but was " + actual;
		assertTrue(message, expected.size() == actual.size() && expected.containsAll(actual));
	}

	public void testWhenOneBoxIsAddedToOneRoomThenTheRoomContainsTheBox() {
		Room loadingDock = new Room(100);

		Warehouse testObject = new Warehouse(Arrays.asList(loadingDock));

		Box box1 = new Box("box1", 10);

		testObject.addBoxes(Arrays.asList(box1));

		assertEqualContents(Arrays.asList(box1), loadingDock.getBoxes());
	}

	public void testWhenBoxesAreAddedThenTheyAreAddedToFirstRoomFirstUntilCapacityIsReached() {
		Room loadingDock = new Room(100);
		Room mainStorage = new Room(1000);
		Box box1 = new Box("box1", 10);
		Box box2 = new Box("box2", 70);
		Box box3 = new Box("box3", 15);
		Box box4 = new Box("box4", 10);

		Warehouse testObject = new Warehouse(Arrays.asList(loadingDock, mainStorage));

		testObject.addBoxes(Arrays.asList(box1, box2, box3, box4));

		assertEqualContents(Arrays.asList(box1, box2, box3), loadingDock.getBoxes());
		assertEqualContents(Arrays.asList(box4), mainStorage.getBoxes());
	}

	public void testWhenBoxOver50VolumeIsLoadedThenItIsNotLoadedInARoomRequiringStairs() {
		Room basement = new Room(1000, true);
		Room mainStorage = new Room(1000, false);
		Box box1 = new Box("box1", 10);
		Box box2 = new Box("box2", 50);
		Box box3 = new Box("box3", 51);
		Box box4 = new Box("box4", 10);

		Warehouse testObject = new Warehouse(Arrays.asList(basement, mainStorage));

		testObject.addBoxes(Arrays.asList(box1, box2, box3, box4));

		assertEqualContents(Arrays.asList(box1, box2, box4), basement.getBoxes());
		assertEqualContents(Arrays.asList(box3), mainStorage.getBoxes());
	}

	public void testWhenBoxesExceedCapacityThenFinalBoxesAreRejected() {
		Room loadingDock = new Room(100);
		Box box1 = new Box("box1", 40);
		Box box2 = new Box("box2", 40);
		Box box3 = new Box("box3", 40);
		Box box4 = new Box("box4", 10);

		Warehouse testObject = new Warehouse(Arrays.asList(loadingDock));

		List<Box> rejectedBoxes = testObject.addBoxes(Arrays.asList(box1, box2, box3, box4));

		assertEqualContents(Arrays.asList(box1, box2, box4), loadingDock.getBoxes());
		assertEqualContents(Arrays.asList(box3), rejectedBoxes);
	}

	public void testWhenChemicalBoxIsLoadedItIsLoadedInSafeRoom() {
		Room loadingDock = new Room(100, false, NONE);
		Room chemStorage = new Room(100, false, CHEMICAL);
		Box box1 = new Box("box1", 10, NONE);
		Box box2 = new Box("box2", 10, CHEMICAL);
		Box box3 = new Box("box3", 10, NONE);

		Warehouse testObject = new Warehouse(Arrays.asList(loadingDock, chemStorage));

		List<Box> rejectedBoxes = testObject.addBoxes(Arrays.asList(box1, box2, box3));

		assertEqualContents(Arrays.asList(box1, box3), loadingDock.getBoxes());
		assertEqualContents(Arrays.asList(box2), chemStorage.getBoxes());
		assertEqualContents(Arrays.asList(), rejectedBoxes);
	}

	public void testWhenHazmatHasNoSafeRoomThenItIsRejected() {
		Room loadingDock = new Room(100, false, NONE);
		Room mainStorage = new Room(1000, false, NONE);
		Box box1 = new Box("box1", 10, NONE);
		Box box2 = new Box("box2", 10, CHEMICAL);
		Box box3 = new Box("box3", 10, NONE);

		Warehouse testObject = new Warehouse(Arrays.asList(loadingDock, mainStorage));

		List<Box> rejectedBoxes = testObject.addBoxes(Arrays.asList(box1, box2, box3));

		assertEqualContents(Arrays.asList(box1, box3), loadingDock.getBoxes());
		assertEqualContents(Arrays.asList(), mainStorage.getBoxes());
		assertEqualContents(rejectedBoxes, Arrays.asList(box2));
	}

	public void testChemicalBoxCannotGoInANuclearRoom() {
		Room nuclearStorage = new Room(100, false, NUCLEAR);
		Box box1 = new Box("box1", 10, NONE);
		Box box2 = new Box("box2", 10, CHEMICAL);
		Box box3 = new Box("box3", 10, NONE);

		Warehouse testObject = new Warehouse(Arrays.asList(nuclearStorage));

		List<Box> rejectedBoxes = testObject.addBoxes(Arrays.asList(box1, box2, box3));

		assertEqualContents(Arrays.asList(box1, box3), nuclearStorage.getBoxes());
		assertEqualContents(rejectedBoxes, Arrays.asList(box2));
	}

	public void testDifferentHazmatBoxesCanBeStoredInDifferentRoomsWhileStillRespectingSizeAndStairs() {
		Room loadingDock = new Room(100, false, NONE);
		Room chemLoft = new Room(100, true, CHEMICAL);
		Room vault = new Room(150, false, CHEMICAL | NUCLEAR);
		Box box1 = new Box("box1", 10, CHEMICAL);
		Box box2 = new Box("box2", 60, CHEMICAL);
		Box box3 = new Box("box3", 10, NUCLEAR);
		Box box4 = new Box("box4", 10, NUCLEAR | CHEMICAL);
		Box box5 = new Box("box5", 50, CHEMICAL);
		Box box6 = new Box("box6", 50, CHEMICAL);

		Warehouse testObject = new Warehouse(Arrays.asList(loadingDock, chemLoft, vault));

		List<Box> rejectedBoxes = testObject.addBoxes(Arrays.asList(box1, box2, box3, box4, box5, box6));

		assertEqualContents(loadingDock.getBoxes(), Arrays.asList());
		assertEqualContents(Arrays.asList(box1, box5), chemLoft.getBoxes());
		assertEqualContents(Arrays.asList(box2, box3, box4, box6), vault.getBoxes());
		assertTrue(rejectedBoxes.size() == 0);
	}

	public void testBoxesAreNotPlacedSuchThatAHazmatWillHaveNoPlaceToGoWhenThereIsEnoughRoom() {
		Room vault = new Room(150, false, CHEMICAL | NUCLEAR);
		Room mainStorage = new Room(1000, false, NONE);
		Box box1 = new Box("box1", 60, NONE);
		Box box2 = new Box("box2", 60, NONE);
		Box box3 = new Box("box3", 60, NONE);
		Box box4 = new Box("box4", 60, NONE);
		Box box5 = new Box("box5", 60, CHEMICAL);

		Warehouse testObject = new Warehouse(Arrays.asList(vault, mainStorage));

		List<Box> rejectedBoxes = testObject.addBoxes(Arrays.asList(box1, box2, box3, box4, box5));

		assertEqualContents(Arrays.asList(box1, box5), vault.getBoxes());
		assertEqualContents(Arrays.asList(box2, box3, box4), mainStorage.getBoxes());
		assertTrue(rejectedBoxes.size() == 0);
	}

	public void testOrderForBoxesIsPreservedWhenThereIsEnoughRoom() {
		Room vault = new Room(150, false, CHEMICAL | NUCLEAR);
		Room mainStorage = new Room(1000, false, NONE);
		Box box1 = new Box("box1", 60, NONE);
		Box box2 = new Box("box2", 60, NONE);
		Box box3 = new Box("box3", 60, NONE);
		Box box4 = new Box("box4", 30, NONE);
		Box box5 = new Box("box5", 60, CHEMICAL);

		Warehouse testObject = new Warehouse(Arrays.asList(vault, mainStorage));

		List<Box> rejectedBoxes = testObject.addBoxes(Arrays.asList(box1, box2, box3, box4, box5));

		assertEqualContents(Arrays.asList(box1, box4, box5), vault.getBoxes());
		assertEqualContents(Arrays.asList(box2, box3), mainStorage.getBoxes());
		assertTrue(rejectedBoxes.size() == 0);
	}
}
