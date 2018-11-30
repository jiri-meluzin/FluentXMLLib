package com.meluzin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Base64;

public class ProxyTester {
	public static void main(String[] args) throws MalformedURLException, IOException {
		System.out.println(args[0]+":"+ Integer.parseInt(args[1]));
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(args[0], Integer.parseInt(args[1])));

        System.setProperty("http.proxyHost", args[0]);
        System.setProperty("http.proxyPort", args[1]);
        System.setProperty("https.proxyHost", args[0]);
        System.setProperty("https.proxyPort", args[1]);
        
		HttpURLConnection con = (HttpURLConnection) (new URL(args[2]).openConnection(/*proxy*/));
		Authenticator authenticator = new Authenticator() {

			public PasswordAuthentication getPasswordAuthentication() {
				return (new PasswordAuthentication(args[3], args[4].toCharArray()));
			}
		};
		Authenticator.setDefault(authenticator);
		con.setRequestMethod("GET");
		    String string = new String(args[3]+":"+args[4]);
		    //System.out.println(string);
			String auth = new String(Base64.getEncoder().encode(string.getBytes()));
		    auth = "Basic " + auth;
		    //con.setRequestProperty("Proxy-Connection","Keep-Alive");
		    //System.out.println(auth);
		    //con.setRequestProperty("Proxy-Authorization",auth);
		    //System.out.println(con.getRequestProperty("Proxy-Authorization"));

		int responseCode = con.getResponseCode();
		System.out.println("done " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}
}
