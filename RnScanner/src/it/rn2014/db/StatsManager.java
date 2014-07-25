package it.rn2014.db;

import it.rn2014.db.entity.StatisticheScansioni;
import it.rn2014.scanner.UserData;

import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Luca
 * 
 */
public class StatsManager {

	public static final String TAG = "DataAdapter";

	private SQLiteDatabase database;
	private DataBaseManager databaseManager;
	
	private static StatsManager instance = null;

	private StatsManager(Context context) {
		databaseManager = new DataBaseManager(context, "stat.db");
		createDatabase();
		createStatTable();
	}
	
	private void createStatTable() {
		String sql = "CREATE TABLE IF NOT EXISTS `statistiche` ( " +
		  " `idScansione` INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		  " `codiceUnivoco` VARCHAR NOT NULL, " + 
		  " `ristampaBadge` INT NOT NULL, " + 
		  " `codiceOperatore` VARCHAR NOT NULL, " + 
		  " `timeStamp` VARCHAR NOT NULL, " + 
		  " `imei` VARCHAR NOT NULL, " + 
		  " `idVarco` VARCHAR NOT NULL, " + 
		  " `turno` INT NOT NULL, " + 
		  " `tipo` VARCHAR NOT NULL, " + 
		  " `sync` INT DEFAULT 0 " + 
		  " ) ";
		
		try {
			databaseManager.openDataBase();
			databaseManager.close();
			database = databaseManager.getWritableDatabase();
			database.execSQL(sql);
			database.close();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "creation >>" + mSQLException.toString());
			throw mSQLException;
		}
	}

	public static synchronized StatsManager getInstance(Context c){
		if (instance == null){
			instance = new StatsManager(c);
		}
		return instance;
	}
	
	public synchronized boolean checkDataBase(){
		return databaseManager.checkDataBase();
	}

	public synchronized StatsManager createDatabase() throws SQLException {
		try {
			databaseManager.createDataBase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public synchronized StatsManager open() throws SQLException {
		try {
			if (database != null && !database.isOpen()){
				databaseManager.openDataBase();
				databaseManager.close();
				database = databaseManager.getReadableDatabase();
			}
		} catch (SQLException mSQLException) {
			Log.e(TAG, "open >>" + mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}
	
	public synchronized void close() {
		databaseManager.close();
	}
	
	public synchronized ArrayList<StatisticheScansioni> findAllStatsNotSync(){
		String sql = "SELECT * from statistiche WHERE sync = 0";
		return findAllStatsBySQL(sql) ;
	}
	
	
	public synchronized ArrayList<StatisticheScansioni> findAllStatsBySQL(String sql){
		
		open();
		
		ArrayList<StatisticheScansioni> statisticheScansioniList = new ArrayList<StatisticheScansioni>();
		Cursor cursor = getDBCursor(sql);
		
		if(cursor.moveToFirst()){
			do{
				StatisticheScansioni statisticheScansioni = getStatisticaScansione(cursor);
				statisticheScansioniList.add(statisticheScansioni);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		close();
		return statisticheScansioniList;
	}
	
	public synchronized boolean insertStats(StatisticheScansioni statistica){
		try {
			
			open();
			
			ContentValues cv = new ContentValues();
			cv.put("ristampaBadge", statistica.getCodiceRistampa());
			cv.put("codiceUnivoco", statistica.getCodiceUnivoco());
			cv.put("idVarco", statistica.getIdVarco());
			cv.put("timeStamp", statistica.getTime());
			cv.put("codiceOperatore", statistica.getOperatore());
			cv.put("turno", statistica.getTurno());
			cv.put("imei", statistica.getImei());
			cv.put("tipo", statistica.getType());
			cv.put("sync", statistica.isSync());
			
			database.insert("statistiche", null, cv);
			
			UserData.getInstance().incToSync();
			
			Log.d("insertStats", "Statistica salvata");
			close();
			return true;
		} catch (Exception ex) {
			Log.d("insertStats", ex.toString());
			return false;
		}
		
		
	}
	
	public synchronized void updateSyncStats (){
		
		String sql = "UPDATE `statistiche` SET sync = 1 WHERE sync = 0";
		
		try {
			databaseManager.openDataBase();
			databaseManager.close();
			database = databaseManager.getWritableDatabase();
			database.execSQL(sql);
        	UserData.getInstance().resetToSync();
			database.close();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "updating >>" + mSQLException.toString());
			throw mSQLException;
		}
		
	}
	
	
	/**
	 * Metodi per mappare gli oggetti Entity Persona, Evento, Statisciche
	 * @param cursor
	 * @return
	 */
	private synchronized StatisticheScansioni getStatisticaScansione(Cursor cursor) {
		StatisticheScansioni statisticheScansioni = new StatisticheScansioni();
		
		statisticheScansioni.setCodiceRistampa(getColumnValue(cursor, "ristampaBadge"));
		statisticheScansioni.setCodiceUnivoco(getColumnValue(cursor, "codiceUnivoco"));
		statisticheScansioni.setIdVarco(getColumnValue(cursor, "idVarco"));
		statisticheScansioni.setTime(getColumnValue(cursor, "timeStamp"));
		statisticheScansioni.setOperatore(getColumnValue(cursor, "codiceOperatore"));
		statisticheScansioni.setTurno(Integer.valueOf(getColumnValue(cursor, "turno")));
		statisticheScansioni.setImei(getColumnValue(cursor, "imei"));
		statisticheScansioni.setSync(Boolean.valueOf(getColumnValue(cursor, "sync")));
		
		String type = getColumnValue(cursor, "tipo");
		if (type.contentEquals(StatisticheScansioni.AUTH)) statisticheScansioni.setAuth();
		if (type.contentEquals(StatisticheScansioni.INVALID)) statisticheScansioni.setInvalid();
		if (type.contentEquals(StatisticheScansioni.NOT_AUTH)) statisticheScansioni.setNotAuth();
		if (type.contentEquals(StatisticheScansioni.USER_ABORT)) statisticheScansioni.setAbort();
		if (type.contentEquals(StatisticheScansioni.VALID_ENTER)) statisticheScansioni.setEnter();
		if (type.contentEquals(StatisticheScansioni.VALID_EXIT)) statisticheScansioni.setExit();
		
		return statisticheScansioni;
	}
	
		
	private synchronized Cursor getDBCursor(String sql) {
		try {
			Cursor mCur = database.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToNext();
			}
			return mCur;
		} catch (SQLException mSQLException) {
			Log.e(TAG, "getTestData >>" + mSQLException.toString());
			throw mSQLException;
		}
	}

	private synchronized static String getColumnValue(Cursor cur, String ColumnName) {
		try {
			return cur.getString(cur.getColumnIndex(ColumnName));
		} catch (Exception ex) {
			return "";
		}
	}
}
