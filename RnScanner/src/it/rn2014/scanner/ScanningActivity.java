package it.rn2014.scanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanningActivity extends ActionBarActivity implements OnClickListener {

	String mode = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanning);
		
		Button btnScan = (Button)findViewById(R.id.btnBadge);
		btnScan.setOnClickListener(this);
		Button btnWrite = (Button)findViewById(R.id.btnWrite);
		btnWrite.setOnClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("mode")) {
		    mode = extras.getString("mode");
		    if (mode.contentEquals("gate")){
				
		    	TextView title = (TextView)findViewById(R.id.title);
				title.setText(R.string.title_gate);
				TextView description = (TextView)findViewById(R.id.description);
				description.setText(R.string.desc_gate);
				
				
		    } else if (mode.contentEquals("lab")) {
		    	
		    } else if (mode.contentEquals("identify")) {
		    	TextView title = (TextView)findViewById(R.id.title);
				title.setText(R.string.title_identify);
				TextView description = (TextView)findViewById(R.id.description);
				description.setText(R.string.desc_identify);
		    }
		} else {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnWrite){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("Codice Badge");
			alert.setMessage("Scrivi il codice badge da scansionare");

			final EditText code = new EditText(this);
			code.setHint(R.string.prompt_code);
			alert.setView(code);

			alert.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = code.getText().toString();
					Intent login;
					// TODO da completare
					if (mode.contentEquals("identify")) login = new Intent(getApplicationContext(), GateResultActivity.class);
					else if (mode.contentEquals("gate")) login = new Intent(getApplicationContext(), GateResultActivity.class);
					else login = new Intent(getApplicationContext(), GateResultActivity.class);
					
					login.putExtra("qrscanned", value);
					startActivity(login);
				}
			});

			alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			alert.show();
		} else if (v.getId() == R.id.btnBadge) {
			IntentIntegrator ii = new IntentIntegrator(this);
			ii.initiateScan();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			Intent login = new Intent(getApplicationContext(), GateResultActivity.class);
			login.putExtra("qrscanned", scanResult.getContents());
			startActivity(login);
		}
	}
}
