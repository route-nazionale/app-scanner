package it.rn2014.scanner;

import it.rn2014.db.StatsManager;
import it.rn2014.db.entity.StatisticheScansioni;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class RemoteResources {

	private static final String URL_MD5 = "http://mobile.rn2014.it/md5.php";
	private static final String URL_DB = "http://mobile.rn2014.it/rn2014.db.gz";
	private static final String URL_UPDATES = "http://mobile.rn2014.it/post.php";
	
	
	public static class DownloadTask extends AsyncTask<String, Integer, String> {

		private final String DB_NAME = "rn2014.db";
		private final String DB_DOWNLOAD_DIR = "db";
		private final String DB_NAME_GZ = "rn2014.db.gz";
		
	    private Context context;
	    private PowerManager.WakeLock mWakeLock;
	    protected boolean alreadyExists = false;
	    private String DB_DIR = "";

	    public DownloadTask(Context context) {
	        this.context = context;
	        DB_DIR = Environment.getDataDirectory() + "/data/" + context.getApplicationContext().getPackageName() + "/databases/";
	    }

	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        File file = new File(context.getDir(DB_DOWNLOAD_DIR, Context.MODE_PRIVATE), DB_NAME_GZ);
	        File file_ungzip = new File(DB_DIR, DB_NAME);
	        
	        // Controllo se i files esistono
	        if(file.exists() && file_ungzip.exists()){
	        	alreadyExists = true;
	        } else {
		        // Blocca lo spegnimento del download su blocco schermo
		        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
		        mWakeLock.acquire();
		        //mProgressDialog.show();
	        }
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
					response = CustomHttpClient.executeHttpGet(URL_MD5);
					res = response.toString();
					res = res.replaceAll("\\s+","");
					File file = new File(context.getDir(DB_DOWNLOAD_DIR, Context.MODE_PRIVATE), DB_NAME_GZ);
					
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
					String computed = convertHashToString(md5Bytes);
					
					if (res.contentEquals(computed))
						return null; // Vuol dire che il file e' gia' esistente
					else
						alreadyExists = false;
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try { if (md5Stream != null) md5Stream.close();
					} catch (IOException e) { e.printStackTrace(); }
				}
	        }
	        try {
	            
	        	onStartDownload();
	        	URL url = new URL(URL_DB);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            // Controllo il codice che mi ritorna il server
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Errore server HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = connection.getInputStream();
	            File db = new File(context.getDir(DB_DOWNLOAD_DIR, Context.MODE_PRIVATE), DB_NAME_GZ);
	            output = new FileOutputStream(db);

	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }
	            output.close();
	            
	            // Decomprimo il file
	       	 	GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(db));
	       	 	File db_dir = new File(DB_DIR);
	       	 	if (!db_dir.exists()) db_dir.mkdir();
	       	 	File db_ungzip = new File(DB_DIR, DB_NAME);
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
	    protected void onPostExecute(String result) {
	        
	    	if (!alreadyExists){
	    		mWakeLock.release();
	        }
	    }
	    
	    /**
	     * Metodo che viene invocato quando parte effettivamente il download
	     */
	    protected void onStartDownload() {
	    }
	}
	
	public static class SendTask extends AsyncTask<Void, Void, Integer>{

		Context c;
		
		public  SendTask(Context context) {
			this.c = context;
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			
			StatsManager qm = StatsManager.getInstance(c);
            ArrayList<StatisticheScansioni> ls = qm.findAllStatsNotSync();
            if (ls.size() == 0) return -1;
            
	        String json= StatisticheScansioni.toJSONArray(ls);
	        Log.e("Mi aspetto di vedere il json", json);
	        
	        ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
	        
	        postParams.add(new BasicNameValuePair("cu", UserData.getInstance().getCU().substring(0, UserData.getInstance().getCU().length()-2)));
	        postParams.add(new BasicNameValuePair("reprint", UserData.getInstance().getCU().substring(UserData.getInstance().getCU().length()-1)));
	        
	        postParams.add(new BasicNameValuePair("date", UserData.getInstance().getDate()));
	        postParams.add(new BasicNameValuePair("json", json));
	        
            TelephonyManager telephonyManager = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
	        postParams.add(new BasicNameValuePair("imei", telephonyManager.getDeviceId()));
	        
	        int ret = 0;
	        try{
	            HttpResponse response = CustomHttpClient.executeHttpPost(URL_UPDATES, postParams);
	            String res = response.getStatusLine().toString();
	            Log.e("Risposta HTTP", res);
	            ret = response.getStatusLine().getStatusCode();
	            
	            if (response.getStatusLine().getStatusCode() == 200){
	            	StatsManager.getInstance(c).updateSyncStats();
	            }
	        } catch (Exception e) {
	            Log.e("me", e.toString());
	        }
	        return ret;
		}
		
	    @Override
	    protected void onPostExecute(Integer ret) {
	    	Log.e("retcode", ret + "");
	    }
		
	}
	
    public static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal;
    }
    
    public static boolean haveNetworkConnection(Context c) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
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
