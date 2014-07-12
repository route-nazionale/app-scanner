package it.rn2014.scanner;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	
	Intent batteryStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btn = (Button) findViewById(R.id.btn1);
		btn.setOnClickListener(this);
		Button btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(this);
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
		
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float)scale;
		
		TextView tw = (TextView)findViewById(R.id.text);
		tw.setText("BATTERIA: " + batteryPct);
		
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if (requestCode == 5) {
			String res = intent.getExtras().getString("la.droid.qr.result");
			TextView tw = (TextView) findViewById(R.id.text);
			tw.setText("RESULT " + res);
			Toast.makeText(getApplicationContext(), "Result: " + res, Toast.LENGTH_SHORT).show();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

			float batteryPct = level / (float)scale;
			
			if (batteryPct < 0.10){
				try {
				    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				    r.play();
				} catch (Exception e) {
				    e.printStackTrace();
				}
			}
			
			Intent it = new Intent("la.droid.qr.scan");
			it.putExtra("la.droid.qr.complete", true);
			startActivityForResult(it, 5);
		} else {
			IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if (res != null){
				TextView tw = (TextView) findViewById(R.id.text);
				tw.setText("RESULT: " + res.getContents());
				Toast.makeText(getApplicationContext(), "Result: " + res, Toast.LENGTH_SHORT).show();
				 
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

				float batteryPct = level / (float)scale;
				
				if (batteryPct < 0.10){
					try {
					    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
					    r.play();
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}
				
				
				IntentIntegrator ii = new IntentIntegrator(this);
				ii.initiateScan();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		
		if (v.getId() == R.id.btn1){
			IntentIntegrator ii = new IntentIntegrator(this);
			ii.initiateScan();
		} else if (v.getId() == R.id.btn2) {
			Intent it = new Intent("la.droid.qr.scan");
			it.putExtra("la.droid.qr.complete", true);
			startActivityForResult(it, 5);
		}
		
	}
}
