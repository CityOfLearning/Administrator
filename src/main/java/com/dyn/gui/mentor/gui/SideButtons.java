package com.dyn.gui.mentor.gui;

import com.dyn.gui.utils.DYNGuiConstants;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.show.Show;

public class SideButtons {

	private static PictureButton but1;
	private static PictureButton but2;
	private static PictureButton but3;
	private static PictureButton but4;

	public static void init(Show show, int pageNum) {

		show.registerComponent(SideButtons.but1 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNGuiConstants.BUTTON_LOCATION_1.getLeft()),
				(int) (show.getHeight() * DYNGuiConstants.BUTTON_LOCATION_1.getRight()), 30, 30,
				DYNGuiConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.setDoesDrawHoverText(true).setClickListener(but -> {
							show.getStage().display(new Home());
							show.onClose();
						}));

		show.registerComponent(SideButtons.but2 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNGuiConstants.BUTTON_LOCATION_2.getLeft()),
				(int) (show.getHeight() * DYNGuiConstants.BUTTON_LOCATION_2.getRight()), 30, 30,
				DYNGuiConstants.ROSTER_IMAGE).setIsEnabled(true).addHoverText("Student Rosters")
						.setDoesDrawHoverText(true).setClickListener(but -> {
							show.getStage().display(new Roster());
							show.onClose();
						}));

		show.registerComponent(SideButtons.but3 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNGuiConstants.BUTTON_LOCATION_3.getLeft()),
				(int) (show.getHeight() * DYNGuiConstants.BUTTON_LOCATION_3.getRight()), 30, 30,
				DYNGuiConstants.STUDENT_IMAGE).setIsEnabled(true).addHoverText("Manage a Student")
						.setDoesDrawHoverText(true).setClickListener(but -> {
							show.getStage().display(new ManageStudent());
							show.onClose();
						}));

		show.registerComponent(SideButtons.but4 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNGuiConstants.BUTTON_LOCATION_4.getLeft()),
				(int) (show.getHeight() * DYNGuiConstants.BUTTON_LOCATION_4.getRight()), 30, 30,
				DYNGuiConstants.INVENTORY_IMAGE).setIsEnabled(true).addHoverText("Manage Inventory")
						.setDoesDrawHoverText(true).setClickListener(but -> {
							show.getStage().display(new ManageStudentsInventory());
							show.onClose();
						}));

		// the side buttons
		switch (pageNum) {
		case 1:
			SideButtons.but1.setIsEnabled(false);
			break;
		case 2:
			SideButtons.but2.setIsEnabled(false);
			break;
		case 3:
			SideButtons.but3.setIsEnabled(false);
			break;
		case 4:
			SideButtons.but4.setIsEnabled(false);
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		case 9:
			break;
		case 10:
			break;
		}

	}
}
