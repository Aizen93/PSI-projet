import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

public class Server {

	private static int refs = 0;
	private ArrayList<User> users;
	private ArrayList<Annonce> annonces;

	
	public Server(ServerSocket s) throws UnknownHostException {
		Affichage.display_load_server(s.getInetAddress().getLocalHost().getHostAddress());
		ServerSocket serv = s;
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
			Affichage.display_error("Error - Connection to thread failed");
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

	public static int getRefs() {
		return refs;
	}

	public static void increment_refs() {
		Server.refs++;
	}

	public void delete_annonce(Annonce a) {
		annonces.remove(a);
	}
	
	public static void main(String[] args) {
		try{
			ServerSocket s = new ServerSocket (1027);
			new Server(s);
		}
		catch (Exception e) {
			Affichage.display_error("Error - Connection to server failed");
		}

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
			Affichage.display_client_connect(-1,null, null);
		}
		
		
		private void buffered() {
	        try {
				br=new BufferedReader(new InputStreamReader(so.getInputStream()));
			} catch (IOException e) {
	        	Affichage.display_error("Echec création BufferedReader");
			}
	        try {
				pw=new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
			} catch (IOException e) {
	        	Affichage.display_error("Echec création PrintWriter");
			}
		}
		
		private void read() {
			try {
				mess = br.readLine();
				Affichage.display_read_server(mess);
			} catch (IOException e) {
				Affichage.display_error("Echec du readLine");
			}
		}
		
		private void send() {
	        pw.println(send);
			Affichage.display_send_server(send);
	        pw.flush();
		}
		
		private synchronized int add_user(String pseudo, String mdp, int port, String ip) {
	        Affichage.display_client_connect(0, pseudo, mdp);
			user = new User(pseudo, mdp, port, ip);
			addUser(user);
			return user.getPortUDP();
		}
		
		private synchronized int connection(String pseudo, String mdp, int port_udp, String ip_address) {
			for(User u : getUsers()) {
				if( pseudo.equals(u.getPseudo()) ) {
					if (mdp.equals(u.getMdp())) {
						if(!u.isConnect()) {
							user = u;
							user.setConnect(true);
							Affichage.display_client_connect(1,pseudo, mdp);
							u.setIp(ip_address);
							u.setPort_udp(port_udp);
							return u.getPortUDP();
						}
						return -1;
					}else {
						return 0;
					}
				}
			}
			add_user(pseudo, mdp,port_udp, ip_address);
                        return port_udp;
		}
		
		private synchronized boolean add_annonce(String []tab) {
			if(user == null)  return false;
			else {
				if(tab.length == 4) {
					if(!isNumeric(tab[2])) return false;
					addAnnonce(new Annonce(Server.getRefs(), user.getPseudo(), Integer.parseInt(tab[2]), tab[1].toLowerCase(), tab[3]));
					Server.increment_refs();
					return true;
				}
				else return false;
			}
		}

		private void connect(String[] tab) {
			if(tab.length == 5) {
				int port = -2;
				if(isNumeric(tab[3])) port = connection(tab[1], tab[2],Integer.parseInt(tab[3]),tab[4]);
				if(port>0) {
					send = "CONNECT";
				}else if(port == -1) send = "FAIL;Already Connected";
				else if(port == -2) send = "FAIL;Port not numeric";
				else send = "FAIL;Wrong password"; 
			}
			else if(tab.length > 5)	send = "FAIL;Too many parameters";
			else if(tab.length < 5)	send = "FAIL;Not enough parameters";
			send();
		}
		
		private void disconnect() {
			if(user != null) {
				user.setConnect(false);
				user = null;
				send="OK";
			}
			else 	send = "FAIL;You are already not loged in";

			send();
		}
		
		private void my_annonces() {
			send = "MYYANNS;";
			for(Annonce a : getAnnonces()) {
				if(a.getLogin() == user.getPseudo()) send +=a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
			}
			send();
		}
		
		public void run(){
			do {
				String[]tab;
				read();
				tab = mess.split(";");
				switch(tab[0]) {
					case "CONNECT":
						connect(tab);
						break;
					case "ADDANNS" :
						if (add_annonce(tab)) send = "OK";
						else send = "FAIL;Can not add this announce";
						send();
						break;
					case "ALLANNS" :
						send = "ALLANNS;";
						for(Annonce a : getAnnonces()) {
							send += a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
						}
						send();
						break;
					case "ANNONCE" :
						send = "ANNONCE;";
						String domain = tab[1].toLowerCase();
						for(Annonce a : getAnnonces()) {
							if(domain.equals(a.getType())) send += a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
						}
						send();
						break;
					case "DELANNS" :
						delanns(tab);
						break;
					case "MYYANNS" :
						if(user == null) send = "FAIL;You are not connected";
						else my_annonces();
						break;
					case "DISCONN" :
						disconnect();
						break;
					case "QUIT" :
						send = "OK";
						send();
						break;
					case "MESSAGE" :
						message(tab);
						break;
					default : 
						send = "FAIL;This command does not exist";
						send();
				}
			}while(!mess.equals("QUIT"));
			close();
		}

		private void delanns(String[] tab) {
			if(user == null) send = "FAIL;You are not connected";
			else {
				for(Annonce a : getAnnonces()) {
					if(!isNumeric(tab[1])) send = "FAIL;Wrong entered ref";
					else if(Integer.parseInt(tab[1]) == a.getRef() && (a.getLogin()).equals(user.getPseudo())) {
						delete_annonce(a);
						send = "OK";
						break;
					}
				}
				if(!send.contains("OK"))send = "FAIL;this annonce doesn't exist";
			}
			send();
		}

		private void message(String[] tab) {
			send = null;
			if(user == null) send = "FAIL;Error - you are not connected";
			else {
				int ref = -1;
				if(isNumeric(tab[1])) ref =  Integer.parseInt(tab[1]);
				for(Annonce a : annonces) {
					if(a.getRef() == ref) {
						for(User u : users) {
							if(u.getPseudo().equals(a.getLogin())) {
								send = "MESSAGE;"+u.getPortUDP()+";"+u.getIp();
							}
						}
					}
				}
				if(send == null) send = "FAIL;Error - this annonce does not exist";
			}
			send();
		}

		private boolean isNumeric(String str) {
			return str.matches("-?\\d+(\\.\\d+)?");
		}

		private void close(){
			pw.close();
			try { br.close(); }
			catch (IOException e) { Affichage.display_error("Echec déconnexion br"); }
			try { so.close(); }
			catch (IOException e) { Affichage.display_error("Echec déconnexion socket"); }
		}
	}

}
