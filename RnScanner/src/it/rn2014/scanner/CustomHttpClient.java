package it.rn2014.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Classe che contiene un HttpClient per effettuare richeste GET/POST
 * 
 * @author Nicola Corti
 */
public class CustomHttpClient {

	/** Timeout */
	public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds
	/** Riferimento al client */
	private static HttpClient mHttpClient;

	/**
	 * Ritorna un istanza di HttpClient (singleton)
	 * 
	 * @return un HttpClient con i parametri di connessione configurati
	 */
	private static HttpClient getHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return mHttpClient;
	}

	/**
	 * Esegue una richiesta Http POST
	 * 
	 * @param url Indirizzo della richiesta
	 * @param postParameters parametri da inviare
	 * @return Risultato della richiesta in formato HttpResponse
	 * @throws Exception
	 */
	public static HttpResponse executeHttpPost(String url,
			ArrayList<NameValuePair> postParameters) throws Exception {
		HttpClient client = getHttpClient();
		HttpPost request = new HttpPost(url);

		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
				postParameters);
		request.setEntity(formEntity);

		HttpResponse response = client.execute(request);
		return response;
	}

	/**
	 * Esegue una richiesta Http POST e torna una stringa
	 * 
	 * @param url Indirizzo della richiesta
	 * @param postParameters parametri da inviare
	 * @return Risultato della richiesta in formato Stringa
	 * @throws Exception
	 */
	public static String executeHttpPostString(String url,
			ArrayList<NameValuePair> postParameters) throws Exception {

		HttpResponse resp = executeHttpPost(url, postParameters);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(resp.getEntity()
					.getContent()));

			// Parser della risposta HTTP
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String result = sb.toString();
			return result;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Esegue una richiesta Http GET
	 * 
	 * @param url Indirizzo della richiesta
	 * @return Risultato della richiesta in formato Stringa
	 * @throws Exception
	 */
    public static String executeHttpGet(String url) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}