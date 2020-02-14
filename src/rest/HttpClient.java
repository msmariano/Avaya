package rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import util.Log;

public class HttpClient {

	public static String deleteMethod(HttpURLConnection con, String contentBody) {

		StringBuilder responseContent = new StringBuilder();
		try {
			con.setRequestProperty("Content-Length", String.valueOf(contentBody.length()));

			con.setRequestMethod(HttpMethod.DELETE);
			con.setDoInput(true);
			con.setDoOutput(true);

			DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
			outputStream.write(contentBody.toString().getBytes());

			outputStream.flush();
			outputStream.close();

			String output = null;
			if (con.getResponseCode() != 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				while ((output = br.readLine()) != null) {
					Log.grava(output);
					responseContent.append(output);
				}

			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				output = null;
				while ((output = br.readLine()) != null) {
					Log.grava(output);
					responseContent.append(output);
				}
			}
		} catch (Exception e) {

		}

		return responseContent.toString();
	}

	public static String postMethod(HttpURLConnection con, String contentBody) {

		StringBuilder responseContent = new StringBuilder();
		BufferedReader br = null;
		try {
			con.setRequestProperty("Content-Length", String.valueOf(contentBody.length()));

			con.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

			con.setRequestMethod(HttpMethod.POST);
			con.setDoInput(true);
			con.setDoOutput(true);

			DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
			outputStream.write(contentBody.toString().getBytes());

			outputStream.flush();
			outputStream.close();

			String output = null;
			if (con.getResponseCode() != 200) {
				if (con.getErrorStream() != null) {
					br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
					if (br != null) {
						while ((output = br.readLine()) != null) {
							Log.grava(output);
							responseContent.append(output);
						}

					}
				}

			} else {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				output = null;
				while ((output = br.readLine()) != null) {
					Log.grava(output);
					responseContent.append(output);
				}
			}
		} catch (Exception e) {
			Log.grava(e.getMessage());

		}

		return responseContent.toString();
	}

	public static String getMethod(HttpURLConnection con) {

		StringBuilder responseContent = new StringBuilder();
		try {

			con.setRequestMethod(HttpMethod.GET);

			if (con.getResponseCode() != 200) {
				throw new Exception("");
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String output = null;

			while ((output = br.readLine()) != null) {
				Log.grava(output);
				responseContent.append(output);
			}
		} catch (Exception e) {

		}

		return responseContent.toString();
	}

	public static HttpURLConnection httpConnect(String endPoint, String resource) {

		// ProxyUtils.setProxyConfigs();
		HttpURLConnection con = null;
		String httpURL = endPoint + resource;

		URL url = null;
		try {
			url = new URL(httpURL);
			con = (HttpURLConnection) url.openConnection();

			con.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
			con.setRequestProperty(HttpHeaders.HOST, url.getHost());
			con.setRequestProperty(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

			con.setUseCaches(false);
		} catch (Exception e) {
			Log.grava("HttpURLConnection: "+e.getMessage());
		}

		return con;

	}

}
