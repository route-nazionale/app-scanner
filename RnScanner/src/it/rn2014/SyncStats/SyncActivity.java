package it.rn2014.SyncStats;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Objects;

import it.rn2014.scanner.R;

/**
 * Created by danger on 13/07/14.
 */
public class SyncActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        DownloadTask login = new DownloadTask();
        login.execute(new String[]{"updateStatistics"});

    }
    public class DownloadTask  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
            if(params[0].equals("downloadStatistics")) {
                startSyncDownload();
            }
            if(params[0].equals("uploadStatistics")) {
                //TODO SELECT FROM LOCAL DATABASE THE VALUE FROM THE IMEI
                startSyncUpload(null);
            }
            return null;
        }

        public void startSyncDownload(){
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String imei=telephonyManager.getDeviceId();

            ArrayList<Statistics> ob= SyncData.getUpdate("FUCK", imei);
            for(int i=0;i<ob.size();i++)
                Log.e("BEN FATTO", ob.get(i).toString());
        }
        public boolean startSyncUpload(ArrayList<Statistics> stat){
            SyncData.postUpdate("FUCK",stat);
            return true;
        }
    }

}