/*
 * Erik Kramer (CRMWaypoint)
 * OAuth2 Authentication Username, Password Flow example.
 */

package example;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class OAuth2Authentication {
	

	private static final String HOSTNAME = "<hostname ie: cs12.salesforce.com";
	private static final String USERNAME = "<sf creditionals>";
	private static final String PASSWORD = "<sf creditionals>";
	private static final String CLIENTID = "<sf remote access settings>";
	private static final String SECRET = "<sf remote access settings>";
	
	private static String accessToken;



	/**
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws HttpException, IOException, JSONException {
		// TODO Auto-generated method stub
		
		oAuthSessionProvider();
		exampleQuery();
		
	}
	
	
	
	public static void oAuthSessionProvider()
			throws HttpException, IOException, JSONException {

		// Set up an http client that will make a connection to the REST API
		HttpClient client = new HttpClient();
		client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(30000);

		// Set the SID
		System.out.println("Logging in as " + USERNAME + " in environment "
				+ HOSTNAME);
		String baseUrl = "https://" + HOSTNAME + "/services/oauth2/token";

		// We're going to send a post request to the OAuth URL
		PostMethod oauthPost = new PostMethod(baseUrl);

		// The request body must contain these 5 values
		NameValuePair[] parametersBody = new NameValuePair[5];
		parametersBody[0] = new NameValuePair("grant_type", "password");
		parametersBody[1] = new NameValuePair("username", USERNAME);
		parametersBody[2] = new NameValuePair("password", PASSWORD);
		parametersBody[3] = new NameValuePair("client_id", CLIENTID);
		parametersBody[4] = new NameValuePair("client_secret", SECRET);

		// Execute the request
		System.out.println("POST " + HOSTNAME);
		oauthPost.setRequestBody(parametersBody);
		int code = client.executeMethod(oauthPost);
		System.out.println("HTTP Return code: " + String.valueOf(code));
		
		JSONObject oauthResponse = new JSONObject(new JSONTokener(
		        new InputStreamReader(oauthPost.getResponseBodyAsStream())));

		System.out.println("Auth response: " + oauthResponse.toString(2));

		// set access token to use in each call header for authorization.
		accessToken = oauthResponse.getString("access_token");
		

		
	}
	

	private static void exampleQuery() throws HttpException, IOException{
		
		//do a query
		HttpClient httpclient = new HttpClient();
		GetMethod get = new GetMethod("https://" + HOSTNAME + "/services/data/v20.0/query");
		get.setRequestHeader("Authorization", "OAuth " + accessToken);
	
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];

		params[0] = new NameValuePair("q",
				"SELECT Name, Id from Account LIMIT 100");
		get.setQueryString(params);

		httpclient.executeMethod(get);
		String jsonResponse = get.getResponseBodyAsString();
		System.out.println("HTTP " + String.valueOf(httpclient) + ": " + jsonResponse);
		
		
	}

}
