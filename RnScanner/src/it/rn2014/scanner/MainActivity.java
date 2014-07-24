package it.rn2014.scanner;

import it.rn2014.db.QueryManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	// declare the dialog as a member field of your activity
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView cu = (TextView)findViewById(R.id.cu);
		cu.setText(UserData.getInstance().getCU());
		Button btnGate = (Button)findViewById(R.id.btnGate);
		btnGate.setOnClickListener(this);
		Button btnLogout = (Button)findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(this);
		Button btnEvents = (Button)findViewById(R.id.btnEvents);
		btnEvents.setOnClickListener(this);
		Button btnIdentify = (Button)findViewById(R.id.btnIdentify);
		btnIdentify.setOnClickListener(this);
		Button btnSync = (Button)findViewById(R.id.btnSyncro);
		btnSync.setOnClickListener(this);

		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setMessage("Download dei dati Route Nazionale");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		if (!haveNetworkConnection() && !QueryManager.getInstance(this).checkDataBase()){
			
		    AlertDialog.Builder adb = new AlertDialog.Builder(this);
		    adb.setTitle("Connessione Assente");
		    adb.setMessage("Non sono presenti i dati della Route Nazionale sul device! Devi essere connesso ad Internet per scaricarli");
		    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
		      } });
		    adb.show();
		} else {
			DownloadTask dt = new DownloadTask(MainActivity.this);
			dt.execute("http://mobile.rn2014.it/rn2014.db.gz");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGate:
			Intent gate = new Intent(getApplicationContext(), ScanningActivity.class);
			gate.putExtra("mode", "gate");
			UserData.getInstance().setChoose("gate");
			startActivity(gate);
			break;
		case R.id.btnLogout:
			UserData.getInstance().logOut();
			UserData.saveInstance(getApplicationContext());
			Intent login = new Intent(getApplicationContext(), AuthActivity.class);
			startActivity(login);
			finish();
			break;
		case R.id.btnEvents:
			Intent event = new Intent(getApplicationContext(), EventActivity.class);
			startActivity(event);
			break;
		case R.id.btnIdentify:
			Intent ident = new Intent(getApplicationContext(), ScanningActivity.class);
			ident.putExtra("mode", "identify");
			UserData.getInstance().setChoose("identify");
			startActivity(ident);
			break;
		case R.id.btnSyncro:
			Intent sync = new Intent(getApplicationContext(), SyncroActivity.class);
			startActivity(sync);
			break;
		default:
			break;
		}
	}

	
	private class DownloadTask extends AsyncTask<String, Integer, String> {

	    private Context context;
	    private PowerManager.WakeLock mWakeLock;
	    private boolean alreadyExists = false;
	    private String db_path = Environment.getDataDirectory() + "/data/" + getApplication().getPackageName() + "/databases/";

	    public DownloadTask(Context context) {
	        this.context = context;
	    }

	    @Override
	    protected String doInBackground(String... sUrl) {
	        
	    	InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        InputStream md5Stream = null;
	        
	        if (alreadyExists) {
	        	
				String res = null;
				String response = null;
				try{
					response = CustomHttpClient.executeHttpGet("http://mobile.rn2014.it/md5.php");
					res = response.toString();
					res = res.replaceAll("\\s+","");
					File file = new File(context.getDir("db", Context.MODE_PRIVATE), "rn2014.db.gz");
					
					md5Stream = new FileInputStream(file);
					byte[] buffer = new byte[1024];
					MessageDigest digest = MessageDigest.getInstance("MD5");
					int numRead = 0;
					while (numRead != -1) {
						numRead = md5Stream.read(buffer);
						if (numRead > 0)
							digest.update(buffer, 0, numRead);
					}
					byte [] md5Bytes = digest.digest();
					String computed = MainActivity.convertHashToString(md5Bytes);
					
					if (res.contentEquals(computed))
						return null; // Vuol dire che il file e' gia' esistente
					else
						alreadyExists = false;
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (md5Stream != null) md5Stream.close();
					} catch (IOException e) { e.printStackTrace(); }
				}
	        }
	        try {
	            URL url = new URL(sUrl[0]);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = connection.getInputStream();
	            File db = new File(context.getDir("db", Context.MODE_PRIVATE), "rn2014.db.gz");
	            output = new FileOutputStream(db);

	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }
	            output.close();
	       	 	GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(db));
	       	 	File db_dir = new File(db_path);
	       	 	if (!db_dir.exists()) db_dir.mkdir();
	       	 	File db_ungzip = new File(db_path, "rn2014.db");
	         	FileOutputStream out = new FileOutputStream(db_ungzip);
	      
	            int len;
	            byte[] buffer = new byte[1024];
	            while ((len = gzis.read(buffer)) > 0) {
	             	out.write(buffer, 0, len);
	            }
	      
	            gzis.close();
	         	out.close();
	      
	        } catch (Exception e) { 
	        	e.printStackTrace();
	        	return e.toString();
	        } finally {
	            try {
	                if (input != null) input.close();
		            if (connection != null) connection.disconnect();
	            } catch (IOException ignored) { }
	        }
	        return null;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        File file = new File(context.getDir("db", Context.MODE_PRIVATE), "rn2014.db.gz");
	        File file_ungzip = new File(db_path, "rn2014.db");
	        if(file.exists() && file_ungzip.exists()){
	        	alreadyExists = true;
	        } else {
		        // Blocca lo spegnimento del download su blocco schermo
		        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
		        mWakeLock.acquire();
		        mProgressDialog.show();
	        }
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        mProgressDialog.setIndeterminate(false);
	        mProgressDialog.setMax(100);
	        mProgressDialog.setProgress(progress[0]);
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        if (!alreadyExists){
	        	mWakeLock.release();
	        	mProgressDialog.dismiss();
	        	if (result != null)
	        		Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
	        	else
	        		Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
	        }
	    }
	}
	
    private static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal;
    }
    
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
