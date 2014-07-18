package it.rn2014.scanner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GateResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gate_result);
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("qrscanned")) {
		    String code = extras.getString("qrscanned");
		    if (code == null) finish();
		    if (code.contentEquals("AA-1079-022839-0")){
		    	TextView result = (TextView)findViewById(R.id.result);
		    	result.setText("CODICE CORRETTO");
		    	result.setBackgroundColor(getResources().getColor(R.color.LightGreen));
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	codetext.setText(code);
		    } else {
		    	TextView result = (TextView)findViewById(R.id.result);
		    	result.setText("CODICE ERRATO");
		    	result.setBackgroundColor(getResources().getColor(R.color.LightPink));
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	codetext.setText(code);
		    }
		} else {
			finish();
		}
	}
}
