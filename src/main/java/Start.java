import java.io.IOException;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Start {

	private static final String CLIENT_ID = "000000004015570E";
	private static final String CLIENT_SECRET = "iXqKj7RrCGGh8ChLVzD0Kuml0aQm8Kg0";
	private static final String CALLBACK_URL = "https://onedriveauthorizationcode.bitballoon.com";
	private static final String API_HOST = "https://login.live.com";

	private static CloseableHttpClient httpClient;

	/*
	 * Return a respond of an http Client which executes a httpPost with the
	 * Authorizations Token to get an refresh token
	 */
	static private HttpResponse getAccessTokenResponde(String authorizationtoken)
			throws ClientProtocolException, IOException {

		String bodyentry = "client_id=" + CLIENT_ID + "&redirect_uri=" + CALLBACK_URL + "&client_secret="
				+ CLIENT_SECRET + "&code=" + authorizationtoken + "&grant_type=authorization_code";

		HttpPost httpPost = new HttpPost(API_HOST + "/oauth20_token.srf");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setEntity(new StringEntity(bodyentry));

		return httpClient.execute(httpPost);
	}

	/* Return an url which can be used to get an Authorizations Token */
	static private String getAuthUrl() {
		final String authUrl = API_HOST + "/oauth20_authorize.srf?client_id=" + CLIENT_ID
				+ "&scope=wl.signin+wl.offline_access+onedrive.readwrite" + "&response_type=code&redirect_uri="
				+ CALLBACK_URL;
		return authUrl;
	}

	
	public static void main(String args[]) throws InterruptedException, ParseException, IOException {

		httpClient = HttpClients.createDefault();
		String authorizationtoken;

		System.out.println("Please instert this U RL in your Browser to authenticate yourself\n");
		System.out.println(getAuthUrl());

		System.out.println("\n\nPlease insert the given Access Token");

		Thread.sleep(1000);

		Scanner scanner = new Scanner(System.in);
		authorizationtoken = scanner.nextLine();
		scanner.close();

		System.out.println("\nRequest Token on the Server please wait");

		HttpResponse response = null;
		try {
			response = getAccessTokenResponde(authorizationtoken);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		if (response.getStatusLine().toString().contains("200")) {
			String json = EntityUtils.toString(response.getEntity());
			JSONObject jsonObject = new JSONObject(json);
			System.out.println("\nYour Refresh Token is:");
			System.out.println(jsonObject.getString("refresh_token"));
		} else {
			System.out.println("\nSomething went wrong, sorry");
			System.out.println("DEBUG:\n" + response.getStatusLine());
		}
	}

}
