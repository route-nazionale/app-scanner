package it.rn2014.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.widget.Toast;

public class Utils {

	

	// public static String GetString(EditText source) {
	// try {
	// return GetString(source.getText().toString());
	// } catch (Exception ex) {
	// return "";
	// }
	// }
	//
	// public static String GetString(TextView source) {
	// try {
	// return GetString(source.getText().toString());
	// } catch (Exception ex) {
	// return "";
	// }
	// }
	//
	// public static String GetString(Object source) {
	// try {
	// return GetString(source.toString());
	// } catch (Exception ex) {
	// return "";
	// }
	// }

	public static void ShowMessageBox(Context cont, String msg) {
		Toast toast = Toast.makeText(cont, msg, Toast.LENGTH_SHORT);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
		toast.show();
	}

	
	
	public static InputStream getUrlData(String url) {

		InputStream inputStream = null;
		OutputStream outputStream = null;

		// Used to get access to HTTP resources

		DefaultHttpClient client = new DefaultHttpClient();

		HttpGet method;
		try {

			// Retrieves information from the URL

			method = new HttpGet(new URI(url));

			// Gets a response from the client on whether the
			// connection is stable

			HttpResponse res = client.execute(method);
			inputStream = res.getEntity().getContent();

			outputStream = new FileOutputStream(new File("/data/data/test.xml"));
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			// An HTTPEntity may be returned using getEntity() which tells
			// the system where the content is coming from

//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (Exception e) {	
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return inputStream;
	}

}
