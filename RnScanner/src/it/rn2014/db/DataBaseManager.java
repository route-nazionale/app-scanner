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

/**
 * @author Luca
 * 
 */
public class DataBaseManager extends SQLiteOpenHelper {
	// Tag just for the LogCat window
	public static final String TAG = "DataBaseHelper"; 
	
	// destination path (location) of our database on device
	private static String DB_FOLDER = "db";
	private static String DB_NAME = "rn2014.db";
	
	private SQLiteDatabase database;
	private File DB_FILE;

	public DataBaseManager(Context context) {
		super(context, DB_NAME, null, 1); // 1? its Database Version
		DB_FILE = new File(context.getDir(DB_FOLDER, Context.MODE_PRIVATE), DB_NAME);
	}

	public void createDataBase() throws IOException {
		// If database not exists copy it from the assets
		boolean mDataBaseExist = checkDataBase();
		if (!mDataBaseExist) {
			this.getReadableDatabase();
			this.close();
		}
	}
	
	public boolean deleteDataBase(){
		return deleteDataBase(DB_FILE);
	}
	
	private boolean deleteDataBase(File dbFile){
		boolean deleteFile = false ;
		if(dbFile.exists()){ deleteFile = dbFile.delete(); }
		return deleteFile;
	}
	
	public boolean checkDataBase(){
		return checkDataBase(DB_FILE);
	}

	// Check that the database exists
	private boolean checkDataBase(File dbFile) {
		return dbFile.exists();
	}

	// Open the database, so we can query it
	public boolean openDataBase() throws SQLException {
		String mPath = DB_FILE.getAbsolutePath();
		database = SQLiteDatabase.openDatabase(mPath, null,SQLiteDatabase.CREATE_IF_NECESSARY);
		return database != null;
	}

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
