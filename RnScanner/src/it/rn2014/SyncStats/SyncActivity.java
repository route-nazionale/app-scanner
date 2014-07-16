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

import it.rn2014.db.entity.StatisticheScansioni;
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
        //login.execute(new String[]{"uploadStatistics"});

        login.execute(new String[]{"downloadStatistics"});

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

                StatisticheScansioni s=new StatisticheScansioni();
                s.setIdScansione(31);
                s.setCodiceUnivoco("anfan");
                s.setCodiceRistampa("amfw");
                s.setTime("2014-07-16 18:07:23");
                s.setOperatore("giggi");
                s.setSlot(5);
                s.setEntrata(true);
                s.setErrore(false);
                s.setImei("anaingana-phoning");
                s.setIdEvento("33");
                ArrayList<StatisticheScansioni> ls=new ArrayList<StatisticheScansioni>();
                ls.add(s);
                startSyncUpload(ls);
            }
            return null;
        }

        public void startSyncDownload(){
            Log.e("startSyncDownload","imei");
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String imei=telephonyManager.getDeviceId();
            Log.e("startSyncDownload",imei);

            ArrayList<StatisticheScansioni> ob= SyncData.getUpdate("FUCK", imei);
            Log.e("startSyncDownload","carico di dati");
            for(int i=0;i<ob.size();i++)
                Log.e("BEN FATTO", ob.get(i).toString());
        }
        public boolean startSyncUpload(ArrayList<StatisticheScansioni> stat){
            SyncData.postUpdate("FUCK",stat);
            return true;
        }
    }

}