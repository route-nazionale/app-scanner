package it.rn2014.db;

import it.rn2014.db.entity.Evento;
import it.rn2014.db.entity.Persona;
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
 * @author Luca
 * 
 */
public class QueryManager {

	public static final String TAG = "DataAdapter";

	private SQLiteDatabase database;
	private DataBaseManager databaseManager;
	
	private static QueryManager instance = null;

	// TODO Questo metodo deve diventare privato
	public QueryManager(Context context) {
		databaseManager = new DataBaseManager(context);
		createDatabase();
	}
	
	public static synchronized QueryManager getInstance(Context c){
		if (instance == null){
			instance = new QueryManager(c);
		}
		return instance;
	}
	
	public synchronized boolean checkDataBase(){
		return databaseManager.checkDataBase();
	}

	public synchronized QueryManager createDatabase() throws SQLException {
		try {
			databaseManager.createDataBase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public synchronized QueryManager open() throws SQLException {
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

	public synchronized void close() {
		databaseManager.close();
	}

	public synchronized Persona findPersonaByCodiceUnivoco(String codiceUnivoco) {
		String sql = "SELECT * from persone WHERE codiceUnivoco =  '" + codiceUnivoco  + "'" ;
		return findPersonaBySQL(sql);
	}
	
	public synchronized Persona findPersonaBySQL(String sql) {
		open();
		Cursor cursor = getDBCursor(sql);
		close();
		return getPersona(cursor);
		
	}
	
	public synchronized ArrayList<StatisticheScansioni> findAllStatsByImeiNotSync(String imei){
		String sql = "SELECT * from statisticheScansioni Where imei = '" + imei + "' and sync ISNULL" ;
		return findAllStatsBySQL(sql) ;
	}
	
	public synchronized ArrayList<StatisticheScansioni> findAllStatsByEventSync(String idEvent){
		String sql = "SELECT * FROM statisticheScansioni WHERE idEvento = '" + idEvent + "' AND sync NOTNULL" ;
		return findAllStatsBySQL(sql) ;
	}
	
	public synchronized ArrayList<StatisticheScansioni> findAllStatsByBadgeAndEventNotMine(String idEvent, String myImei, String codiceUnivoco, String codiceRisampa ){
		String sql = "SELECT * FROM statisticheScansioni WHERE idEvento = 'idevento' " +
					 "AND codiceUnivoco = '" + codiceUnivoco + "'" +
					 "AND codiceRisampa = '" + codiceRisampa+ "'" +
					 "AND imei<>'" + myImei + "'" ;
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
		
		close();
		return statisticheScansioniList;
	}
	
	public synchronized boolean insertStats(StatisticheScansioni statistica){
		try {
			
			ContentValues cv = new ContentValues();
			cv.put("codiceRistampa", statistica.getCodiceRistampa());
			cv.put("codiceUnivoco", statistica.getCodiceUnivoco());
			cv.put("idEvento", statistica.getIdEvento());
			cv.put("time", statistica.getTime());
			cv.put("operatore", statistica.getOperatore());
			cv.put("slot", statistica.getSlot());
			cv.put("imei", statistica.getImei());
			cv.put("errore", statistica.isErrore());
			cv.put("entrata", statistica.isEntrata());
			cv.put("sync", statistica.isSync());
			
			database.insert("statisticheScansioni", null, cv);
			
			Log.d("insertStats", "Statistica salvata");
			return true;
		} catch (Exception ex) {
			Log.d("insertStats", ex.toString());
			return false;
		}
		
		
	}
	
	/**
	 * Trova gli eventi di una persona
	 * 
	 * @param persona
	 * @return
	 */
	public synchronized ArrayList<Evento> findEventiByPersona(Persona persona) {
		String sql = "SELECT * from eventi" +
				"JOIN assegnamenti ON assegnamenti.idEvento = eventi.idEvento" +
				"AND assegnamenti.codiceUnivoco = " + persona.getCodiceUnivoco();
		
		return findAllEventiBySQL(sql);
	}
	
	/**
	 * Trova tutti gli eventi a partire da uno script sql
	 * @param sql
	 * @return
	 */
	public synchronized ArrayList<Evento> findAllEventiBySQL(String sql){
		
		open() ;
		
		ArrayList<Evento> eventoList = new ArrayList<Evento>();
		Cursor cursor = getDBCursor(sql);
		
		if(cursor.moveToFirst()){
			do{
				Evento evento = getEvento(cursor);
				eventoList.add(evento);
			} while(cursor.moveToNext());
		}
		
		close();
		
		return eventoList;
		
	}

	/**
	 * Metodi per mappare gli oggetti Entity Persona, Evento, Statisciche
	 * @param cursor
	 * @return
	 */
	private synchronized StatisticheScansioni getStatisticaScansione(Cursor cursor) {
		StatisticheScansioni statisticheScansioni = new StatisticheScansioni();
		
		statisticheScansioni.setCodiceRistampa(getColumnValue(cursor, "codiceRistampa"));
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
	
	private synchronized Evento getEvento(Cursor cursor){
		Evento evento = new Evento();
		
		evento.setIdEvento(getColumnValue(cursor, "idEvento"));
		evento.setNome(getColumnValue(cursor, "nome"));
		evento.setQuartiere(getColumnValue(cursor, "quartiere"));
		evento.setContrada(getColumnValue(cursor, "contrade"));
		evento.setStradaCoraggio(getColumnValue(cursor, "stradaCorggio"));
		evento.setTipoEvento(getColumnValue(cursor, "tipoEvento"));
		
		return evento;
	}
	
	private synchronized Persona getPersona(Cursor cursor) {
		Persona persona;
		persona = new Persona();
		
		persona.setCodiceUnivoco(getColumnValue(cursor, "codiceUnivoco")) ;
		persona.setRistampaBadge(getColumnValue(cursor, "ristampaBadge")) ;
		persona.setCognome(" **** ");
		persona.setNome(" **** ");
		persona.setIdGruppo(getColumnValue(cursor, "idGruppo"));
		persona.setRuolo(getColumnValue(cursor, "ruolo"));
		persona.setCodiceAgesci(getColumnValue(cursor, "codiceAgesci"));
		persona.setIdUnita(getColumnValue(cursor, "idUnita"));
		persona.setContrada(getColumnValue(cursor, "contrada"));
		persona.setQuartiere(getColumnValue(cursor, "quartiere"));
 
		return persona;
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
