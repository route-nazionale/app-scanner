package it.rn2014.scanner;

import java.util.ArrayList;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AuthActivity extends ActionBarActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		
		Button btnPass = (Button)findViewById(R.id.btnPass);
		btnPass.setOnClickListener(this);
		
		Button btnBadge = (Button)findViewById(R.id.btnBadge);
		btnBadge.setOnClickListener(this);
			
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnBadge){
			IntentIntegrator ii = new IntentIntegrator(this);
			ArrayList<String> formats = new ArrayList<String>();
			formats.add("QR_CODE");
			ii.initiateScan(formats);
		} else if (v.getId() == R.id.btnPass){
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(login);
		}
			
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != null && !scanResult.getContents().contentEquals("")) {
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			login.putExtra("qrscanned", scanResult.getContents());
			startActivity(login);
		}
	}
}
