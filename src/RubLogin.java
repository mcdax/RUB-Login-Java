import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

public class RubLogin {
	public RubLogin(){}
	
	public String getIP() throws IOException {
		String ip = "";
		String formURL = "https://login.rz.ruhr-uni-bochum.de/cgi-bin/start";
		URL myurl = new URL(formURL);
		HttpURLConnection fCon = (HttpURLConnection) myurl.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(fCon.getInputStream()));
		String line = "";
		while ((line = in.readLine()) != null) {
			if (line.contains("<td valign=\"top\"><input type=\"ipaddr\" name=\"ipaddr\" value=")) {
				ip = line
						.replace(
								"<td valign=\"top\"><input type=\"ipaddr\" name=\"ipaddr\" value=\"",
								"").replace("\"></td>", "").replaceAll(" ", "");
				break;
			}
		}
		in.close();
		return ip;
	}

	public String login(String userID, String password, String ip) throws IOException {

		String httpsURL = "https://login.rz.ruhr-uni-bochum.de/cgi-bin/laklogin";

		String query = "loginid=" + URLEncoder.encode(userID, "UTF-8");
		query += "&";
		query += "password=" + URLEncoder.encode(password, "UTF-8");

		query += "&";
		query += "ipaddr=" + URLEncoder.encode(ip, "UTF-8");

		query += "&";
		query += "action=" + URLEncoder.encode("Login", "UTF-8");

		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
		con.setRequestMethod("POST");

		con.setRequestProperty("Content-length", String.valueOf(query.length()));
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
		con.setDoOutput(true);
		con.setDoInput(true);

		DataOutputStream output = new DataOutputStream(con.getOutputStream());
		output.writeBytes(query);
		output.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String line = "";
		while ((line = in.readLine()) != null) {
			if(line.contains("<font face=\"Helvetica, Arial, sans-serif\"><big><big>")){
				line = line.replace("<font face=\"Helvetica, Arial, sans-serif\"><big><big>", "").replace("<br>", ""); 
				line = line.concat(" - " + in.readLine()).replace("<small><small>", "").replace("<br>", "");
				in.close();
				return line;
			}
		}
		in.close();
		
		
		System.out.println("Resp Code:" + con.getResponseCode());
		System.out.println("Resp Message:" + con.getResponseMessage());
		
		return "FAILED, please call support."; // really? ._.
	}
	
	public String logout(String userID, String password, String ip) throws IOException {

		String httpsURL = "https://login.rz.ruhr-uni-bochum.de/cgi-bin/laklogin";

		String query = "loginid=" + URLEncoder.encode(userID, "UTF-8");
		query += "&";
		query += "password=" + URLEncoder.encode(password, "UTF-8");

		query += "&";
		query += "ipaddr=" + URLEncoder.encode(ip, "UTF-8");

		query += "&";
		query += "action=" + URLEncoder.encode("Logout", "UTF-8");

		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
		con.setRequestMethod("POST");

		con.setRequestProperty("Content-length", String.valueOf(query.length()));
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
		con.setDoOutput(true);
		con.setDoInput(true);

		DataOutputStream output = new DataOutputStream(con.getOutputStream());
		output.writeBytes(query);
		output.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String line = "";
		while ((line = in.readLine()) != null) {
			if(line.contains("<font face=\"Helvetica, Arial, sans-serif\"><big><big>")){
				line = line.replace("<font face=\"Helvetica, Arial, sans-serif\"><big><big>", "").replace("<br>", ""); 
				line = line.concat(in.readLine()).replace("<small><small>", "").replace("<br>", "");
				in.close();
				return line;
			}
		}
		in.close();
		
		
		System.out.println("Resp Code:" + con.getResponseCode());
		System.out.println("Resp Message:" + con.getResponseMessage());
		
		return "FAILED, please call support.";
	}
}