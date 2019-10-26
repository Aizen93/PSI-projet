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
		try{      
			while(true){
				Socket so = serv.accept();
				User user = new User(this, so, id_user);
				users.add(user);
				increment_id_user();
				Thread t_user = new Thread(user);
				t_user.start();
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
			ServerSocket s = new ServerSocket (4242);
			Server serv = new Server(s);
		}
		catch (Exception e) {
			System.out.println("Error - Connection to server failed");
		}

	}

}
