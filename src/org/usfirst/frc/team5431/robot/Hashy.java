package org.usfirst.frc.team5431.robot;

public class Hashy {
	public Object value;
	
	public Hashy(Object def) {
		value = def;
	}
	public Object get() {
		return value;
	}
	public void set(Object news) {
		value = news;
	}
}
