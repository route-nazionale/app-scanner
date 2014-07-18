package it.rn2014.scanner;

public class UserData {

    private static UserData instance= null;

    public String lastChoose = null;
    public String date = null;
    public String cu = null;
    public String authLevel = null;
    public String lastEvent = null;
    public int lastEventTurn = 0;
    

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
    public synchronized String getLevel(){ return this.authLevel; }
    public synchronized String getChoose(){ return this.lastChoose; }
    public synchronized String getEvent(){ return this.lastEvent; }
    public synchronized int getTurn(){ return this.lastEventTurn; }
    
    public synchronized void logOut() {
    	this.date = null;
    	this.cu = null;
    	this.authLevel = null;
    	this.lastChoose = null;
    	this.lastEvent = null;
    	this.lastEventTurn = 0;
	}
}
