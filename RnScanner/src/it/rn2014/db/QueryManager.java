/**
 * 
 */
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
 
		persona.setNome(getColumnValue(cursor, "nome"));
		persona.setCognome(getColumnValue(cursor, "cognome"));
		persona.setCodiceAgesci(getColumnValue(cursor, "codAgesci"));
		persona.setGruppo(getColumnValue(cursor, "gruppo"));
		persona.setRuolo(getColumnValue(cursor, "ruolo"));
		persona.setCodiceBadge(getColumnValue(cursor, "codiceBadge"));
		return persona;
	}

	

	// public boolean SaveEmployee(String name, String email)
	// {
	// try
	// {
	// ContentValues cv = new ContentValues();
	// cv.put("Name", name);
	// cv.put("Email", email);
	//
	// database.insert("Employees", null, cv);
	//
	// Log.d("SaveEmployee", "informationsaved");
	// return true;
	//
	// }
	// catch(Exception ex)
	// {
	// Log.d("SaveEmployee", ex.toString());
	// return false;
	// }
	// }

}
