package it.rn2014.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AuthActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		
		//Button btnBadge = (Button)findViewById(R.id.btnBadge);
		Button btnPass = (Button)findViewById(R.id.btnPass);
		
		btnPass.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent pass = new Intent(AuthActivity.this, LoginActivity.class);
				startActivity(pass);
			}
		});
	}
}
