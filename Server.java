import java.net.*;
import java.util.ArrayList;

public class Server {
	
	private int id = 1; 
	private ServerSocket serv;
	private ArrayList<User> users;
	
	public Server(ServerSocket s) {
		this.serv = s;
		users = new ArrayList<User>();
		try{      
			while(true){
				Socket so = serv.accept();
				User user = new User(this, so, id);
				users.add(user);
				increment_id();
				Thread t_user = new Thread(user);
				t_user.start();
			}	

		}
		catch (Exception e) {
			System.out.println("Error - Connection to thread failed");
		}

	}
	
	public void increment_id() {
		id++;
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
