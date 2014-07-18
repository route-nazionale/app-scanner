package it.rn2014.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;

public class EventActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		ListView lw = (ListView)findViewById(R.id.listEvent);
		lw.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				
				RadioGroup rg = (RadioGroup)findViewById(R.id.radioTurn);
				int turn = 0;
				switch (rg.getCheckedRadioButtonId()) {
					case R.id.radio1: turn = 1;	break;
					case R.id.radio2: turn = 2; break;
					case R.id.radio3: turn = 3; break;
					default: turn = -1; break;
				}
				
				Intent scan = new Intent(EventActivity.this, ScanningActivity.class);
				scan.putExtra("mode", "event");
				
				UserData.getInstance().setChoose("event");
				UserData.getInstance().setEvent(String.valueOf(position));
				UserData.getInstance().setTurn(turn);
				
				startActivity(scan);
			}
		});
		
	}
}
