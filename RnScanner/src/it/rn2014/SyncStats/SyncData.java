package it.rn2014.SyncStats;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.rn2014.db.entity.StatisticheScansioni;
import it.rn2014.scanner.CustomHttpClient;

/**
 * This utility let you to manage a communications between client and server in order to upload and download
 * Statistics of access between the device.
 * Created by danger on 12/07/14.
 */
public class SyncData {

    private static String serverUrl="http://www.512b.it/rn2014/";

    /**
     * NOTA: funzione attualmente non implementata
     * 
     * This method return the ArrayList updated that must be added to the local database
     * @param token the auth token that let you to download data
     * @param imei the imei of the phone in order to download only the diff from the database (filtered by imei)
     * @return the arraylist of Statistics that must be added to the local database
     */
    /*
    public static ArrayList<StatisticheScansioni> getUpdate(String token,String imei,String idEvento){
        ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("token",token));
        postParams.add(new BasicNameValuePair("imei", imei));
        postParams.add(new BasicNameValuePair("idEvento", idEvento));
        String res = null;
        String response = null;
        try{
            response = CustomHttpClient.executeHttpPost(serverUrl+"get.php", postParams);
            res = response.toString();
            Log.e("me", res);
            JSONArray jsonArray=new JSONArray(response.toString());
            return JsonToStatList(jsonArray);

        } catch (Exception e) {
            Log.e("me", e.toString());
            return null;
        }

    }*/

    /**
     * This method let you to upload the Statistic from the local database to the remote database,
     * this method upload the database iff the token is accepted from the remote server
     * @param token the the auth token that let you to upload the data
     * @param stat the list of Statistics
     * @return true if the Upload is successful elsewhere if no
     */
    public static boolean postUpdate(String token,ArrayList<StatisticheScansioni> stat){

        String json= StatisticheScansioni.toJSONArray(stat);
        Log.e("Mi aspetto di vedere il json", json);
        ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("token",token));
        postParams.add(new BasicNameValuePair("update", json));
        String res = null;
        String response = null;
        try{
            response = CustomHttpClient.executeHttpPost(serverUrl + "post.php", postParams);
            res = response.toString();
            Log.e("me", res);
            res = res.replaceAll("\\s+","");
            return true;
        } catch (Exception e) {
            Log.e("me", e.toString());
            return false;
        }
    }


    public static  StatisticheScansioni JsonToStat(JSONObject js) throws JSONException,ClassCastException {
        StatisticheScansioni s=new StatisticheScansioni();

        s.setIdScansione(js.getInt("idScansione"));
        s.setCodiceUnivoco(js.getString("codiceUnivoco"));
        Log.e("Dato",js.getString("codiceUnivoco"));
        s.setCodiceRistampa(js.getString("codiceRistampa"));
        s.setTime(js.getString("time"));
        Log.e("Dato",js.getString("time"));
        s.setOperatore(js.getString("operatore"));
        s.setTurno(js.getInt("slot"));
        s.setImei(js.getString("imei"));
        s.setSync(true);
        s.setIdVarco(js.getString("idEvento"));
        return s;
    }

    public static ArrayList<StatisticheScansioni> JsonToStatList(JSONArray jsonArray){
        ArrayList<StatisticheScansioni> ar=new ArrayList<StatisticheScansioni>();
        for(int i=0;i<jsonArray.length();i++) {
            try {
                ar.add(JsonToStat((JSONObject)jsonArray.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ar;

    }
}
