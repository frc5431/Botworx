package org.usfirst.frc.team5431.robot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.json.JSONObject;
import org.json.JSONArray;

public class ThingWorx {

	private HttpsURLConnection connection = null;
	private final String baseUrl = "https://gj6mbgz0.pp.vuforia.io:8443/Thingworx",
			userName = "first", userPass = "Rob0t1cs";

	private String selected_thing = "academyRobot";

	private final String[] header_h = { "Connection", "X-Requested-With", "Content-Type", "DNT", "Accept-Encoding",
			"Accept" },
			header_b = { "keep-alive", "XMLHttpRequest", "application/json", "1", "gzip, deflate",
					"application/json, application/json-compressed, text/javascript, */*, q=0.01" };

	public ThingWorx() {

	}

	private void print_https_cert(HttpsURLConnection con) {

		if (con != null) {

			try {

				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");

				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
					System.out.println("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public String put_property(String JSON) throws Exception {
		final String props_path = baseUrl + "/Things/" + selected_thing + "/Properties/*";
		System.out.println("Sending " + JSON + " To: " + props_path);
		return request("PUT", props_path, JSON);
	}

	public JSONObject get_property() throws Exception {
		try {
			final String props_path = baseUrl + "/Things/" + selected_thing + "/Properties/";
			System.out.println("GETTING STUFF: " + String.valueOf(props_path));
			String returned = request("GET", props_path, "");
			System.out.println("Got response " + returned + " From " + props_path);
			JSONObject total = new JSONObject(returned);
			JSONArray rows = total.getJSONArray("rows");
			return (JSONObject) rows.get(0);

		} catch (Throwable t) {
			// ignored
		}
		return null;
	}

	private String request(String type, String targetURL, String urlParameters) {
		   
		  try {
		    URL url = new URL(targetURL);
		    connection = (HttpsURLConnection)url.openConnection();
		    connection.setRequestMethod(type);
		    final String userpass = userName + ":" + userPass; 
		    final String auth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
		    connection.setRequestProperty("Authorization", auth);
		    connection.setRequestProperty("Content-Length", 
		        Integer.toString(urlParameters.getBytes().length));
		    for(int ind = 0; ind < header_h.length; ind++) {
		    	connection.setRequestProperty(header_h[ind], header_b[ind]);
		    }
		    connection.setUseCaches(false);
		    connection.setDoOutput(true);
		    if(type == "PUT") {
			    DataOutputStream wr = new DataOutputStream (
			        connection.getOutputStream());
			    wr.writeBytes(urlParameters);
			    wr.close();
		    }
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
		    String line;
		    while((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if(connection != null) {
		      connection.disconnect(); 
		    }
		  }
		}

}