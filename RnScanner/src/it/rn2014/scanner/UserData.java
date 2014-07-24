package it.rn2014.scanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;

public class UserData implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String FILENAME = "userdata";

	private static UserData instance= null;

    private String date = null;
    private String cu = null;
    private String authLevel = null;
    private String lastEvent = null;
    private int lastEventTurn = 0;
    private String lastChoose = null;
    private boolean setNoBarcode = false;
    
    protected UserData(){}

    public static synchronized UserData getInstance(){
    	if(null == instance){
    		instance = new UserData();
    	}
    	return instance;
    }
	
    public synchronized void setDate(String d){ this.date = d; }
    public synchronized void setCU(String c){ this.cu = c; }
    public synchronized void setLevel(String l){ this.authLevel = l; }
    public synchronized void setChoose(String l){ this.lastChoose = l; }
    public synchronized void setEvent(String l){ this.lastEvent = l; }
    public synchronized void setTurn(int t){ this.lastEventTurn = t; }
    
    public synchronized String getDate(){ return this.date; }
    public synchronized String getCU(){ return this.cu; }
    public synchronized String getCUnoReprint(){ return this.cu.substring(0, this.cu.length()-2); }
    public synchronized String getLevel(){ return this.authLevel; }
    public synchronized String getChoose(){ return this.lastChoose; }
    public synchronized String getEvent(){ return this.lastEvent; }
    public synchronized int getTurn(){ return this.lastEventTurn; }
    
    public synchronized boolean getSetNoBarcode() { return setNoBarcode; }
	public synchronized void setSetNoBarcode(boolean setNoBarcode) { this.setNoBarcode = setNoBarcode;	}
    
    public synchronized void logOut() {
    	this.date = null;
    	this.cu = null;
    	this.authLevel = null;
    	this.lastChoose = null;
    	this.lastEvent = null;
    	this.lastEventTurn = 0;
	}
    
    public static synchronized void saveInstance(Context c){
    	FileOutputStream fos;
		try {
			fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
	    	ObjectOutputStream oos = new ObjectOutputStream(fos);
	    	oos.writeObject(instance);
	    	if (oos != null) oos.close();
	    	if (fos != null) fos.close();
	    	
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		  catch (IOException e) { e.printStackTrace(); }
    }
    
    public static synchronized boolean restoreInstance(Context c){
    	FileInputStream fis;
		try {
			fis = c.openFileInput(FILENAME);
	    	ObjectInputStream ois = new ObjectInputStream(fis);
	    	instance = (UserData) ois.readObject();
	    	if (ois != null) ois.close();
	    	if (fis != null) fis.close();
	    	
		} catch (Exception e) { return false; }
		return true;
    }
}
