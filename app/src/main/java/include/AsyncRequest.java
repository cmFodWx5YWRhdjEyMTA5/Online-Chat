package include;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncRequest extends AsyncTask<String, String, String> {

	public AsyncResponse delegate = null;

	@Override
	protected void onPreExecute() {
		//
	}

	@Override
	protected String doInBackground(String... params) {
		return GET(params[0]);
	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	@Override
	protected void onProgressUpdate(String... params) {
		// Update a progress bar here, or ignore it, it's up to you
	}

	@Override
	protected void onCancelled() {
		// Handle what you want to do if you cancel this task
	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);
		delegate.processFinish(result);

	};

}
