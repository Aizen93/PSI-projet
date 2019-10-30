package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
			if(u.getPseudo() == pseudo) {
				if (u.getMdp() == mdp) {
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
	
	private ArrayList<Annonce> my_annonces() {
		ArrayList<Annonce> tmp = new ArrayList<>();
		for(Annonce annonce : serv.getAnnonces()) {
			if(annonce.getLogin() == user.getPseudo()) tmp.add(annonce);
		}
		return tmp;
	}
	
	private ArrayList<Annonce> annonces_by_type(String type) {
		ArrayList<Annonce> tmp = new ArrayList<>();
		for(Annonce annonce : serv.getAnnonces()) {
			if(annonce.getType() == type) tmp.add(annonce);
		}
		return tmp;
	}
	
	

	
	public void run(){
		do {
			String[]tab;
			read();
			tab = mess.split(";");
			switch(tab[0]) {
				case "CONNECT":
					if(tab.length == 3) {
						if(connect(tab[1], tab[2])) send = "OK";
						else send = "FAIL";
					}else {
						send = "FAIL";
					}
					send();
					break;
				case "ADD" :
					if(user == null)  send = "FAIL";
					else {
						if(tab.length == 4) {
								Annonce a = new Annonce(Server.getRefs(), user.getPseudo(), Integer.parseInt(tab[2]), tab[1], tab[3]);
								serv.addAnnonce(a);
								Server.increment_refs();
								send = "OK";
							
						}
						else send = "FAIL";
					}
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
				case "MYANNS" :
					send = "MYANNS";
					for(Annonce a : serv.getAnnonces()) {
						if(a.getLogin() == user.getPseudo()) send +="###"+a.getType()+"***"+a.getDescription()+"***"+a.getRef()+"***"+a.getPrix()+"***"+a.getLogin();
					}
					break;
				case "DISCONNECT" :
					user.setConnect(false);
					user = null;
					break;
				default : 
					send = "FAIL";
					send();
			}
		}while(!mess.equals("QUIT"));
		pw.println("Au revoir !");
		pw.flush();
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
