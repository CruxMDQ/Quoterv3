package com.callisto.quoter;

import java.util.Observable;

public class Status extends Observable 
{
	private String name = "First time i have this Text";

	/**
	 * @return the value
	 */
	public String getValue() {
		return name;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String name) {
		this.name = name;
		setChanged();
		notifyObservers();
	}
}
