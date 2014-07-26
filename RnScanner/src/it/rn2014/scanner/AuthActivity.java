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

/**
 * Activity che mostra la selezione per il tipo di login
 * 
 * @author Nicola Corti
 *
 */
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
			
			// Se ho premuto badge faccio partire una scansione
			IntentIntegrator ii = new IntentIntegrator(this);
			ArrayList<String> formats = new ArrayList<String>();
			formats.add("QR_CODE"); 	//Limito alla scansione QR
			ii.initiateScan(formats);
		} else if (v.getId() == R.id.btnPass){
			
			// Se ho premuto codice a mano faccio partire il login
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(login);
		}
			
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		/*	Viene invocata quando torno da una scansione QR
		 * 	Se il contenuto va bene allora faccio partire la schermata login
		 */
		
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != null && !scanResult.getContents().contentEquals("")) {
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			
			// Inserisco parametro qrscanned
			login.putExtra("qrscanned", scanResult.getContents());
			startActivity(login);
		}
	}
}
