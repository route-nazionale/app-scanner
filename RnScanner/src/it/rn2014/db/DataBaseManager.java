/**
 * 
 */
package it.rn2014.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	private static String DB_PATH = "";
	private static String DB_PATH_DOWNLOAD = "";
	// Database name
	public static String DB_NAME = "rn2014.db";
	
	private SQLiteDatabase database;

	public DataBaseManager(Context context) {
		super(context, DB_NAME, null, 1); // 1? its Database Version
		DB_PATH			 = context.getDir("db", Context.MODE_PRIVATE).getAbsolutePath();
		DB_PATH_DOWNLOAD = context.getDir("download", Context.MODE_PRIVATE).getAbsolutePath();
	}

	public void createDataBase() throws IOException {
		
		// If database not exists copy it from the assets
		boolean mDataBaseExist = checkDataBase();
		if (!mDataBaseExist) {
			this.getReadableDatabase();
			this.close();
		}
	}
	
	private boolean deleteDataBaseDownload(){
		return deleteDataBase(DB_PATH_DOWNLOAD);
	}

	private boolean deleteDataBase(){
		return deleteDataBase(DB_PATH);
	}
	
	private boolean deleteDataBase(String path){
		boolean deleteFile = false ;
		File dbFile = new File(path + "/" + DB_NAME);
		if(dbFile.exists()){
			deleteFile = dbFile.delete();
		}
		return deleteFile;
	}
	
	public boolean checkDataBase(){
		return checkDataBase(DB_PATH);
	}

	// Check that the database exists
	private boolean checkDataBase(String path) {
		File dbFile = new File(path + "/" + DB_NAME);
		return dbFile.exists();
	}
	
	// Copy the database from assets
	public void copyDataBaseDownload() throws IOException {
		deleteDataBase();
		copyDataBase(DB_PATH_DOWNLOAD) ;
		deleteDataBaseDownload();
	}
	
	private void copyDataBase(String path) {
		String pathToCopy = DB_PATH ;
		String pathFromCopy = DB_PATH_DOWNLOAD;
		
		try {
				
			InputStream inputStream = null;
			OutputStream outputStream = null;	
			
			File dbDir = new File(pathToCopy);
			File dbFile = new File(pathToCopy  + "/" + DB_NAME);

			if (!dbDir.exists()) dbDir.mkdir();
			if (!dbFile.exists()) dbFile.createNewFile();
				
			inputStream = new FileInputStream(pathFromCopy  + "/" + DB_NAME);
			outputStream = new FileOutputStream(dbFile);
			
			
			byte[] mBuffer = new byte[1024];
			int length;
			while ((length = inputStream.read(mBuffer)) > 0) {
				outputStream.write(mBuffer, 0, length);
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
		} catch (FileNotFoundException e) {	e.printStackTrace();
		} catch (IOException e) { e.printStackTrace();
		}
	}

	// Open the database, so we can query it
	public boolean openDataBase() throws SQLException {
		String mPath = DB_PATH + DB_NAME;
		database = SQLiteDatabase.openDatabase(mPath, null,SQLiteDatabase.CREATE_IF_NECESSARY);
		return database != null;
	}

	@Override
	public synchronized void close() {
		if (database != null)
			database.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }
}
