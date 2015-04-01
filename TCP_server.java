import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import com.sun.net.httpserver.*;

public class TCP_server 
{
	public static Date date; 
	public static String[] uP = new String[2];
	public static int aux = 0;
	public static int aux2 = 0;

	public static void main(String[] args)throws Exception 
	{
			ThreadPool pool = new ThreadPool(5, 10);
		   	HttpServer server = HttpServer.create(new InetSocketAddress(8050), 0);		   
			server.createContext("/", new Home());
			server.createContext("/home_old", new Home_old());
			server.createContext("/secret", new Secret());
			server.createContext("/login", new Login());
			server.setExecutor(null); // creates a default executor
			server.start();
	}
	public static Boolean checkDataSaved() throws IOException 
	{

		if(TCP_server.uP[0] == null && TCP_server.uP[1] == null )
		{
			return false;
		}
	    else if(TCP_server.uP[0].equals("root") && TCP_server.uP[1].equals("laboratorio1"))
	    {
	    	return true;
	    }
		return false;
  	}

	public static void logBonus(String address,String context,String date) throws IOException 
	{	
	    File file = new File("log.txt");
		if (!file.exists()) 
		{
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

    		System.out.println(t.getRemoteAddress().getAddress().toString());
    		String response = "<!DOCTYPE html> <h1 style='margin-left:500px'> 200    (OK) </h1>";
      		t.sendResponseHeaders(200, response.length());
      		if(TCP_server.aux == 0 || (!t.getRemoteAddress().getAddress().toString().equals("/127.0.0.1") && TCP_server.aux2 ==0))
      		{	
    			TCP_server.date = new Date();      			
      			logBonus(t.getRemoteAddress().toString(),"/home    ",date.toString());
      			TCP_server.aux = 1;
      			TCP_server.aux2 = 1;
      		}
      		OutputStream os = t.getResponseBody();
      		os.write(response.getBytes());
      		os.close();
      		t.close();

   	 	}	
  	}

	static class Home_old implements HttpHandler 
	{
    	public void handle(HttpExchange t) throws IOException 
    	{	
    		TCP_server.date = new Date();
    		String response = "<!DOCTYPE html> <head> <meta http-equiv='Refresh' content='2; URL=/'>"
    		+ "</head> <h1 style='margin-left:500px'>301  (Moved	Permanently)</h1> <br> <h2 style='margin-left:500px'>Redirecting to Home</h2>";
      		t.sendResponseHeaders(403, response.length());
      		logBonus(t.getRemoteAddress().toString(),"/home_old",date.toString());
      		OutputStream os = t.getResponseBody();
      		os.write(response.getBytes());
      		os.close();
      		TCP_server.aux = 0;
      		if(t.getRemoteAddress().getAddress().toString() != "/127.0.0.1")
      		{
      			TCP_server.aux2 = 0;      			
      		}

		}
  	}	
	
  	static class Login implements HttpHandler 
	{
    	public void handle(HttpExchange	 t) throws IOException 
    	{	
    		TCP_server.date = new Date();
    		String response = "<!DOCTYPE html><body> <form method = 'POST' action='/secret' 'style='margin-left:500px'>User:<br> <input type='text' name='user' id='user'>"+
			"<br>Pass:<br> <input type='password' name='password' id='pass'> <input onclick='submit()' type='submit' value='Login'></form> </body>";
      		t.sendResponseHeaders(200, response.length());
      		OutputStream os = t.getResponseBody();
      		logBonus(t.getRemoteAddress().toString(),"/login   ",date.toString());
      		os.write(response.getBytes());
      		os.close();
   	 	}	
  	}


  	static class Secret implements HttpHandler 
  	{
    	public void handle(HttpExchange t) throws IOException 
    	{	
    		System.out.println(TCP_server.aux);
    		TCP_server.date = new Date();
    		String response = "<h1>Authentication OK</h1>";
    		BufferedReader br = null;
      		StringBuilder sb = new StringBuilder();
		    String line;
	      	try 
	      	{
		        br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
		        while ((line = br.readLine()) != null) 
		        {
		          	sb.append(line);
		        }
		    } 
		    catch (IOException e) 
		    {
		        e.printStackTrace();
		    } 
		    finally 
		    {
		        if (br != null) 
		        {
		        	try 
		        	{
		            	br.close();
		          	} 
		          	catch (IOException e) 
		          	{
		            	e.printStackTrace();
		          	}
		        }
		    }
	      	String responseData = sb.toString();

	      	if (responseData.length() > 0) 
	        {
      			logBonus(t.getRemoteAddress().toString(),"/secret  ",date.toString());
	        	String[] userPass = responseData.split("&"); 
	        	
	        	if (userPass[0].equals("user=root") && userPass[1].equals("password=laboratorio1")) 
	        	{
		          	TCP_server.uP[0]=userPass[0].split("=")[1];
		          	TCP_server.uP[1]=userPass[1].split("=")[1];
		          	t.sendResponseHeaders(200, response.length());
		         	OutputStream os = t.getResponseBody();
      				os.write(response.getBytes());
		          	os.close();
		          	if(t.getRemoteAddress().getAddress().toString() != "/127.0.0.1")
		      		{
		      			TCP_server.aux2 = 0;      			
		      		}
	        	}
	        	else
	        	{	
		          	TCP_server.uP[0]=userPass[0].split("=")[1];
		          	TCP_server.uP[1]=userPass[1].split("=")[1];
	        		response = "<!DOCTYPE html> <head> <meta http-equiv='Refresh' content='2; URL=/'></head><h1>403 Page Forbbiden</h1><br>Redirecting to Home";
	        		t.sendResponseHeaders(403, response.length());
		         	OutputStream os = t.getResponseBody();
      				os.write(response.getBytes());
		          	os.close();
		          	TCP_server.aux = 0;
		          	if(t.getRemoteAddress().getAddress().toString() != "/127.0.0.1")
		      		{
		      			TCP_server.aux2 = 0;      			
		      		}
	        	}
	        } 
	        else if(checkDataSaved() == true)
	        {
        		t.sendResponseHeaders(200, response.length());
      			logBonus(t.getRemoteAddress().toString(),"/secret  ",date.toString());
	         	OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
	          	os.close();
	          	if(t.getRemoteAddress().getAddress().toString() != "/127.0.0.1")
	      		{
	      			TCP_server.aux2 = 0;      			
	      		}
	        }
	        else
	        {
	        	response = "<!DOCTYPE html> <head> <meta http-equiv='Refresh' content='2; URL=/'></head><h1>403 Page Forbbiden</h1><br>Redirecting to Home";
        		t.sendResponseHeaders(403, response.length());
      			logBonus(t.getRemoteAddress().toString(),"/secret  ",date.toString());
	         	OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
	          	os.close();
		        TCP_server.aux = 0;
		        if(t.getRemoteAddress().getAddress().toString() != "/127.0.0.1")
	      		{
	      			TCP_server.aux2 = 0;      			
	      		}

	        }

	    }
	}

}


 		

	


