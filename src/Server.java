import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

public class Server {
	
	private ServerSocket serv;
	private static int nb_connecte = 0;
	private static int refs = 0;
	private static int port_udp = 2000;
	private ArrayList<User> users;
	private ArrayList<Annonce> annonces;

	
	public Server(ServerSocket s) throws UnknownHostException {
		System.out.println(Color.GREEN_BOLD_BRIGHT + "--------------Server is now running -----------------------" + Color.ANSI_RESET);
                InetAddress ip = InetAddress.getLocalHost();
                System.out.println(Color.GREEN_BOLD_BRIGHT +"----------------- IP : " + s.getInetAddress().getLocalHost().getHostAddress()+" -----------------------" + Color.ANSI_RESET);
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
			System.out.println(Color.RED_BRIGHT + "Error - Connection to thread failed" + Color.ANSI_RESET);
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
			new Server(s);
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
			System.out.println(Color.YELLOW_BRIGHT + "--------------New guest -----------------------" + Color.ANSI_RESET);
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
		        System.out.println("-------------------------------------------");
		        System.out.println("----> Message receive : "+mess);
		        System.out.println("-------------------------------------------");
			} catch (IOException e) {
				System.out.println("Echec du readLine");
			}
		}
		
		private void send() {
	        pw.println(send);
	        System.out.println("-------------------------------------------");
	        System.out.println("----> Message send : "+send);
	        System.out.println("-------------------------------------------");
	        pw.flush();
		}
		
		private synchronized int add_user(String pseudo, String mdp, int port, String ip) {
	        System.out.println("-------------------------------------------");
			System.out.println("            ----> New User created");
			System.out.println("                       pseudo : "+pseudo+", mdp : "+mdp);
	        System.out.println("-------------------------------------------");
			user = new User(pseudo, mdp, port, ip);
			addUser(user);;
			return user.getPortUDP();
		}
		
		private synchronized int connection(String pseudo, String mdp, int port_udp, String ip_address) {
			for(User u : getUsers()) {
				if( pseudo.equals(u.getPseudo()) ) {
					if (mdp.equals(u.getMdp())) {
						user = u;
						user.setConnect(true);
				        System.out.println("-------------------------------------------");
						System.out.println("            ----> User connected");
						System.out.println("                       pseudo : "+pseudo+", mdp : "+mdp);
				        System.out.println("-------------------------------------------");
                                                u.setIp(ip_address);
                                                u.setPort_udp(port_udp);
						return u.getPortUDP();
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
						addAnnonce(new Annonce(Server.getRefs(), user.getPseudo(), Integer.parseInt(tab[2]), tab[1], tab[3]));
						Server.increment_refs();
						return true;	
				}
				else return false;
			}
		}

		private void connect(String[] tab) {
			if(tab.length == 5) {
				int port = connection(tab[1], tab[2],Integer.parseInt(tab[3]),tab[4]);
				if(port>0) {
					send = "CONNECT;" + port;
				}
				else send = "FAIL;Wrong password"; 
			}
			else	send = "FAIL;Too many parameters"; 
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
						for(Annonce a : getAnnonces()) {
							if(tab[1].equals(a.getType())) send += a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
						}
						send();
						break;
					case "DELANNS" :
						if(user == null) send = "FAIL;You are not connected";
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
						send = null;
						if(user == null) send = "FAIL;Error - you are not connected";
						else {
							int ref =  Integer.parseInt(tab[1]);
							for(Annonce a : annonces) {
								if(a.getRef() == ref) {
									for(User u : users) {
										if(u.getPseudo().equals(a.getLogin())) {
											send = "MESSAGE;"+u.getPortUDP();
										}
									}
								}
							}
							if(send == null) send = "FAIL;Error - this annonce does not exist";
						}
						send();
						break;
					default : 
						send = "FAIL;This command does not exist";
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
