import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

public class Server {
	
	private static int id_user = 1, id_annonce = 1; 
	private ServerSocket serv;
	private ArrayList<User> users;
	private ArrayList<Annonce> annonces;
	
	public Server(ServerSocket s) {
            this.serv = s;
            users = new ArrayList<User>();
            BufferedReader br;
            PrintWriter pw;
            try{      
                while(true){
                    Socket so = serv.accept();
                    //User user = new User(this, so, id_user);
                    //users.add(user);
                    increment_id_user();
                    //Thread t_user = new Thread(user);
                    //t_user.start();
                    br = new BufferedReader(new InputStreamReader(so.getInputStream()));
                    pw = new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
                    String mess_recu = "";
                    mess_recu = br.readLine();
                    System.out.println("Message reçu: " + mess_recu);
                    System.out.println("------------------------------");
                    pw.println("OK");
                    System.out.println("--------------OK-------------");
                    pw.flush();
                    System.out.println("--------------OKSENT-------------");
                }	

            }
            catch (Exception e) {
                System.out.println("Error - Connection to thread failed");
            }

	}
	
	public void increment_id_user() {
		id_user++;
	}
	public void increment_id_annonce() {
		id_annonce++;
	}
	
	
	public static void main(String[] args) {
		try{
			ServerSocket s = new ServerSocket (1027);
			Server serv = new Server(s);
		}
		catch (Exception e) {
			System.out.println("Error - Connection to server failed");
		}

	}

}
