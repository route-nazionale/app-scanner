package it.rn2014.scanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GateResultActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gate_result);
		
		Button enter = (Button)findViewById(R.id.btnEnter);
		Button exit = (Button)findViewById(R.id.btnExit);
		Button abort = (Button)findViewById(R.id.btnAbort);
		enter.setOnClickListener(this);
		exit.setOnClickListener(this);
		abort.setOnClickListener(this);
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("qrscanned")) {
		    String code = extras.getString("qrscanned");
		    if (code == null) finish();
		    if (code.contentEquals("AA-1079-022839-0")){
		    	TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.autorizzato));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightGreen));
		    	
		    } else if (code.contentEquals("AA-1079-022840-0")) {

		    	TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.non_autorizzato));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightPink));
		    	
		    	exit.setVisibility(View.INVISIBLE);
		    	enter.setVisibility(View.INVISIBLE);
		    	
		    } else {
		    	TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.invalido));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightBlue));
		    	
		    	exit.setVisibility(View.INVISIBLE);
		    	enter.setVisibility(View.INVISIBLE);
		    }
		} else {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		if (v.getId() == R.id.btnEnter){
			t.setText("Accesso al varco registrato");
		} else if (v.getId() == R.id.btnExit) {
			t.setText("Escita dal varco registrata");
		} else if (v.getId() == R.id.btnAbort) {
			t.setText("Scansione annullata");
		}
		t.show();
		finish();
	}
}
