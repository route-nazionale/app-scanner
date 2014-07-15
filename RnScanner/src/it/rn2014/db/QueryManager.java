package it.rn2014.db;

import it.rn2014.db.entity.Evento;
import it.rn2014.db.entity.Persona;
import it.rn2014.db.entity.StatisticheScansioni;

import java.io.IOException;
import java.util.ArrayList;

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

	public Persona findPersonaByCodiceUnivoco(String codiceUnivoco) {
		String sql = "SELECT * from persone WHERE codiceUnivoco =  '" + codiceUnivoco  + "'" ;
		return findPersonaBySQL(sql);
	}
	
	public Persona findPersonaBySQL(String sql) {
		Cursor cursor = getDBCursor(sql);
		return getPersona(cursor);
	}
	
	public ArrayList<StatisticheScansioni> findAllStatsByImeiNotSync(String imei){
		String sql = "SELECT * from statisticheScansioni Where imei = '" + imei + "' and sync ISNULL" ;
		return findAllStatsBySQL(sql) ;
	}
	
	public ArrayList<StatisticheScansioni> findAllStatsByEventSync(String idEvent){
		String sql = "SELECT * FROM statisticheScansioni WHERE idEvento = '" + idEvent + "' AND sync NOTNULL" ;
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
	
	/**
	 * Trova gli eventi di una persona
	 * 
	 * @param persona
	 * @return
	 */
	public ArrayList<Evento> findEventiByPersona(Persona persona) {
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
	public ArrayList<Evento> findAllEventiBySQL(String sql){
		ArrayList<Evento> eventoList = new ArrayList<Evento>();
		Cursor cursor = getDBCursor(sql);
		
		if(cursor.moveToFirst()){
			do{
				Evento evento = getEvento(cursor);
				eventoList.add(evento);
			} while(cursor.moveToNext());
		}
		
		return eventoList;
		
	}

	/**
	 * Metodi per mappare gli oggetti Entity Persona, Evento, Statisciche
	 * @param cursor
	 * @return
	 */
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
	
	private Evento getEvento(Cursor cursor){
		Evento evento = new Evento();
		
		evento.setCodiceEvento(getColumnValue(cursor, "codiceEvento"));
		evento.setContrada(getColumnValue(cursor, "contrada"));
		evento.setIdEvento(getColumnValue(cursor, "contrada"));
		evento.setNome(getColumnValue(cursor, "nome"));
		evento.setQuartiere(getColumnValue(cursor, "quartiere"));
		evento.setContrada(getColumnValue(cursor, "contrada"));
		evento.setStradaCoraggio(getColumnValue(cursor, "stradaCoraggio"));
		evento.setTipoEvento(Integer.valueOf(getColumnValue(cursor, "tipoEvento")));
		
		return evento;
	}
	
	
	private Persona getPersona(Cursor cursor) {
		Persona persona;
		persona = new Persona();
		
		persona.setCodiceUnivoco(getColumnValue(cursor, "codiceUnivoco")) ;
		persona.setRistampaBadge(getColumnValue(cursor, "ristampaBadge")) ;
		persona.setCognome(" **** ");
		persona.setNome(" **** ");
		persona.setIdGruppo(getColumnValue(cursor, "idGruppo"));
		persona.setRuolo(getColumnValue(cursor, "ruolo"));
		persona.setCodiceAgesci(getColumnValue(cursor, "codAgesci"));
		persona.setIdUnita(getColumnValue(cursor, "idUnita"));
		persona.setContrada(getColumnValue(cursor, "contrada"));
		persona.setQuartiere(getColumnValue(cursor, "quartiere"));
 
		return persona;
	}

}
