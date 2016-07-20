package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener httpCallbackListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//HttpURLConnection
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(7000);
					InputStream is = connection.getInputStream();
					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(is));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = bReader.readLine()) != null) {
						response.append(line);
					}
					if (httpCallbackListener != null) {
						// �ص�onFinish()����
						httpCallbackListener.onFinish(response.toString());
						Log.d("���ص������ǣ�-->", response.toString());
					}
				} catch (Exception e) {
					
					if (httpCallbackListener != null) {
						// �ص�onError()����
						httpCallbackListener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
