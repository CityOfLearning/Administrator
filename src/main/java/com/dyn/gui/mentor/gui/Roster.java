package com.dyn.gui.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.dyn.gui.GuiMods;
import com.dyn.gui.admin.AdminUI;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.RequestUserlistMessage;
import com.dyn.gui.utils.BooleanChangeListener;
import com.dyn.gui.utils.DYNGuiConstants;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;
import com.rabbit.gui.utils.DefaultTextures;

import net.minecraft.client.Minecraft;

public class Roster extends Show {

	private SelectStringEntry selectedEntry;
	private DisplayList selectedList;
	private ScrollableDisplayList userDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList<>();
	TextLabel numberOfStudentsOnRoster;

	private BooleanChangeListener rosterlistener;

	public Roster() {
		setBackground(new DefaultBackground());
		title = "Mentor Gui Roster Management";

		// its possible there arent any users but lets refresh anyways
		if (GuiMods.usernames.size() == 0) {
			NetworkManager.sendToServer(new RequestUserlistMessage());
		}
	}

	private void addToRoster() {
		if ((selectedEntry != null) && (selectedList != null)) {
			if ((selectedList.getId() == "users") && !AdminUI.roster.contains(selectedEntry.getTitle())) {
				AdminUI.roster.add(selectedEntry.getTitle());
				selectedEntry.setSelected(false);
				rosterDisplayList.add(selectedEntry);
				userDisplayList.remove(selectedEntry);
			}
		}
		numberOfStudentsOnRoster.setText("Roster Count: " + AdminUI.roster.size());
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
		selectedList = list;

	}

	@Override
	public void onClose() {
		GuiMods.serverUserlistReturned.removeBooleanChangeListener(rosterlistener);
	}

	private void removeFromRoster() {
		if ((selectedEntry != null) && (selectedList != null)) {
			if ((selectedList.getId() == "roster") && AdminUI.roster.contains(selectedEntry.getTitle())) {
				AdminUI.roster.remove(selectedEntry.getTitle());
				selectedEntry.setSelected(false);
				rosterDisplayList.remove(selectedEntry);
				userDisplayList.add(selectedEntry);
			}
		}
		numberOfStudentsOnRoster.setText("Roster Count: " + AdminUI.roster.size());
	}

	@Override
	public void setup() {
		super.setup();

		SideButtons.init(this, 2);

		for (String s : GuiMods.usernames) {
			if (!AdminUI.roster.contains(s) && !s.equals(Minecraft.getMinecraft().thePlayer.getName())) {
				userlist.add(s);
			}
		}

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Roster Management",
				TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList<>();

		for (String s : userlist) {
			if (!s.isEmpty()) {
				ulist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
						int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
			}
		}

		registerComponent(
				new TextBox((int) (width * .15), (int) (height * .25), (int) (width / 3.3), 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		registerComponent(
				new TextBox((int) (width * .55), (int) (height * .25), (int) (width / 3.3), 20, "Search for User")
						.setId("rostersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		userDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), (int) (width / 3.3), 130,
				15, ulist);
		userDisplayList.setId("users");
		registerComponent(userDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<>();

		for (String student : AdminUI.roster) {
			rlist.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .55), (int) (height * .35), (int) (width / 3.3),
				130, 15, rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// Buttons
		registerComponent(
				new Button((width / 2) - 10, (int) (height * .4), 20, 20, ">>").setClickListener(but -> addToRoster()));

		// Buttons
		registerComponent(new Button((width / 2) - 10, (int) (height * .6), 20, 20, "<<")
				.setClickListener(but -> removeFromRoster()));

		registerComponent(
				new PictureButton((width / 2) - 10, (int) (height * .8), 20, 20, DYNGuiConstants.REFRESH_IMAGE)
						.addHoverText("Refresh").setDoesDrawHoverText(true)
						.setClickListener(but -> NetworkManager.sendToServer(new RequestUserlistMessage())));

		numberOfStudentsOnRoster = new TextLabel((int) (width * .5) + 20, (int) (height * .2), 90, 20, Color.black,
				"Roster Count: " + AdminUI.roster.size(), TextAlignment.LEFT);
		registerComponent(numberOfStudentsOnRoster);

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DefaultTextures.BACKGROUND1));

		rosterlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				((Roster) show).updateRoster();
				event.getDispatcher().setFlag(false);
			}
		};

		GuiMods.serverUserlistReturned.setFlag(false);
		GuiMods.serverUserlistReturned.addBooleanChangeListener(rosterlistener, this);
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "usersearch") {
			userDisplayList.clear();
			for (String student : userlist) {
				if (student.contains(textbox.getText())) {
					userDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "rostersearch") {
			rosterDisplayList.clear();
			for (String student : AdminUI.roster) {
				if (student.contains(textbox.getText())) {
					rosterDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}

	public void updateRoster() {
		userDisplayList.clear();
		for (String s : GuiMods.usernames) {
			if (!AdminUI.roster.contains(s) && !s.equals(Minecraft.getMinecraft().thePlayer.getName())
					&& !s.isEmpty()) {
				userDisplayList.add(new SelectStringEntry(s));
			}
		}
	}
}
