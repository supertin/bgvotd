package com.gmail.supertin.unofficialbiblegatewayvotd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.Bukkit;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class votdUpdate {

	public static void main() {
		

		//Broadcast a message that the VOTD is updating.
		Bukkit.broadcastMessage("Refreshing Bible Gateway's Verse Of The Day");
		
		//Set the URL to use to get the VOTD. Base URL plus the version ID.
		String sURL = "https://www.biblegateway.com/votd/get/?format=json&version=" + main.bibleversion;

		//OK, now this is where we get the data, mangle it, and set the variable if it all goes well.
		try {
			// Connect to the URL using java's native library (based on some copy pasta)
			URL url = new URL(sURL);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();

			// Convert to a JSON object to print data
			JsonParser jp = new JsonParser(); //from gson
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
			JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
			JsonObject votd = rootobj.get("votd").getAsJsonObject(); //holder for votd nested json :S
			main.verse = ReplaceSpecialChars(votd.get("text").getAsString()); //Set the main verse variable.
			main.reference = votd.get("reference").getAsString(); //Set the verse reference (as in the book, chapter and verse number)

		} catch (IOException e) {
			//Something bad happened.
			//Log an error... We can't be sure what happened, but it happened grabbing the JSON data
			Bukkit.getLogger().warning("Error getting or processing data from " + sURL);
		}
	}

	// This is a simple hack to remove/fix any special chars from the text. BG assumes the VOTD is going to a web browser.
	static String ReplaceSpecialChars(String html){
		String htmltemp = html; //Just making sure the string replace works right. It seemed broken directly changing the "html" var.
		
		// Replace fancy quotes with real ones.
		htmltemp = htmltemp.replaceAll("&ldquo;","\"");
		htmltemp = htmltemp.replaceAll("&rdquo;","\"");
		htmltemp = htmltemp.replaceAll("&#8221;","\""); //Right fancy quote numeric - Why, BibleGateway?!?! Why?
		htmltemp = htmltemp.replaceAll("&#8220;","\""); //And the left one.
		htmltemp = htmltemp.replaceAll("&#8217;","'");  //Numeric single quote mark
		
		//Non breaking spaces should be actual spaces.
		htmltemp = htmltemp.replaceAll("&nbsp;"," ");
		//Ampersands should be ampersands.
		htmltemp = htmltemp.replaceAll("&amp;","&");
		return htmltemp;
	}
}