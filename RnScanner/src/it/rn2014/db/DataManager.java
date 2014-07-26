package it.rn2014.db;

import it.rn2014.db.entity.Evento;
import it.rn2014.db.entity.Persona;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Classe per la gestione delle query sul DB scaricato
 * Implementato con patter singleton
 * 
 * @author Luca Sirri & Nicola Corti
 */
public class DataManager {

	/** TAG per i file di log */
	public static final String TAG = "DataManager";
	/** Nome del DB Scaricato */
	private static final String DB_NAME = "rn2014.db";

	/** Riferimento al DB */
	private SQLiteDatabase database;
	/** Riferimento al DB Manager */
	private DataBaseManager databaseManager;
	
	/** Istanza di Datamanager (singleto) */
	private static DataManager instance = null;

	/**
	 * Ritorna un nuovo Data manager
	 * 
	 * @param context
	 */
	private DataManager(Context context) {
		databaseManager = new DataBaseManager(context, DB_NAME);
		createDatabase();
	}
	
	/**
	 * Ritorna un'istanza di un Data manager (singleton) 
	 * 
	 * @param c Contesto di esecuzione
	 */
	public static synchronized DataManager getInstance(Context c){
		if (instance == null){
			instance = new DataManager(c);
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
	public synchronized DataManager createDatabase() throws SQLException {
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
	 * @return Il datamanager aperto
	 * @throws SQLException Errori a livello di DB
	 */
	private synchronized DataManager open() throws SQLException {
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

	/**
	 * Chiude il datamanager
	 */
	private synchronized void close() {
		databaseManager.close();
	}

	/**
	 * Ritorna la persona dato un codice univoco
	 * 
	 * @param codiceUnivoco Codice univoco da cercare
	 * @return La persona trovata
	 */
	public synchronized Persona findPersonaByCodiceUnivoco(String codiceUnivoco) {
		String sql = "SELECT * FROM `persone` WHERE `codiceUnivoco` =  '" + codiceUnivoco  + "'" ;
		return findPersonaBySQL(sql);
	}
	
	/**
	 * Ritorna la persona dato un codice univoco e il reprint badge
	 * 
	 * @param codiceUnivoco Codice univoco da cercare
	 * @param reprint Ristampa badge da cercare
	 * @return La persona trovata
	 */
	public synchronized Persona findPersonaByCodiceUnivoco(String codiceUnivoco, String reprint) {
		String sql = "SELECT * FROM `persone` WHERE `codiceUnivoco` =  '" + codiceUnivoco  + "' AND `ristampaBadge` = '" + reprint + "'" ;
		return findPersonaBySQL(sql);
	}
	
	/**
	 * Ritorna il nome del gruppo di una persona
	 * 
	 * @param p Persona da cercare
	 * @return La stringa del nome
	 */
	public synchronized String findGruppoByPersona(Persona p){
		String sql = "SELECT gruppi.nome AS nome FROM gruppi, persone WHERE gruppi.idGruppo = persone.idGruppo AND gruppi.idUnita = persone.idUnita AND persone.codiceUnivoco = '" + p.getCodiceUnivoco() + "'";
		open();
		Cursor cursor = getDBCursor(sql);
		close();
		return getColumnValue(cursor, "nome");
	}
	
	/**
	 * Ritorna una persona dato l'SQL
	 * 
	 * @param sql SQL della persona da cercare
	 * @return Persona trovata
	 */
	public synchronized Persona findPersonaBySQL(String sql) {
		open();
		Cursor cursor = getDBCursor(sql);
		close();
		return getPersona(cursor);
		
	}
	
	
	
	/**
	 * Trova gli eventi di una persona in un dato turno
	 * 
	 * @param persona Persona di cui si vogliono gli eventi
	 * @param turno Turno di ricerca (1,2 o 3)
	 * @return Elenco di eventi della persona
	 */
	public synchronized ArrayList<Evento> findEventiByPersona(Persona persona, int turno) {
		String sql = "SELECT * from eventi" +
				" JOIN assegnamenti ON assegnamenti.idEvento = eventi.idEvento" +
				" AND assegnamenti.codiceUnivoco = '" + persona.getCodiceUnivoco() + "' AND " +
				" assegnamenti.slot = '" + turno + "'";
		
		return findAllEventiBySQL(sql);
	}
	
	/**
	 * Trova gli eventi di una persona
	 * 
	 * @param persona Persona di cui si vogliono gli eventi
	 * @return Elenco di eventi della persona
	 */
	public synchronized ArrayList<Evento> findEventiByPersona(Persona persona) {
		String sql = "SELECT * from eventi" +
				" JOIN assegnamenti ON assegnamenti.idEvento = eventi.idEvento" +
				" AND assegnamenti.codiceUnivoco = '" + persona.getCodiceUnivoco() + 
				"' ORDER BY assegnamenti.slot ";
		
		return findAllEventiBySQL(sql);
	}
	
	/**
	 * Trova gli eventi di una persona in un dato turno di cui si e' capispalla
	 * 
	 * @param cu Codice univoco della persona
	 * @param turno Turno di ricerca (1,2 o 3)
	 * @return Elenco di eventi della persona
	 */
	public synchronized ArrayList<Evento> findEventiToCheckin(String cu, String turno) {
		/////////////////////////////////////////////
		/*
		 * NOTA BENE TODO FIXME
		 * 
		 * La query seguente deve essere cambiata da
		 * AND assegnamenti.staffEvento = 0
		 * 
		 * a
		 * AND assegnamenti.staffEvento = 1
		 * 
		 * Questa e' solamente a scopo di testing, sennò non recupera i capi spalla
		 */
		/////////////////////////////////////////////
		String sql = "SELECT * from eventi" +
				"  JOIN assegnamenti ON assegnamenti.idEvento = eventi.idEvento" +
				"  AND assegnamenti.staffEvento = 1 AND assegnamenti.slot = '" + turno + 
				"' AND assegnamenti.codiceUnivoco = '" + cu + "'";
		return findAllEventiBySQL(sql);
	}
	
	/**
	 * Ritorna un evento dato un ID
	 * 
	 * @param eventid Evento che si vuole cercare
	 * @return L'evento trovato
	 */
	public Evento findEventById(String eventid) {
		String sql = "SELECT * from eventi WHERE idEvento = '" + eventid + "'";
		ArrayList<Evento> e = findAllEventiBySQL(sql);
		if (!e.isEmpty())
			return e.get(0);
		else
			return null;
	}
	
	/**
	 * Trova tutti gli eventi a partire da uno script sql
	 * @param sql Select su eventi
	 * @return Lista di eventi trovati
	 */
	public synchronized ArrayList<Evento> findAllEventiBySQL(String sql){
		
		open();
		
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
	 * Ritorna un evento a partire da un cursor
	 * 
	 * @param cursor Cursore su eventi
	 * @return Evento risultante
	 */
	private synchronized Evento getEvento(Cursor cursor){
		Evento evento = new Evento();
		
		evento.setIdEvento(getColumnValue(cursor, "idEvento"));
		evento.setNome(getColumnValue(cursor, "nome"));
		evento.setCodiceStampa(getColumnValue(cursor, "codiceStampa"));
		evento.setQuartiere(getColumnValue(cursor, "quartiere"));
		evento.setContrada(getColumnValue(cursor, "contrade"));
		evento.setStradaCoraggio(getColumnValue(cursor, "stradaCoraggio"));
		evento.setTipoEvento(getColumnValue(cursor, "tipoEvento"));
		
		return evento;
	}
	
	/**
	 * Ritorna una persona a partire da un cursore
	 * 
	 * @param cursor Cursore su persone
	 * @return Persona risultante
	 */
	private synchronized Persona getPersona(Cursor cursor) {
		Persona persona;
		persona = new Persona();
		
		persona.setCodiceUnivoco(getColumnValue(cursor, "codiceUnivoco")) ;
		persona.setRistampaBadge(getColumnValue(cursor, "ristampaBadge")) ;
		persona.setCognome(" **** ");
		persona.setNome(" **** ");
		persona.setIdGruppo(getColumnValue(cursor, "idGruppo"));
		persona.setCodiceAgesci(getColumnValue(cursor, "codiceAgesci"));
		persona.setIdUnita(getColumnValue(cursor, "idUnita"));
		persona.setContrada(getColumnValue(cursor, "contrada"));
		persona.setQuartiere(getColumnValue(cursor, "quartiere"));
 
		return persona;
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
