package it.rn2014.db;

import it.rn2014.db.entity.StatisticheScansioni;

import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Classe per la gestione delle query sul DB delle statistiche
 * Implementato con patter singleton
 * 
 * @author Luca Sirri & Nicola Corti
 */
public class StatsManager {

	/** TAG per i file di log */
	public static final String TAG = "StatsManager";

	/** Nome del DB delle statistiche */
	private static final String DB_NAME = "stat.db";
	
	/** Riferimento al DB */
	private SQLiteDatabase database;
	/** Riferimento al DB Manager */
	private DataBaseManager databaseManager;
	
	/** Istanza di Stats (singleto) */
	private static StatsManager instance = null;

	/**
	 * Ritorna un nuovo Stats manager
	 * 
	 * @param context
	 */
	private StatsManager(Context context) {
		databaseManager = new DataBaseManager(context, DB_NAME);
		createDatabase();
		createStatTable();
	}
	
	/**
	 * Crea la tabella delle statistiche per il nuovo DB
	 * (ricorda che questo non e' scaricato ma aperto sul device)
	 */
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
			// Recupero un DB scrivibile e faccio la query
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

	/**
	 * Ritorna un'istanza di un Stat manager (singleton) 
	 * 
	 * @param c Contesto di esecuzione
	 */
	public static synchronized StatsManager getInstance(Context c){
		if (instance == null){
			instance = new StatsManager(c);
		}
		return instance;
	}
	
	/** Controlla se esiste il DB
	 * @return True se esiste il DB false altrimenti
	 */
	public synchronized boolean checkDataBase(){
		return databaseManager.checkDataBase();
	}

	/**
	 * Crea un nuovo database
	 * @throws SQLException Se ha problemi di tipo SQL
	 */
	public synchronized StatsManager createDatabase() throws SQLException {
		try {
			databaseManager.createDataBase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	/**
	 * Apre il database al fine di poterci fare query (funzione di comodo)
	 * 
	 * @return Lo statmanager aperto
	 * @throws SQLException Errori a livello di DB
	 */
	private synchronized StatsManager open() throws SQLException {
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
	
	/**
	 * Chiude lo stat manager
	 */
	private synchronized void close() {
		databaseManager.close();
	}
	
	/**
	 * Ritorno tutte le statistiche non ancora sincronizzare
	 * 
	 * @return L'elenco delle statistiche da sincronizzare
	 */
	public synchronized ArrayList<StatisticheScansioni> findAllStatsNotSync(){
		String sql = "SELECT * from statistiche WHERE sync = 0";
		return findAllStatsBySQL(sql) ;
	}
	
	
	/**
	 * Ritorno tutte le statistiche data un query SQL select
	 * 
	 * @param sql Query di select su `statistiche`
	 * @return L'elenco delle statistiche trovate
	 */
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
	
	/**
	 * Inserisco una nuova statistica nel DB
	 * 
	 * @param statistica Statistica da inserire
	 * @return True se inserita correttamente
	 */
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
			
			Log.d("insertStats", "Statistica salvata");
			close();
			return true;
		} catch (Exception ex) {
			Log.d("insertStats", ex.toString());
			return false;
		}
		
		
	}
	
	/**
	 * Aggiorno tutte le statistiche le imposto come sincronizzate
	 */
	public synchronized void updateSyncStats (){
		
		// Query di update delle statistiche
		String sql = "UPDATE `statistiche` SET sync = 1 WHERE sync = 0";
		
		try {
			databaseManager.openDataBase();
			databaseManager.close();
			database = databaseManager.getWritableDatabase();
			database.execSQL(sql);
			
			database.close();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "updating >>" + mSQLException.toString());
			throw mSQLException;
		}
		
	}
	
	
	/**
	 * Metodo per mappare l'oggetto StatisticheScansioni a partire dal cursore
	 * 
	 * @param cursor Cursore su statistiche scansioni
	 * @return Un oggetto di tipo StatisticheScansioni
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
	
	/**
	 * Ritorna un cursore a partire da una query
	 * 
	 * @param sql Query SQL da eseguire
	 * @return Cursore della query
	 */	
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

	/**
	 * Ritorna il valore di un campo del cursore
	 * 
	 * @param cur Cursore di una query
	 * @param ColumnName Nome della colonna
	 * @return Valore per quella colonna
	 */
	private synchronized static String getColumnValue(Cursor cur, String ColumnName) {
		try {
			return cur.getString(cur.getColumnIndex(ColumnName));
		} catch (Exception ex) {
			return "";
		}
	}
}
