/**
 * 
 */
package it.rn2014.db;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Classe per la gestione dei DATABASE
 *  
 * @author Luca Sirri
 */
public class DataBaseManager extends SQLiteOpenHelper {
	
	/** Tag per i log */
	public static final String TAG = "DataBaseHelper"; 
	
	/** Nome del database */
	private static String DB_NAME = "";
	/** Path del database */
	private String DB_PATH = "";
	
	/** Riferimento al database */
	private SQLiteDatabase database;
	/** Riferimento al file DB */
	private File DB_FILE;

	/**
	 * Costruttore che istanzia un nuovo DB
	 * caricando PATH, NAME e FILE
	 * 
	 * @param context Contesto di esecuzione
	 * @param name Nome del database
	 */
	public DataBaseManager(Context context, String name) {
		super(context, name, null, 1); // 1? its Database Version
		DB_NAME = name;
		DB_PATH = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/";
		DB_FILE = new File(DB_PATH, DB_NAME);
	}

	/**
	 * Crea un nuovo database
	 * @throws IOException Se ha problemi di IO (non riesce a creare il file)
	 */
	public void createDataBase() throws IOException {
		// If database not exists copy it from the assets
		boolean mDataBaseExist = checkDataBase();
		if (!mDataBaseExist) {
			this.getReadableDatabase();
			this.close();
		}
	}
	
	/**
	 * Elimina il database
	 * 
	 * @return True se completato correttamente, false altrimenti
	 */
	public boolean deleteDataBase(){
		return deleteDataBase(DB_FILE);
	}
	
	/**
	 * Elimina il file di database indicato
	 * 
	 * @param dbFile File DB Da eliminare
	 * @return True se completato correttamente, false altrimenti 
	 */
	private boolean deleteDataBase(File dbFile){
		boolean deleteFile = false ;
		if(dbFile.exists()){ deleteFile = dbFile.delete(); }
		return deleteFile;
	}
	
	/**
	 * Controlla se il db esiste
	 * 
	 * @return True se esiste, false altrimenti
	 */
	public boolean checkDataBase(){
		return checkDataBase(DB_FILE);
	}

	/**
	 * Controlla se il db esiste
	 * 
	 * @param dbFile File DB Da controllare
	 * @return True se esiste, false altrimenti
	 */
	private boolean checkDataBase(File dbFile) {
		return dbFile.exists();
	}

	
	/**
	 * Apre il database al fine di poterci fare query
	 * 
	 * @return True se aperto correttamente, false altrimenti
	 * @throws SQLException Errori a livello di DB
	 */
	public boolean openDataBase() throws SQLException {
		String mPath = DB_FILE.getPath();
		database = SQLiteDatabase.openDatabase(mPath, null,SQLiteDatabase.CREATE_IF_NECESSARY);
		return database != null;
	}

	/**
	 * Chiude il database
	 */
	@Override
	public synchronized void close() {
		if (database != null) database.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }

}
