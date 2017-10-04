package com.dyn.gui.admin.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dyn.gui.GuiMods;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.RequestUserlistMessage;
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
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;
import com.rabbit.gui.utils.DefaultTextures;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

public class ManageStudentsInventory extends Show {
	private ScrollableDisplayList itemDisplayList;
	private ScrollableDisplayList userDisplayList;
	private ArrayList<Item> itemList = new ArrayList<>();
	private TextBox userBox;
	private TextBox itemBox;
	private TextBox amountBox;
	private boolean affectAllStudents;
	private EntityPlayerSP admin;
	private Button checkButton;

	public ManageStudentsInventory() {
		setBackground(new DefaultBackground());
		title = "Admin Gui";
		affectAllStudents = false;

		BooleanChangeListener listener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				userDisplayList.clear();
				for (String student : GuiMods.usernames) {
					userDisplayList.add(new SelectStringEntry(student));
				}
				event.getDispatcher().setFlag(false);
			}
		};
		GuiMods.serverUserlistReturned.setFlag(false);
		GuiMods.serverUserlistReturned.addBooleanChangeListener(listener, this);
	}

	private void checkBoxChanged() {
		affectAllStudents = !affectAllStudents;
		checkButton.setIsEnabled(!affectAllStudents);
	}

	private void checkStudentInventory() {
		if (!userBox.getText().isEmpty()) {
			if (!userBox.getText().isEmpty()) {
				admin.sendChatMessage("/invsee " + userBox.getText());
			}
		}
	}

	private void clearPlayerInventory() {
		// Clear all students inventory
		if (affectAllStudents) {
			for (String student : GuiMods.usernames) {
				GuiMods.proxy.addScheduledTask(() -> {
					admin.sendChatMessage("/clear " + student);
				});
			}
		} else if (!userBox.getText().isEmpty()) {
			admin.sendChatMessage("/clear " + userBox.getText());
		}
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		for (ListEntry listEntry : list.getContent()) {
			if (!listEntry.equals(entry)) {
				listEntry.setSelected(false);
			}
		}
		if (list.getId() == "itms") {
			itemBox.setText(entry.getTitle());
		} else if (list.getId() == "roster") {
			userBox.setText(entry.getTitle());
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
						if (is.getDisplayName().contentEquals(itemBox.getText())) {
							tItem = i;
							itmSt = is;
						}
					}
				} else {
					ItemStack is = new ItemStack(i);
					if (is.getDisplayName().contentEquals(itemBox.getText())) {
						tItem = i;
						itmSt = is;
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
		admin.sendChatMessage("/give " + student + " " + tItem.getRegistryName() + " " + amt + " " + itemMod);
	}

	private void giveItemToPlayer() {
		if (affectAllStudents) {
			if (itemBox.getText().isEmpty()) {
				return;
			}
			for (String student : GuiMods.usernames) {
				giveItem(student);
			}
		}

		else {
			if (userBox.getText().isEmpty() || itemBox.getText().isEmpty()) {
				return;
			}
			giveItem(userBox.getText());
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
						if (is.getDisplayName().contentEquals(itemBox.getText())) {
							tItem = i;
							itmSt = is;
						}
					}
				} else {
					ItemStack is = new ItemStack(i);
					if (is.getDisplayName().contentEquals(itemBox.getText())) {
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
		admin.sendChatMessage("/clear " + student + " " + tItem.getRegistryName() + " " + amt + " " + itemMod);
	}

	private void removeItemFromPlayer() {

		if (affectAllStudents) {
			for (String student : GuiMods.usernames) {
				removeItem(student);
			}
		} else {
			if (userBox.getText().isEmpty() || itemBox.getText().isEmpty()) {
				return;
			}
			removeItem(userBox.getText());
		}
	}

	@Override
	public void setup() {
		super.setup();

		admin = Minecraft.getMinecraft().thePlayer;

		SideButtons.init(this, 4);

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Manage Student Inventory",
				TextAlignment.CENTER));

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

		for (String s : GuiMods.usernames) {
			if (!s.isEmpty()) {
				rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
						int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
			}
		}

		userDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .275), width / 3, 100, 15,
				rlist);
		userDisplayList.setId("roster");
		registerComponent(userDisplayList);

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
			userDisplayList.clear();
			for (String student : GuiMods.usernames) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					userDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
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
