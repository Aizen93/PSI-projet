import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

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
				ServerThread server = new ServerThread(so);
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
	
	public class ServerThread implements Runnable{
		
		private Socket so;
		private BufferedReader br;
		private PrintWriter pw;
		private User user;
		private String mess, send;
		
		public ServerThread(Socket s) {
			this.so = s;
			buffered();
			this.user = null;
		}
		
		
		private void buffered() {
	        try {
				br=new BufferedReader(new InputStreamReader(so.getInputStream()));
			} catch (IOException e) {
				System.out.println("Echec création BufferedReader");
			}
	        try {
				pw=new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
			} catch (IOException e) {
				System.out.println("Echec création PrintWriter");
			}
		}
		
		private void read() {
			try {
				mess = br.readLine();
		        System.out.println(mess);

			} catch (IOException e) {
				System.out.println("Echec du readLine");
			}
		}
		
		private void send() {
	        pw.println(send);
	        pw.flush();
		}
		
		
		
		private boolean connect(String pseudo, String mdp) {
			System.out.println("pseudo : "+pseudo+", mdp : "+mdp);
			for(User u : getUsers()) {
				if( pseudo.equals(u.getPseudo()) ) {
					if (mdp.equals(u.getMdp())) {
						user = u;
						user.setConnect(true);
						return true;
					}else {
						return false;
					}
				}
			}
			user = new User(pseudo, mdp);
			addUser(user);;
			return true;
		}
		
		private boolean add_annonce(String []tab) {
			if(user == null)  return false;
			else {
				if(tab.length == 4) {
						addAnnonce(new Annonce(Server.getRefs(), user.getPseudo(), Integer.parseInt(tab[2]), tab[1], tab[3]));
						Server.increment_refs();
						return true;	
				}
				else return false;
			}
		}

		
		
		public void run(){
			do {
				String[]tab;
				read();
				tab = mess.split(";");
				switch(tab[0]) {
					case "CONNECT":
						if(tab.length == 3) {
							System.out.println("run - login : "+tab[1]+", mdp : "+tab[2]);
							if(connect(tab[1], tab[2])) {
								send = "OK";
							}
							else send = "FAIL";
						}
						else	send = "FAIL";
						send();
						break;
					case "ADDANNS" :
						if (add_annonce(tab)) send = "OK";
						else send = "FAIL";
						send();
						break;
					case "ALLANNS" :
						send = "ALLANNS;";
						for(Annonce a : getAnnonces()) {
							send += a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
						}
						System.out.println("ALLANNS : "+send);
						send();
						break;
					case "ANNONCE" :
						send = "ANNONCE;";
						for(Annonce a : getAnnonces()) {
							if(tab[1].equals(a.getType())) send += a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
						}
						send();
						break;
					case "DELANNS" :
						if(user == null) send = "FAIL";
						else {
							for(Annonce a : getAnnonces()) {
								if(Integer.parseInt(tab[1]) == a.getRef() && (a.getLogin()).equals(user.getPseudo())) {
									delete_annonce(a);
									send = "OK";
									send();
									break;
								}
							}
							send = "FAIL";
						}
						send();
						break;
					case "MYYANNS" :
						if(user == null) send = "FAIL";
						else {
							send = "MYYANNS;";
							for(Annonce a : getAnnonces()) {
								if(a.getLogin() == user.getPseudo()) send +=a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
							}
						}
						send();
						break;
					case "DISCONN" :
						if(user != null) {
							user.setConnect(false);
							user = null;
							send="OK";
						}
						else {
							send = "FAIL";
						}
						send();
						break;
					case "QUIT" :
						send = "OK";
						send();
						break;
					default : 
						send = "FAIL";
						send();
				}
			}while(!mess.equals("QUIT"));
			pw.close();
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Echec déconnexion br");
			}
			try {
				so.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Echec déconnexion socket");
			}
		}
	}


}
