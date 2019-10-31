import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread implements Runnable{
	
	private Socket so;
	private Server serv;
	private BufferedReader br;
	private PrintWriter pw;
	private User user;
	private String mess, send;
	private Scanner sc;
	
	public ServerThread(Server serv, Socket s) {
		this.so = s;
		this.serv= serv;
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
		for(User u : serv.getUsers()) {
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
		serv.addUser(user);;
		return true;
	}
	
	private boolean add_annonce(String []tab) {
		if(user == null)  return false;
		else {
			if(tab.length == 4) {
					serv.addAnnonce(new Annonce(Server.getRefs(), user.getPseudo(), Integer.parseInt(tab[2]), tab[1], tab[3]));
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
				case "ADD" :
					if (add_annonce(tab)) send = "OK";
					else send = "FAIL";
					send();
					break;
				case "ANNS" :
					send = "ANNS;";
					for(Annonce a : serv.getAnnonces()) {
						send += a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
					}
					System.out.println("ANNS : "+send);
					send();
					break;
				case "ANN" :
					send = "ANN;";
					for(Annonce a : serv.getAnnonces()) {
						if(tab[1].equals(a.getType())) send += a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
					}
					send();
					break;
				case "DELETE" :
					if(user == null) send = "FAIL";
					else {
						for(Annonce a : serv.getAnnonces()) {
							if(Integer.parseInt(tab[1]) == a.getRef() && (a.getLogin()).equals(user.getPseudo())) {
								serv.delete_annonce(a);
								send = "OK";
								send();
								break;
							}
						}
						send = "FAIL";
					}
					send();
					break;
				case "MYANNS" :
					if(user == null) send = "FAIL";
					else {
						send = "MYANNS;";
						for(Annonce a : serv.getAnnonces()) {
							if(a.getLogin() == user.getPseudo()) send +=a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin()+"###";
						}
					}
					send();
					break;
				case "DISCONNECT" :
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
