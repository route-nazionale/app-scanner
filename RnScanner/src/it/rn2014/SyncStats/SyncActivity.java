package it.rn2014.SyncStats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Objects;

import it.rn2014.db.entity.StatisticheScansioni;
import it.rn2014.scanner.MainActivity;
import it.rn2014.scanner.R;

/**
 * Created by danger on 13/07/14.
 */
public class SyncActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        final ProgressBar progDatabase= (ProgressBar)findViewById(R.id.progDatabase);
        final CheckBox checkDatabase= (CheckBox)findViewById(R.id.checkDatabase);

        final ProgressBar progDownStats= (ProgressBar)findViewById(R.id.progDownStat);
        final CheckBox checkDownStats= (CheckBox)findViewById(R.id.checkDownStat);


        final ProgressBar progUpStats= (ProgressBar)findViewById(R.id.progUpStat);

        final CheckBox checkUpStats =(CheckBox)findViewById(R.id.checkUpStat);

        DownloadTask sync2 = new DownloadTask(progDownStats,checkDownStats);
        //login.execute(new String[]{"uploadStatistics"});

        sync2.execute(new String[]{"downloadStatistics"});

        DownloadTask sync1 = new DownloadTask(progUpStats,checkUpStats);
        sync1.execute(new String[]{"downloadStatistics"});

        checkDownStats.setFocusable(false);
        checkDownStats.setFocusable(false);
        checkUpStats.setFocusable(false);

    }
    public class DownloadTask  extends AsyncTask<String, Void, String> {
        ProgressBar prb;
        CheckBox check;
        public DownloadTask(ProgressBar p,CheckBox c){
            this.prb=p;
            this.check=c;
        }

        @Override
        protected void onPreExecute() {
            prb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(), "Download Riuscito", Toast.LENGTH_SHORT).show();
                check.setChecked(true);
                prb.setVisibility(View.GONE);
            }
            else{
                Toast.makeText(getApplicationContext(), "Download Fallito", Toast.LENGTH_SHORT).show();
            }

        }
        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
            boolean res=false;
            if(params[0].equals("downloadStatistics")) {
                res=startSyncDownload();
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
                res=startSyncUpload(ls);
            }
            if(res)
                return "1";
            else
                return "0";
        }

        public boolean startSyncDownload(){
            Log.e("startSyncDownload","imei");
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String imei=telephonyManager.getDeviceId();
            Log.e("startSyncDownload",imei);

            ArrayList<StatisticheScansioni> ob= SyncData.getUpdate("FUCK", imei);
            Log.e("startSyncDownload","carico di dati");
            for(int i=0;i<ob.size();i++)
                Log.e("BEN FATTO", ob.get(i).toString());

            return true;
        }
        public boolean startSyncUpload(ArrayList<StatisticheScansioni> stat){
            return SyncData.postUpdate("FUCK",stat);
        }
    }

}