/**
 * 
 */
package it.rn2014.db;

import it.rn2014.db.entity.Evento;
import it.rn2014.db.entity.Persona;
import it.rn2014.db.entity.StatisticheScansioni;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author Luca
 * 
 */
public class QueryManager {

	protected static final String TAG = "DataAdapter";

	private final Context mContext;
	private SQLiteDatabase database;
	private DataBaseManager databaseManager;

	public QueryManager(Context context) {
		this.mContext = context;
		databaseManager = new DataBaseManager(mContext);
	}

	public QueryManager createDatabase() throws SQLException {
		try {
			databaseManager.createDataBase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public QueryManager open() throws SQLException {
		try {
			databaseManager.openDataBase();
			databaseManager.close();
			database = databaseManager.getReadableDatabase();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "open >>" + mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}

	public void close() {
		databaseManager.close();
	}

	private Cursor getDBCursor(String sql) {
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

	private static String getColumnValue(Cursor cur, String ColumnName) {
		try {
			return cur.getString(cur.getColumnIndex(ColumnName));
		} catch (Exception ex) {
			return "";
		}
	}

	public Persona findPersonaByCodAgesci(String codAgesci) {
		
		String sql = "SELECT * FROM persone WHERE codiceAgesci = '" + codAgesci  + "'" ;
		Cursor cursor = getDBCursor(sql);
		return getPersona(cursor);
		
	}
	
	public Persona findPersonabyBadge(String badge) {
		
		String sql = "SELECT * FROM persone WHERE codiceBadge = '" + badge  + "'" ;
		Cursor cursor = getDBCursor(sql);
		return getPersona(cursor);
	}
	
	public ArrayList<Evento> findEvento(){
		return null;
	}

	
	private Persona getPersona(Cursor cursor) {
		Persona persona;
		persona = new Persona();
 
		//FIXME aggiornare i campi con quelli nuovi
		/*
		persona.setNome(getColumnValue(cursor, "nome"));
		persona.setCognome(getColumnValue(cursor, "cognome"));
		persona.setCodiceAgesci(getColumnValue(cursor, "codAgesci"));
		persona.setGruppo(getColumnValue(cursor, "gruppo"));
		persona.setRuolo(getColumnValue(cursor, "ruolo"));
		persona.setCodiceBadge(getColumnValue(cursor, "codiceBadge"));
		*/
		return persona;
	}
	
	
	
	public ArrayList<StatisticheScansioni> findAllStatsByImeiNotSync(String imei){
		String sql = "SELECT * from statisticheScansioni Where imei = '" + imei + "' and sync ISNULL" ;
		return findAllStatsBySQL(sql) ;
	}
	
	public ArrayList<StatisticheScansioni> findAllStatsByEventSync(String idEvent){
		String sql = "SELECT * FROM statisticheScansioni WHERE idEvento = '" + idEvent+ "' AND sync NOTNULL" ;
		return findAllStatsBySQL(sql) ;
	}
	
	public ArrayList<StatisticheScansioni> findAllStatsByBadgeAndEventNotMine(String idEvent, String myImei, String codiceUnivoco, String codiceRisampa ){
		String sql = "SELECT * FROM statisticheScansioni WHERE idEvento = 'idevento' " +
					 "AND codiceUnivoco = '" + codiceUnivoco + "'" +
					 "AND codiceRisampa = '" + codiceRisampa+ "'" +
					 "AND imei<>'" + myImei + "'" ;
		return findAllStatsBySQL(sql) ;
	}
	
	public ArrayList<StatisticheScansioni> findAllStatsBySQL(String sql){
		
		ArrayList<StatisticheScansioni> statisticheScansioniList = new ArrayList<StatisticheScansioni>();
		Cursor cursor = getDBCursor(sql);
		
		if(cursor.moveToFirst()){
			do{
				StatisticheScansioni statisticheScansioni = getStatisticaScansione(cursor);
				statisticheScansioniList.add(statisticheScansioni);
			} while(cursor.moveToNext());
		}
		
		return statisticheScansioniList;
	}

	private StatisticheScansioni getStatisticaScansione(Cursor cursor) {
		StatisticheScansioni statisticheScansioni = new StatisticheScansioni();
		
		statisticheScansioni.setCodiceRisampa(getColumnValue(cursor, "codiceRistampa"));
		statisticheScansioni.setCodiceUnivoco(getColumnValue(cursor, "codiceUnivoco"));
		statisticheScansioni.setIdEvento(getColumnValue(cursor, "idEvento"));
		statisticheScansioni.setTime(getColumnValue(cursor, "time"));
		statisticheScansioni.setOperatore(getColumnValue(cursor, "operatore"));
		statisticheScansioni.setSlot(Integer.valueOf(getColumnValue(cursor, "slot")));
		statisticheScansioni.setImei(getColumnValue(cursor, "imei"));
		statisticheScansioni.setErrore(Boolean.valueOf(getColumnValue(cursor, "errore")));
		statisticheScansioni.setEntrata(Boolean.valueOf(getColumnValue(cursor, "entrata")));
		statisticheScansioni.setSync(Boolean.valueOf(getColumnValue(cursor, "sync")));
		return statisticheScansioni;
	}

}
