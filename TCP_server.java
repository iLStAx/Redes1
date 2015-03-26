import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.io.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import java.net.InetSocketAddress;
import java.io.OutputStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Map;
import java.io.*;

public class TCP_server 
{
	public static String user = "";
	public static String password = "";
	public static Date date = new Date();
	public static void main(String[] args)throws Exception 
	{
		// try
		// {	
		   	HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);
			server.createContext("/home_old", new Home_old());
			server.createContext("/secret", new Secret());
			server.createContext("/login", new Login());
			server.createContext("/", new Home());
			server.setExecutor(null); // creates a default executor
			server.start();
			// se inicia el servidor con puerto 8080
			// ServerSocket servidor = new ServerSocket(8080);
			// System.out.println("Server Up");
			// while(true)
			// {	

			// 	Socket cliente = servidor.accept();
			// 	//DataInputStream data = new DataInputStream(cliente.getInputStream());
			// 	BufferedReader data = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

			// 	//mensaje de retorno de la pagina web
			// 	PrintWriter dataWeb = new PrintWriter(cliente.getOutputStream());
			// 	System.out.println("IP : " + cliente.getInetAddress());
			// 	System.out.println("Port : " + cliente.getPort());
			// 	System.out.println("Message : " + data.readLine());

			// 	dataWeb.println("Welcome to redes");
			// 	dataWeb.println("HTTP/1.0 200 OK");
			// 	dataWeb.close();

		// 	}
		// }
		// catch(IOException e)
		// {
		// 	e.printStackTrace();
		// }
	}

	static class Home implements HttpHandler 
	{
    	public void handle(HttpExchange t) throws IOException 
    	{
    		String response = "<!DOCTYPE html> <h1 style='margin-left:500px'> 200    (OK) </h1>";
      		t.sendResponseHeaders(200, response.length());
      		OutputStream os = t.getResponseBody();
      		System.out.println("IP : " + t.getRemoteAddress());
			System.out.println("URI : " + t.getRequestURI());
			File file = new File("log.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("IP : " + t.getRemoteAddress());
			bw.write(" | URI : /home" );
			bw.write(" | "+date.toString()+"\n" );
			bw.close();

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

			File file = new File("log.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("IP : " + t.getRemoteAddress());
			bw.write(" | URI : /login");
			bw.write(" | "+date.toString()+"\n" );
			bw.close();

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
			File file = new File("log.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("IP : " + t.getRemoteAddress());
			bw.write(" | URI : /home_old");
			bw.write(" | "+date.toString()+"\n" );
			bw.close();

      		os.write(response.getBytes());
      		os.close();

		}
  	}	

  	static class Secret implements HttpHandler 
  	{
    	public void handle(HttpExchange t) throws IOException 
    	{	
    		URL    url            = new URL("localhost:8090/login");
			HttpURLConnection cox= (HttpURLConnection) url.openConnection();           
			cox.setDoOutput( true );
			cox.setDoInput ( true );
			cox.setInstanceFollowRedirects( false );
			cox.setRequestMethod( "POST" );
			cox.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			cox.setRequestProperty( "charset", "utf-8");
			cox.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			cox.setUseCaches( false );
			try( DataOutputStream wr = new DataOutputStream( cox.getOutputStream())) {
			   wr.write( postData );
			}
    		String response = "secret";
    		// String userlogin = request.getParameter("user"); // HTTPSERVLET
    		// String passwordlogin = request.getParameter("password"); //HTTPSERVLET
    		// if (TCP_server.user != "root" && TCP_server.password != "laboratorio1")
    		// 	{
      // 				response = "<!DOCTYPE html> <h1 style='margin-left:500px'>403  (Forbidden)</h1> <br> <h2 style='margin-left:500px'>Can't Display this page, need authentication</h2>";
    		// 	}
    		// else if((TCP_server.user == "root" && TCP_server.password == "laboratorio1") || (userlogin == "root" && passwordlogin == "laboratorio1"))
    		// {
      // 				response = "<!DOCTYPE html> <h1 style='margin-left:500px'>200   (OK)</h1>";
    		// }
    		
      		t.sendResponseHeaders(403, response.length());
		    OutputStream os = t.getResponseBody();
		    System.out.println("IP : " + t.getRemoteAddress());
			System.out.println("URI : " + user);
			File file = new File("log.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("IP : " + t.getRemoteAddress());
			bw.write(" | URI : /secret");
			bw.write(" | "+date.toString()+"\n" );
			bw.close();

		    os.write(response.getBytes());
		    os.close();
   	 	}
  	}		

}

