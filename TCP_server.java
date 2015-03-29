import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import com.sun.net.httpserver.*;

public class TCP_server 
{
	public static Date date = new Date();
	public static void main(String[] args)throws Exception 
	{
			ThreadPool pool = new ThreadPool(5, 10);
		   	HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);
			server.createContext("/home_old", new Home_old());
			server.createContext("/secret", new Secret());
			server.createContext("/login", new Login());
			server.createContext("/", new Home());
			server.setExecutor(pool); // creates a default executor
			dataCleaner();
			server.start();
	}
	public static Boolean checkDataSaved() throws IOException 
	{

	    FileInputStream fstream = new FileInputStream("data.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   
		{
		  // Print the content on the console
		 	if(strLine.split("&")[0].equals("root") && strLine.split("&")[1].equals("laboratorio1") )
		  	{
		  		br.close();
		  		return true;
		  	} 
		}
		br.close();
		return false;

		//Close the input stream	
  	}

  	public static void data(String user,String pass) throws IOException 
	{
	    File file = new File("data.txt");
	    
	    if(!file.exists()) {
	      file.createNewFile();
	    }
	    
	    FileWriter fw= new FileWriter(file.getName(),false);
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write(user+"&"+pass+"\n");
	    bw.close();
  	}

  	public static void dataCleaner() throws IOException 
	{
	    File file = new File("data.txt");
	    
	    if(!file.exists()) {
	      file.createNewFile();
	    }
	    
	    FileWriter fw= new FileWriter(file.getName(),false);
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write("");
	    bw.close();
  	}


	public static void logBonus(String address,String context,String date) throws IOException 
	{
	    File file = new File("log.txt");
			// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(" IP : " + address);
		bw.write(" | URL 	: "+context);
		bw.write(" | "+date+"\n" );
		bw.close();
  	}

	static class Home implements HttpHandler 
	{
    	public void handle(HttpExchange t) throws IOException 
    	{
    		String response = "<!DOCTYPE html> <h1 style='margin-left:500px'> 200    (OK) </h1>";
      		t.sendResponseHeaders(200, response.length());
      		OutputStream os = t.getResponseBody();
      		System.out.println("IP : " + t.getRemoteAddress());
      		logBonus(t.getRemoteAddress().toString().toString(),"/home",date.toString());
      		os.write(response.getBytes());
      		os.close();
   	 	}	
  	}

	static class Home_old implements HttpHandler 
	{
    	public void handle(HttpExchange t) throws IOException 
    	{	

    		String response = "<!DOCTYPE html> <head> <meta http-equiv='Refresh' content='2; URL=localhost:8090/'>"
    		+ "</head> <h1 style='margin-left:500px'>301  (Moved	Permanently)</h1> <br> <h2 style='margin-left:500px'>Redirecting to Home</h2>";
      		t.sendResponseHeaders(301, response.length());
      		OutputStream os = t.getResponseBody();
      		System.out.println("IP : " + t.getRemoteAddress());
			System.out.println("URI : " + t.getRequestURI());
      		logBonus(t.getRemoteAddress().toString(),"/home_old",date.toString());
      		os.write(response.getBytes());
      		os.close();

		}
  	}	

  	static class Login implements HttpHandler 
	{
    	public void handle(HttpExchange	 t) throws IOException 
    	{
    		String response = "<!DOCTYPE html><body> <form method = 'POST' action='/secret' 'style='margin-left:500px'>User:<br> <input type='text' name='user' id='user'>"+
			"<br>Pass:<br> <input type='password' name='password' id='pass'> <input onclick='submit()' type='submit' value='Login'></form> </body>";
      		t.sendResponseHeaders(200, response.length());
      		OutputStream os = t.getResponseBody();
      		System.out.println("IP : " + t.getRemoteAddress());
			System.out.println("URI : " + t.getRequestURI());
      		logBonus(t.getRemoteAddress().toString(),"/login",date.toString());
      		os.write(response.getBytes());
      		os.close();
   	 	}	
  	}


  	static class Secret implements HttpHandler 
  	{
    	public void handle(HttpExchange t) throws IOException 
    	{	
    		String response = "<h1>Authentication OK</h1>";
    		BufferedReader br = null;
      		StringBuilder sb = new StringBuilder();
   
		      String line;
		      try {
		   
		        br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
		        while ((line = br.readLine()) != null) {
		          sb.append(line);
		        }
		   
		      } catch (IOException e) {
		        e.printStackTrace();
		      } finally {
		        if (br != null) {
		          try {
		            br.close();
		          } catch (IOException e) {
		            e.printStackTrace();
		          }
		        }
		      }
   
	      	String body = sb.toString();

	      	if (body.length() > 0) 
	        {
      			logBonus(t.getRemoteAddress().toString(),"/secret",date.toString());
	        	String[] userPass = body.split("&"); 
	        	System.out.println(userPass[0] + " ||| " + userPass[1]);

	        	if (userPass[0].equals("user=root") && userPass[1].equals("password=laboratorio1")) {
		          	data(userPass[0].split("=")[1],userPass[1].split("=")[1]);
		          	t.sendResponseHeaders(200, response.length());
		         	OutputStream os = t.getResponseBody();
      				os.write(response.getBytes());
		          	os.close();
	        	}
	        	else
	        	{	
		          	data(userPass[0].split("=")[1],userPass[1].split("=")[1]);
	        		response = "<!DOCTYPE html> <head> <meta http-equiv='Refresh' content='2; URL=localhost:8090'></head><h1>403 Page Forbbiden</h1><br>Redirecting to Home";
	        		t.sendResponseHeaders(403, response.length());
		         	OutputStream os = t.getResponseBody();
      				os.write(response.getBytes());
		          	os.close();
	        	}
	        } 
	        else if(checkDataSaved() == true)
	        {
	        	response = "<h1>Authentication OK</h1>";
        		t.sendResponseHeaders(200, response.length());
      			logBonus(t.getRemoteAddress().toString(),"/secret",date.toString());
	         	OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
	          	os.close();
	        }
	        else
	        {
	        	response = "<!DOCTYPE html> <head> <meta http-equiv='Refresh' content='2; URL=localhost:8090'></head><h1>403 Page Forbbiden</h1><br>Redirecting to Home";
        		t.sendResponseHeaders(403, response.length());
      			logBonus(t.getRemoteAddress().toString(),"/secret",date.toString());
	         	OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
	          	os.close();
	        }

	    }
	}

}


 		

	


