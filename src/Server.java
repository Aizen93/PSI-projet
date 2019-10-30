package src;

import java.net.*;
import java.util.ArrayList;

public class Server {
	
	private ServerSocket serv;
	private static int nb_connecte = 0;
	private static int refs = 0;
	private ArrayList<User> users;
	private ArrayList<Annonce> annonces;

	
	public Server(ServerSocket s) {
		this.serv = s;
		users = new ArrayList<User>();
		annonces = new ArrayList<Annonce>();
		try{      
			while(true){
				Socket so = serv.accept();
				ServerThread server = new ServerThread(this, so);
				Thread t_server = new Thread(server);
				t_server.start();
			}	
		}
		catch (Exception e) {
			System.out.println("Error - Connection to thread failed");
		}

	}
	
	public ArrayList<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		this.users.add(user);
	}

	public ArrayList<Annonce> getAnnonces() {
		return annonces;
	}

	public void addAnnonce(Annonce annonce) {
		this.annonces.add(annonce);
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

	public static int getNb_connecte() {
		return nb_connecte;
	}

	public static void increment_connecte() {
		Server.nb_connecte ++;
	}
	
	public static void decrement_connecte() {
		Server.nb_connecte --;
	}	

	public static int getRefs() {
		return refs;
	}

	public static void increment_refs() {
		Server.refs++;
	}

	public void delete_annonce(Annonce a) {
		annonces.remove(a);
	}

}
