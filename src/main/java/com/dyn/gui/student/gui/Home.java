package com.dyn.gui.student.gui;

import java.util.ArrayList;

import com.dyn.gui.student.StudentUI;
import com.dyn.gui.utils.BooleanChangeListener;
import com.dyn.gui.utils.DYNGuiConstants;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;
import com.rabbit.gui.utils.DefaultTextures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Home extends Show {

	private EntityPlayerSP student;
	private SelectStringEntry selectedEntry;
	private ScrollableDisplayList plotsDisplayList;
	private TextBox plotText;
	private TextBox nameText;
	private String PLOTS_TEXT = "Plot Name";
	private String NAMES_TEXT = "Nickname";

	public Home() {
		setBackground(new DefaultBackground());
		title = "Student Gui";
		// NetworkManager.sendToServer(new RequestPlotListMessage());

		BooleanChangeListener listener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				refreshList();
			}
		};

		StudentUI.needsRefresh.setFlag(false);
		StudentUI.needsRefresh.addBooleanChangeListener(listener, this);
	}

	private void claimPlot() {
		// if ((StudentUI.selection == null) || (StudentUI.selection.getStart() == null)
		// || (StudentUI.selection.getEnd() == null)) {
		// // selection is null or not there so do the default
		// student.sendChatMessage("//pos1 " + Math.round((student.posX - 4.5)) + "," +
		// Math.round((student.posY - 1))
		// + "," + Math.round((student.posZ - 4.5)));
		// student.sendChatMessage("//pos2 " + Math.round((student.posX + 4.5)) + "," +
		// Math.round((student.posY - 1))
		// + "," + Math.round((student.posZ + 4.5)));
		// student.sendChatMessage("/plot claim");
		// // right now it asks to confirm plot but we might not want that
		// student.sendChatMessage("/yes");
		// // how should we name a plot... maybe make them provide a name?
		// if (plotText.getText().equals(PLOTS_TEXT)) {
		// namePlot("Plot#" + (StudentUI.plots.size() + 1));
		// } else {
		// namePlot(plotText.getText());
		// plotText.setText("");
		// }
		// student.sendChatMessage("//desel");
		// } else {
		// student.sendChatMessage("/plot claim");
		// // right now it asks to confirm plot but we might not want that
		// student.sendChatMessage("/yes");
		// // how should we name a plot... maybe make them provide a name?
		// if (plotText.getText().equals(PLOTS_TEXT)) {
		// namePlot("Plot#" + (StudentUI.plots.size() + 1));
		// } else {
		// namePlot(plotText.getText());
		// plotText.setText("");
		// }
		// student.sendChatMessage("//desel");
		// }
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
	}

	private void namePlot(String plotName) {
		// if (!plotName.isEmpty() && !plotName.equals(PLOTS_TEXT) &&
		// !Censor.containsSwear(plotName)) {
		// student.sendChatMessage("/plot set name " + plotName);
		// NetworkManager.sendToServer(new RequestPlotListMessage());
		// }
	}

	@Override
	public void onClose() {
		StudentUI.needsRefresh.removeBooleanChangeListener(this);
	}

	public void refreshList() {
		plotsDisplayList.clear();
		plotsDisplayList.add(new StringEntry("--Your Plots--"));
		for (String s : StudentUI.plots) {
			plotsDisplayList.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}
		StudentUI.needsRefresh.setFlag(false);
	}

	private void setNickname() {
		// if (!nameText.getText().isEmpty() && !nameText.getText().equals(NAMES_TEXT)
		// && !Censor.containsSwear(nameText.getText())) {
		// NetworkManager.sendToServer(new SyncNamesServerMessage(nameText.getText(),
		// student.getName()));
		// NetworkManager.sendToServer(new ServerCommandMessage(
		// "/nickname " + student.getName() + " " + nameText.getText().replace(' ',
		// '_')));
		// }
	}

	private void setPos(int pos) {
		if (pos == 1) {
			student.sendChatMessage("//pos1 " + Math.round((student.posX)) + "," + Math.round((student.posY - 1)) + ","
					+ Math.round((student.posZ)));
		} else {
			student.sendChatMessage("//pos2 " + Math.round((student.posX)) + "," + Math.round((student.posY - 1)) + ","
					+ Math.round((student.posZ)));
		}
	}

	@Override
	public void setup() {
		student = Minecraft.getMinecraft().thePlayer;

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Student Controls", TextAlignment.CENTER));

		// The students on the Roster List for this class
		ArrayList<ListEntry> plotList = new ArrayList<>();

		plotList.add(new StringEntry("--Your Plots--"));
		for (String s : StudentUI.plots) {
			plotList.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		plotsDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), width / 3, 100, 15,
				plotList);
		plotsDisplayList.setId("plots");
		registerComponent(plotsDisplayList);

		// GUI main section
		registerComponent(new Button((int) (width * .5), (int) (height * .2), 150, 20, "Claim Plot")
				.setClickListener(but -> claimPlot()));

		plotText = new TextBox((int) (width * .5), (int) (height * .3), 150, 20, PLOTS_TEXT);

		registerComponent(plotText);

		registerComponent(new Button((int) (width * .5), (int) (height * .4), 150, 20, "Name Plot")
				.setClickListener(but -> namePlot(plotText.getText())));

		registerComponent(new Button((int) (width * .5), (int) (height * .5), 150, 20, "Teleport to Plot")
				.setClickListener(but -> teleportToPlot()));

		nameText = new TextBox((int) (width * .5), (int) (height * .6), 150, 20, NAMES_TEXT);

		registerComponent(nameText);

		registerComponent(new Button((int) (width * .5), (int) (height * .7), 150, 20, "Set Nickname")
				.setClickListener(but -> setNickname()));

		registerComponent(new Button((int) (width * .5), (int) (height * .8), 60, 20, "Set Pos 1")
				.setClickListener(but -> setPos(1)));

		registerComponent(new Button((int) (width * .675), (int) (height * .8), 60, 20, "Set Pos 2")
				.setClickListener(but -> setPos(2)));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DefaultTextures.BACKGROUND1));
	}

	private void teleportToPlot() {
		if (selectedEntry != null) {
			// need a way to get the plot and its center point, should be
			// visible but will have to make a packet
			// student.sendChatMessage("/tp ");
		}
	}
}
