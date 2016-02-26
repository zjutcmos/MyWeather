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
					//Log.d("http----->", "-1-");
					if (httpCallbackListener != null) {
						// 回调onFinish()方法
						httpCallbackListener.onFinish(response.toString());
					}
				} catch (Exception e) {
					
					if (httpCallbackListener != null) {
						// 回调onError()方法
						httpCallbackListener.onError(e);
					}
					//Log.d("http----->", "-2-");
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					//Log.d("http----->", "-3-");
				}
			}
		}).start();
	}

}
