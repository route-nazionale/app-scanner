package it.rn2014.SyncStats;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.rn2014.scanner.CustomHttpClient;

/**
 * This utility let you to manage a communications between client and server in order to upload and download
 * Statistics of access between the device.
 * Created by danger on 12/07/14.
 */
public class SyncData {

    private static String serverUrl="http://www.512b.it/rn2014/";

    /**
     * This method return the ArrayList updated that must be added to the local database
     * @param token the auth token that let you to download data
     * @param imei the imei of the phone in order to download only the diff from the database (filtered by imei)
     * @return the arraylist of Statistics that must be added to the local database
     */
    public static ArrayList<Statistics> getUpdate(String token,String imei){
        ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("token",token));
        postParams.add(new BasicNameValuePair("imei", imei));
        String res = null;
        String response = null;
        try{
            response = CustomHttpClient.executeHttpPost(serverUrl+"get.php", postParams);
            res = response.toString();
            Log.e("me", res);
            JSONArray jsonArray=new JSONArray(response.toString());
            return JsonToStatisticsList(jsonArray);

        } catch (Exception e) {
            Log.e("me", e.toString());
            return null;
        }

    }

    /**
     * This method let you to upload the Statistic from the local database to the remote database,
     * this method upload the database iff the token is accepted from the remote server
     * @param token the the auth token that let you to upload the data
     * @param stat the list of Statistics
     * @return true if the Upload is successful elsewhere if no
     */
    public static boolean postUpdate(String token,ArrayList<Statistics> stat){

        String json= StatisticsListToJSONArr(stat).toString();
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


    public static  Statistics JsonToStatistics(JSONObject js) throws JSONException {
        Statistics s=new Statistics();
        //s.setIdScansione(js.getInt("idScansione"));
        s.setCodiceUnivoco(js.getString("codiceUnivoco"));
        Log.e("Dato",js.getString("codiceUnivoco"));
        s.setCodiceRistampa(js.getString("codiceRistampa"));
        s.setTime(js.getString("time"));
        Log.e("Dato",js.getString("time"));
        s.setOperatore(js.getString("operatore"));
        s.setSlot(js.getInt("slot"));
        s.setImei(js.getString("imei"));
        s.setErrore(js.getString("errore").equals("1"));
        s.setErrore(js.getString("entrata").equals("1"));;
        s.setSync(true);
        return s;
    }

    public static ArrayList<Statistics> JsonToStatisticsList(JSONArray jsonArray){
        ArrayList<Statistics> ar=new ArrayList<Statistics>();
        for(int i=0;i<jsonArray.length();i++) {
            try {
                ar.add(JsonToStatistics((JSONObject)jsonArray.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ar;

    }
    /**
     * This method could be moved into Statistics
     * @param s the row of Statistics table
     * @return the json version of the row
     * @throws JSONException
     */
    public static JSONObject StatisticsToJSON(Statistics s) throws JSONException {
        JSONObject js=new JSONObject();
        js.put("idScansione",s.getIdScansione());
        js.put("codiceUnivoco",s.getCodiceUnivoco());
        js.put("codiceRistampa",s.getCodiceRistampa());
        js.put("time",s.getTime());
        js.put("operatore",s.getOperatore());
        js.put("slot",s.getSlot());
        js.put("imei",s.getImei());
        js.put("errore",s.isErrore());
        js.put("entrata",s.isEntrata());
        return js;
    }

    /**
     * This method merge the Statistics List into a jason object
     * @param stat the Statistic List row by row
     * @return a Json Object fulfill with the Statistics
     */
    public static JSONObject StatisticsListToJSONArr(ArrayList<Statistics> stat){
        JSONArray jsonArr = new JSONArray();

        for (Statistics s : stat ) {
            try {
                jsonArr.put(StatisticsToJSON(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject js=new JSONObject();
        try {
            js.put("update",jsonArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js;
    }
}
