package com.dyn.gui.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dyn.gui.utils.BooleanListener;

public class AdminUI {

	public static Set<String> roster = new HashSet<>();
	// public static ArrayList<String> programRoster = new ArrayList<>();
	public static List<String> groups = new ArrayList<>();
	public static BooleanListener groupsMessageRecieved = new BooleanListener(false);
	public static Map<Integer, String> zones = new HashMap<>();
	public static BooleanListener zonesMessageRecieved = new BooleanListener(false);
	public static List<String> permissions = new ArrayList<>();
	public static BooleanListener permissionsMessageRecieved = new BooleanListener(false);

}