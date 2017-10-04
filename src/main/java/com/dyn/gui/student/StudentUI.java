package com.dyn.gui.student;

import java.util.ArrayList;
import java.util.List;

import com.dyn.gui.utils.BooleanListener;

public class StudentUI {

	public static List<String> plots = new ArrayList<>();
	public static BooleanListener needsRefresh = new BooleanListener(false);
	public static BooleanListener frozen = new BooleanListener(false);
}
