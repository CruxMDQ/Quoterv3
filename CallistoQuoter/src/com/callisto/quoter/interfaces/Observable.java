package com.callisto.quoter.interfaces;

public interface Observable 
{
	public void registerObserver(Observer o);
	public void removeObserver(Observer o);
	public void notifyObservers();
}
