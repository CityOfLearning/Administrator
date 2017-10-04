package com.dyn.gui.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dyn.gui.GuiMods;
import com.dyn.gui.mentor.MentorUI;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.RequestUserlistMessage;
import com.dyn.gui.network.messages.ServerCommandMessage;
import com.dyn.gui.utils.BooleanChangeListener;
import com.dyn.gui.utils.DYNGuiConstants;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBox;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;
import com.rabbit.gui.utils.DefaultTextures;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

public class ManageStudentsInventory extends Show {
	private ScrollableDisplayList itemDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<Item> itemList = new ArrayList<>();
	private TextBox userBox;
	private TextBox itemBox;
	private SelectStringEntry userSelected;
	private SelectStringEntry itemSelected;
	private TextBox amountBox;
	private boolean affectAllStudents;
	private Button checkButton;

	public ManageStudentsInventory() {
		setBackground(new DefaultBackground());
		title = "Mentor Gui";
		affectAllStudents = false;

		BooleanChangeListener rosterlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				rosterDisplayList.clear();
				for (String student : MentorUI.roster) {
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
		};

		GuiMods.serverUserlistReturned.setFlag(false);
		GuiMods.serverUserlistReturned.addBooleanChangeListener(rosterlistener, this);
	}

	private void checkBoxChanged() {
		affectAllStudents = !affectAllStudents;
		checkButton.setIsEnabled(!affectAllStudents);
	}

	private void checkStudentInventory() {
		if (!userSelected.getTitle().isEmpty()) {

			NetworkManager.sendToServer(new ServerCommandMessage("/invsee " + userSelected.getValue()));
		}
	}

	private void clearPlayerInventory() {
		// Clear all students inventory
		if (affectAllStudents) {
			for (String student : MentorUI.roster) {
				NetworkManager.sendToServer(new ServerCommandMessage("/clear " + student));
			}
		} else if (!userSelected.getTitle().isEmpty()) {
			NetworkManager.sendToServer(new ServerCommandMessage("/clear " + userSelected.getValue()));
			// userBox.getText()
		}
	}

	private void entryClicked(SelectListEntry entry, DisplayList list, int mouseX, int mouseY) {
		for (ListEntry listEntry : list.getContent()) {
			if (!listEntry.equals(entry)) {
				listEntry.setSelected(false);
			}
		}
		if (list.getId() == "itms") {
			itemBox.setText(((SelectStringEntry) entry).getTitle());
			itemSelected = ((SelectStringEntry) entry);
		} else if (list.getId() == "roster") {
			userSelected = (SelectStringEntry) entry;
			userBox.setText(((SelectStringEntry) entry).getTitle());
		}

	}

	private void giveItem(String student) {
		Item tItem = null;
		ItemStack itmSt = null;
		for (Item i : itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList<>();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						if (is.getDisplayName().contentEquals(itemSelected.getTitle())) {
							tItem = i;
							itmSt = is;
						}
					}
				} else {
					ItemStack is = new ItemStack(i);
					if (is.getDisplayName().contentEquals(itemSelected.getTitle())) {
						tItem = i;
					}
				}
			}
		}
		if (tItem == null) {
			return;
		}
		String itemMod = "";
		if (itmSt != null) {
			itemMod = " " + itmSt.getItemDamage();
		}
		int amt = 0;
		try {
			amt = Integer.parseInt(amountBox.getText());
		} catch (NumberFormatException nfe) {
			amt = 1;
		}
		NetworkManager.sendToServer(new ServerCommandMessage(
				"/give " + student + " " + tItem.getRegistryName() + " " + amt + " " + itemMod));
	}

	private void giveItemToPlayer() {
		if (affectAllStudents) {
			for (String student : MentorUI.roster) {
				giveItem(student);
			}
		}

		else {
			if ((userSelected == null) || (itemSelected == null)) {
				return;
			}
			String student = userSelected.getValue();
			giveItem(student == null ? (String) userSelected.getValue() : student);
		}
	}

	@Override
	public void onClose() {
		GuiMods.serverUserlistReturned.removeBooleanChangeListener(this);
	}

	private void removeItem(String student) {
		Item tItem = null;
		ItemStack itmSt = null;
		for (Item i : itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList<>();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						if (is.getDisplayName().contentEquals(itemSelected.getTitle())) {
							tItem = i;
							itmSt = is;
						}
					}
				} else {
					ItemStack is = new ItemStack(i);
					if (is.getDisplayName().contentEquals(itemSelected.getTitle())) {
						tItem = i;
					}
				}
			}
		}
		if (tItem == null) {
			return;
		}
		String itemMod = "";
		if (itmSt != null) {
			itemMod = " " + itmSt.getItemDamage();
		}
		String amt;
		if (!((amountBox.getText() == null) || amountBox.getText().isEmpty())) {
			try {
				int amount = Math.abs(Integer.parseInt(amountBox.getText())) % 65;
				amt = "" + amount;
			} catch (NumberFormatException nfe) {
				amt = "1";
			}
		} else {
			amt = "1";
		}
		NetworkManager.sendToServer(new ServerCommandMessage(
				"/clear " + student + " " + tItem.getRegistryName() + " " + amt + " " + itemMod));
	}

	private void removeItemFromPlayer() {

		if (affectAllStudents) {
			for (String student : MentorUI.roster) {
				removeItem(student);
			}
		} else {
			if ((userSelected == null) || (itemSelected == null)) {
				return;
			}
			String student = userSelected.getValue();
			removeItem(student == null ? (String) userSelected.getValue() : student);
		}
	}

	@Override
	public void setup() {
		super.setup();

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Manage Student Inventory",
				TextAlignment.CENTER));

		SideButtons.init(this, 4);

		// get all the items in the registry
		FMLControlledNamespacedRegistry<Block> blockRegistry = GameData.getBlockRegistry();
		Iterator<?> iterator = blockRegistry.iterator();

		List<Item> blockList = new ArrayList<>();

		while (iterator.hasNext()) {
			Block blocks = (Block) iterator.next();
			blockList.add(Item.getItemFromBlock(blocks));
		}

		FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();
		iterator = itemRegistry.iterator();

		List<Item> itemsList = new ArrayList<>();

		while (iterator.hasNext()) {
			Item items = (Item) iterator.next();
			itemsList.add(items);

		}

		for (Item i : blockList) {
			if (!itemList.contains(i)) {
				itemList.add(i);
			}
		}
		for (Item i : itemsList) {
			if (!itemList.contains(i)) {
				itemList.add(i);
			}
		}

		itemList.remove(null);

		ArrayList<ListEntry> dslist = new ArrayList<>();

		for (Item i : itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList<>();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						dslist.add(
								new SelectStringEntry(is.getDisplayName(), (SelectStringEntry entry, DisplayList dlist,
										int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
					}
				} else {
					ItemStack is = new ItemStack(i);
					dslist.add(new SelectStringEntry(is.getDisplayName(), (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}

		registerComponent(new TextBox((int) (width * .235), (int) (height * .175), width / 4, 20, "Search for User")
				.setId("usersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		registerComponent(new TextBox((int) (width * .55), (int) (height * .175), width / 4, 20, "Search for Item")
				.setId("itemsearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		itemDisplayList = new ScrollableDisplayList((int) (width * .5), (int) (height * .275), width / 3, 100, 15,
				dslist);
		itemDisplayList.setId("itms");

		registerComponent(itemDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<>();

		for (String student : MentorUI.roster) {
			if (GuiMods.usernames.contains(student)) {
				rlist.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist, int mouseX,
						int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
			} else {
				rlist.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist, int mouseX,
						int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)).setIsEnabled(false));
			}
		}

		rlist.add(new SelectStringEntry(Minecraft.getMinecraft().thePlayer.getName(), (SelectStringEntry entry,
				DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .275), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		registerComponent(
				new PictureButton((int) (width * .15), (int) (height * .175), 20, 20, DYNGuiConstants.REFRESH_IMAGE)
						.addHoverText("Refresh").setDoesDrawHoverText(true)
						.setClickListener(but -> NetworkManager.sendToServer(new RequestUserlistMessage())));

		userBox = new TextBox((int) (width * .235), (int) (height * .725), width / 4, 20, "User").setId("user")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		registerComponent(userBox);

		amountBox = new TextBox((int) (width * .795) - 10, (int) (height * .725), 25, 20, "Amt").setId("amt")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		registerComponent(amountBox);

		itemBox = new TextBox((int) (width * .5), (int) (height * .725), width / 4, 20, "Item").setId("item")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		registerComponent(itemBox);

		registerComponent(new CheckBox((int) (width * .15), (int) (height * .73), 15, 15, Color.green, Color.BLACK,
				"All", affectAllStudents).setStatusChangedListener(but -> checkBoxChanged()));

		checkButton = new Button((int) (width * .175) - 10, (int) (height * .825), 50, 20, "Look");
		checkButton.addHoverText("Look at inventory").setClickListener(but -> checkStudentInventory())
				.setDoesDrawHoverText(true);
		registerComponent(checkButton);

		registerComponent(new Button((int) (width * .314) - 10, (int) (height * .825), 50, 20, "Clear")
				.setClickListener(but -> clearPlayerInventory()).addHoverText("Clear Inventory")
				.setDoesDrawHoverText(true));

		registerComponent(new Button((int) (width * .6) - 10, (int) (height * .825), 50, 20, "Give")
				.addHoverText("Give Item").setDoesDrawHoverText(true).setClickListener(but -> giveItemToPlayer()));

		registerComponent(new Button((int) (width * .739) - 10, (int) (height * .825), 50, 20, "Remove")
				.setClickListener(but -> removeItemFromPlayer()).addHoverText("Remove Item")
				.setDoesDrawHoverText(true));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DefaultTextures.BACKGROUND1));
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "itemsearch") {
			itemDisplayList.clear();
			for (Item i : itemList) {
				if (i != null) {
					if (i.getHasSubtypes()) {
						List<ItemStack> subItem = new ArrayList<>();
						i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
						for (ItemStack is : subItem) {
							if (is.getDisplayName().toLowerCase().contains(textbox.getText().toLowerCase())) {
								itemDisplayList.add(new SelectStringEntry(is.getDisplayName(),
										(SelectStringEntry entry, DisplayList dlist, int mouseX,
												int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
							}
						}
					} else {
						ItemStack is = new ItemStack(i);
						if (is.getDisplayName().toLowerCase().contains(textbox.getText().toLowerCase())) {
							itemDisplayList.add(new SelectStringEntry(is.getDisplayName(),
									(SelectStringEntry entry, DisplayList dlist, int mouseX,
											int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
						}
					}
				}
			}
		} else if (textbox.getId() == "usersearch") {
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
		} else if (textbox.getId() == "amt") {
			try {
				int num = Integer.parseInt(textbox.getText());
				int cnum = Math.max(Math.min(num, 64), 0);
				if (num != cnum) {
					textbox.setText("" + cnum);
				}
			} catch (NumberFormatException e) {
				textbox.setText("");
			}
		}
	}
}
