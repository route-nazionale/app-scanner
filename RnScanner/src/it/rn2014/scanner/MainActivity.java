package it.rn2014.scanner;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGate:
			// TODO Da implementare	
			break;
		case R.id.btnLogout:
			// TODO Da implementare	
			break;
		case R.id.btnEvents:
			// TODO Da implementare	
			break;
		case R.id.btnIdentify:
			// TODO Da implementare	
			break;
		case R.id.btnSyncro:
			// TODO Da implementare	
			break;
		default:
			break;
		}
	}
}
