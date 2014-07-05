package it.rn2014.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btn = (Button) findViewById(R.id.btn1);
		btn.setOnClickListener(this);
		Button btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(this);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if (requestCode == 5) {
			String res = intent.getExtras().getString("la.droid.qr.result");
			TextView tw = (TextView) findViewById(R.id.text);
			tw.setText("RESULT " + res);
		} else {
			IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if (res != null){
				TextView tw = (TextView) findViewById(R.id.text);
				tw.setText("RESULT: " + res.getContents());
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
