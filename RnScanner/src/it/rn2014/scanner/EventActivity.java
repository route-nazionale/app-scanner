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

/**
 * Activity che gestisce l'elenco degli eventi di cui si puo' fare l'autenticazione
 * come capospalla
 * 
 * @author Nicola Corti
 */
public class EventActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		// Filtro le liste eventi dei capispalla
		final ArrayList<Evento> turn1 = DataManager.getInstance(EventActivity.this).findEventiToCheckin(
				UserData.getInstance().getCUnoReprint(), "1");
		final ArrayList<Evento> turn2 = DataManager.getInstance(EventActivity.this).findEventiToCheckin(
				UserData.getInstance().getCUnoReprint(), "2");
		final ArrayList<Evento> turn3 = DataManager.getInstance(EventActivity.this).findEventiToCheckin(
				UserData.getInstance().getCUnoReprint(), "3");		
		
		// Mi creo 3 array adapter per la lista
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
		
		// Imposto il listner del radiobutton in modo che aggiorni l'elenco
		// degli eventi disponibili.
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

				// Recupero il turno che e' stato premuto
				RadioGroup rg = (RadioGroup)findViewById(R.id.radioTurn);
				int turn = 0;
				switch (rg.getCheckedRadioButtonId()) {
					case R.id.radio1: turn = 1;	break;
					case R.id.radio2: turn = 2; break;
					case R.id.radio3: turn = 3; break;
					default: turn = -1; break;
				}
				
				// Vado in modalita' scansione e passo il parametro `event`
				Intent scan = new Intent(EventActivity.this, ScanningActivity.class);
				scan.putExtra("mode", "event");
				
				// Salvo nello stato globale la scelta appena fatta				
				String eventcode = null;
				switch (turn) {
				case 1: eventcode = turn1.get(0).getCodiceEvento(); break;
				case 2: eventcode = turn2.get(0).getCodiceEvento(); break;
				case 3: eventcode = turn3.get(0).getCodiceEvento(); break;
				}
				UserData.getInstance().setEvent(eventcode);
				UserData.getInstance().setTurn(turn);
				UserData.getInstance().setChoose("event");
				
				startActivity(scan);
			}
		});
	}
}