package it.rn2014.scanner;

import it.rn2014.db.DataManager;
import it.rn2014.db.entity.Evento;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EventActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		final ArrayList<Evento> turn1 = DataManager.getInstance(EventActivity.this).findEventiToCheckin(
				UserData.getInstance().getCUnoReprint(), "1");
		final ArrayList<Evento> turn2 = DataManager.getInstance(EventActivity.this).findEventiToCheckin(
				UserData.getInstance().getCUnoReprint(), "2");
		final ArrayList<Evento> turn3 = DataManager.getInstance(EventActivity.this).findEventiToCheckin(
				UserData.getInstance().getCUnoReprint(), "3");		
		
		final ArrayAdapter<Evento> adapter1 = new ArrayAdapter<Evento>(
				this, android.R.layout.simple_list_item_1, turn1);
		final ArrayAdapter<Evento> adapter2 = new ArrayAdapter<Evento>(
				this, android.R.layout.simple_list_item_1, turn2);
		final ArrayAdapter<Evento> adapter3 = new ArrayAdapter<Evento>(
				this, android.R.layout.simple_list_item_1, turn3);
		
		final ListView lw = (ListView)findViewById(R.id.listEvent);
		final RadioGroup rg = (RadioGroup)findViewById(R.id.radioTurn);
		
		rg.check(R.id.radio1);
		lw.setAdapter(adapter1);
		
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (rg.getCheckedRadioButtonId()) {
					case R.id.radio1: lw.setAdapter(adapter1);	break;
					case R.id.radio2: lw.setAdapter(adapter2); break;
					case R.id.radio3: lw.setAdapter(adapter3); break;
				}
			}
		});
		
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
				String eventcode = null;
				switch (turn) {
				case 1: eventcode = turn1.get(0).getCodiceEvento(); break;
				case 2: eventcode = turn2.get(0).getCodiceEvento(); break;
				case 3: eventcode = turn3.get(0).getCodiceEvento(); break;
				}
				UserData.getInstance().setEvent(eventcode);
				UserData.getInstance().setTurn(turn);
				
				startActivity(scan);
			}
		});
	}
}
