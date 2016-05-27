package org.usfirst.frc.team5431.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class SmarterDashboard {
	private static NetworkTable table;
	private static boolean init = false;

	private static ArrayList<String> debug = new ArrayList<String>();

	private static String constructDebug() {
		final StringBuilder b = new StringBuilder();
		for (String s : debug)
			b.append(s).append("\n");
		return b.toString();
	}

	private static void init() {
		init = true;
		table = NetworkTable.getTable("5431");
	}

	public static void periodic() {
		table.putString("DEBUG", constructDebug());
	}

	private static void checkInit() {
		if (!init)
			init();
	}

	public static void put(String key, Object o) {
		checkInit();
		table.putValue(key, o);
	}

	public static Object get(String key) {
		checkInit();
		return table.getValue(key, null);
	}
	
	public static double[] getNumberArray(String key, double[] defaults) {
		checkInit();
		return table.getNumberArray(key, defaults);
	}

	public static void putString(String key, String val) {
		checkInit();
		table.putString(key, val);
	}

	public static void putBoolean(String key, boolean val) {
		checkInit();
		table.putBoolean(key, val);
	}

	public static void putNumber(String key, double val) {
		checkInit();
		table.putNumber(key, val);
	}

	public static String getString(String key, String def) {
		checkInit();
		return table.getString(key, def);
	}

	public static boolean getBoolean(String key, boolean def) {
		checkInit();
		return table.getBoolean(key, def);
	}

	public static double getNumber(String key, double def) {
		checkInit();
		return table.getNumber(key, def);
	}

	public static void addDebugString(String val) {
		checkInit();
		final int index = debug.indexOf(val);
		if (index >= 0) {
			debug.add(index, val);
		} else {
			debug.add(val);
		}
	}

}
