package com.dyn.gui.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.dyn.gui.GuiMods;
import com.dyn.gui.mentor.MentorUI;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.FeedPlayerMessage;
import com.dyn.gui.network.messages.RemoveEffectsMessage;
import com.dyn.gui.network.messages.RequestFreezePlayerMessage;
import com.dyn.gui.network.messages.RequestUserStatusMessage;
import com.dyn.gui.network.messages.RequestUserlistMessage;
import com.dyn.gui.network.messages.ServerCommandMessage;
import com.dyn.gui.utils.BooleanChangeListener;
import com.dyn.gui.utils.DYNGuiConstants;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBoxButton;
import com.rabbit.gui.component.control.CheckBoxPictureButton;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.PictureToggleButton;
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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class ManageStudent extends Show {

	private EntityPlayerSP mentor;
	private SelectStringEntry selectedEntry;
	private ScrollableDisplayList rosterDisplayList;

	private boolean isFrozen;
	private boolean isMuted;
	private boolean isStudentInCreative;

	private String muteText;
	private String freezeText;
	private String modeText;
	private PictureToggleButton muteButton;
	private CheckBoxPictureButton freezeButton;
	private CheckBoxButton modeButton;

	public ManageStudent() {
		setBackground(new DefaultBackground());
		title = "Mentor GUI Manage A Student";
		freezeText = "Freeze Students";
		muteText = "Mute Students";
		modeText = "Creative Mode";
		isFrozen = false;
		isMuted = false;
		isStudentInCreative = false;
		BooleanChangeListener listener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				isFrozen = GuiMods.playerStatus.get("frozen").getAsBoolean();
				freezeButton.setToggle(isFrozen);
				isMuted = GuiMods.playerStatus.get("muted").getAsBoolean();
				muteButton.setToggle(isMuted);
				isStudentInCreative = GuiMods.playerStatus.get("mode").getAsBoolean();
				modeButton.setToggle(isStudentInCreative);
			}
		};

		GuiMods.playerStatusReturned.setFlag(false);
		GuiMods.playerStatusReturned.addBooleanChangeListener(listener, this);

		BooleanChangeListener rosterlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				rosterDisplayList.clear();
				for (String student : MentorUI.roster) {
					if (GuiMods.usernames.contains(student)) {
						rosterDisplayList
								.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
										int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
					} else {
						rosterDisplayList.add(
								new SelectStringEntry(student, Color.gray, (SelectStringEntry entry, DisplayList dlist,
										int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
					}
				}
			}
		};

		GuiMods.serverUserlistReturned.setFlag(false);
		GuiMods.serverUserlistReturned.addBooleanChangeListener(rosterlistener, this);
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		for (ListEntry listEntry : list.getContent()) {
			if (!listEntry.equals(entry)) {
				listEntry.setSelected(false);
			}
		}
		selectedEntry = entry;
		NetworkManager.sendToServer(new RequestUserStatusMessage(selectedEntry.getValue()));
	}

	private void feedStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				NetworkManager.sendToServer(new FeedPlayerMessage(selectedEntry.getValue()));
			}
		}
	}

	private void freezeUnfreezeStudent() {
		if (selectedEntry != null) {
			isFrozen = !isFrozen;
			NetworkManager.sendToServer(new RequestFreezePlayerMessage(selectedEntry.getValue(), isFrozen));

			if (isFrozen) {
				freezeText = "UnFreeze Students";
				List<String> text = freezeButton.getHoverText();
				text.clear();
				text.add(freezeText);
				freezeButton.setHoverText(text);
			} else {
				freezeText = "Freeze Students";
				List<String> text = freezeButton.getHoverText();
				text.clear();
				text.add(freezeText);
				freezeButton.setHoverText(text);
			}
		}
	}

	private void healStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				NetworkManager.sendToServer(new ServerCommandMessage(selectedEntry.getValue()));
			}
		}
	}

	private void muteUnmuteStudent() {
		if (selectedEntry != null) {
			if (!isMuted) {
				NetworkManager.sendToServer(new ServerCommandMessage(selectedEntry.getValue()));
			} else {
				NetworkManager.sendToServer(new ServerCommandMessage(selectedEntry.getValue()));
			}

			isMuted = !isMuted;
			if (isMuted) {
				muteText = "UnMute Students";
				List<String> text = muteButton.getHoverText();
				text.clear();
				text.add(muteText);
				muteButton.setHoverText(text);
			} else {
				muteText = "Mute Students";
				List<String> text = muteButton.getHoverText();
				text.clear();
				text.add(muteText);
				muteButton.setHoverText(text);
			}
		}
	}

	@Override
	public void onClose() {
		GuiMods.playerStatusReturned.removeBooleanChangeListener(this);
		GuiMods.serverUserlistReturned.removeBooleanChangeListener(this);
	}

	@Override
	public void setup() {
		super.setup();

		SideButtons.init(this, 3);

		mentor = Minecraft.getMinecraft().thePlayer;

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Manage a Student", TextAlignment.CENTER));

		registerComponent(new TextBox((int) (width * .23), (int) (height * .25), width / 4, 20, "Search for User")
				.setId("rostersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<>();

		for (String student : MentorUI.roster) {
			if (GuiMods.usernames.contains(student)) {
				rlist.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist, int mouseX,
						int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
			} else {
				rlist.add(new SelectStringEntry(student, Color.GRAY, (SelectStringEntry entry, DisplayList dlist,
						int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
			}
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// GUI main section
		registerComponent(
				new PictureButton((int) (width * .15), (int) (height * .25), 20, 20, DYNGuiConstants.REFRESH_IMAGE)
						.addHoverText("Refresh").setDoesDrawHoverText(true)
						.setClickListener(but -> NetworkManager.sendToServer(new RequestUserlistMessage())));

		freezeButton = new CheckBoxPictureButton((int) (width * .55), (int) (height * .25), 50, 25,
				DYNGuiConstants.FREEZE_IMAGE, false);
		freezeButton.setIsEnabled(true).addHoverText(freezeText).setDoesDrawHoverText(true)
				.setClickListener(but -> freezeUnfreezeStudent());
		registerComponent(freezeButton);

		muteButton = new PictureToggleButton((int) (width * .55), (int) (height * .365), 50, 25,
				DYNGuiConstants.UNMUTE_IMAGE, DYNGuiConstants.MUTE_IMAGE, false);
		muteButton.setIsEnabled(true).addHoverText(muteText).setDoesDrawHoverText(true)
				.setClickListener(but -> muteUnmuteStudent());
		registerComponent(muteButton);

		modeButton = new CheckBoxButton((int) (width * .55), (int) (height * .5), (int) (width / 3.3), 20,
				"   Toggle Creative", false);
		modeButton.setIsEnabled(true).addHoverText(modeText).setDoesDrawHoverText(true)
				.setClickListener(but -> switchMode());
		registerComponent(modeButton);

		registerComponent(
				new PictureButton((int) (width * .7), (int) (height * .25), 50, 25, DYNGuiConstants.HEART_IMAGE)
						.setIsEnabled(true).addHoverText("Heal Students").setDoesDrawHoverText(true)
						.setClickListener(but -> healStudent()));

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .365), 50, 25,
				new ResourceLocation("minecraft", "textures/items/chicken_cooked.png")).setIsEnabled(true)
						.addHoverText("Feed Students").setDoesDrawHoverText(true)
						.setClickListener(but -> feedStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .6), (int) (width / 3.3), 20, "Teleport to Student")
						.setClickListener(but -> teleportToStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .7), (int) (width / 3.3), 20, "Teleport Student to Me")
						.setClickListener(but -> teleportStudentTo()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .8), (int) (width / 3.3), 20, "Remove Effects")
						.addHoverText("Removes effects like poison and invisibility").setDoesDrawHoverText(true)
						.setClickListener(but -> {
							if ((selectedEntry != null) && !selectedEntry.getTitle().isEmpty()) {
								NetworkManager.sendToServer(new RemoveEffectsMessage(selectedEntry.getValue()));
							}
						}));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DefaultTextures.BACKGROUND1));
	}

	private void switchMode() {
		if (selectedEntry != null) {
			NetworkManager.sendToServer(new ServerCommandMessage(
					"/gamemode " + (isStudentInCreative ? "0 " : "1 ") + selectedEntry.getValue()));
			isStudentInCreative = !isStudentInCreative;
			if (isStudentInCreative) {
				modeText = "Survival Mode";
				List<String> text = modeButton.getHoverText();
				text.clear();
				text.add(modeText);
				modeButton.setHoverText(text);
			} else {
				modeText = "Creative Mode";
				List<String> text = modeButton.getHoverText();
				text.clear();
				text.add(modeText);
				modeButton.setHoverText(text);
			}
		}
	}

	private void teleportStudentTo() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				NetworkManager
						.sendToServer(new ServerCommandMessage(selectedEntry.getValue() + " " + mentor.getName()));
			}
		}
	}

	private void teleportToStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				NetworkManager.sendToServer(
						new ServerCommandMessage("/tp " + mentor.getName() + " " + selectedEntry.getValue()));
			}
		}
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "rostersearch") {
			rosterDisplayList.clear();
			for (String student : MentorUI.roster) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					if (GuiMods.usernames.contains(student)) {
						rosterDisplayList
								.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
										int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
					} else {
						rosterDisplayList
								.add(new SelectStringEntry(student,
										(SelectStringEntry entry, DisplayList dlist, int mouseX,
												int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY))
														.setIsEnabled(false));
					}
				}
			}
		}
	}
}
