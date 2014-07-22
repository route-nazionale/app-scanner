package it.rn2014.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView cu = (TextView)findViewById(R.id.cu);
		cu.setText(UserData.getInstance().getCU());
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
			Intent gate = new Intent(getApplicationContext(), ScanningActivity.class);
			gate.putExtra("mode", "gate");
			UserData.getInstance().setChoose("gate");
			startActivity(gate);
			break;
		case R.id.btnLogout:
			UserData.getInstance().logOut();
			UserData.saveInstance(getApplicationContext());
			Intent login = new Intent(getApplicationContext(), AuthActivity.class);
			startActivity(login);
			finish();
			break;
		case R.id.btnEvents:
			Intent event = new Intent(getApplicationContext(), EventActivity.class);
			startActivity(event);
			break;
		case R.id.btnIdentify:
			Intent ident = new Intent(getApplicationContext(), ScanningActivity.class);
			ident.putExtra("mode", "identify");
			UserData.getInstance().setChoose("identify");
			startActivity(ident);
			break;
		case R.id.btnSyncro:
			Intent sync = new Intent(getApplicationContext(), SyncroActivity.class);
			startActivity(sync);
			break;
		default:
			break;
		}
	}
}
