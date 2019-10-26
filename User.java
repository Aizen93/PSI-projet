import java.net.*;
import java.io.*;

public class User implements Runnable{

	private static int nb_connecte = 0;
	private BufferedReader br;
	private PrintWriter pw;
	private Server server;
	private Socket so;
	private int id_client;
	private String pseudo;
  
	public User(Server serv, Socket s, int id) {
		try{
			this.so = s;
			this.server = serv;
			this.id_client = id;
			nb_connecte++;
			this.br=new BufferedReader(new InputStreamReader(so.getInputStream()));
			this.pw=new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
		}catch (Exception e) {
			System.out.println("Erreur création de l'utilisateur");
		}
	}

	public void menu_display() {
		StringBuilder sb = new StringBuilder();
		sb.append("+--Menu-------------------------------------------+&");
		sb.append("| MENU : display the options                      |&");
		sb.append("| CONNECT : display the user's informations       |&");
		sb.append("| DISCONNECT : send a message to a user           |&");
		sb.append("| MYANNS : display the options                    |&");
		sb.append("| ANNS : display the options                      |&");
		sb.append("+-------------------------------------------------+");
		pw.println(sb.toString());
		pw.flush();
	}
	
	public void run(){
		try{
			pw.println("Bonjour. Vous êtes bien connecté au serveur. Votre identifiant est le "+id_client+". (Pour quitter envoyer quit)");
			pw.flush();
			pw.println("Vous êtes actuellement "+nb_connecte+" utilisateur(s) connecté(s). (Pour quitter envoyer quit)");
			pw.flush();
			System.out.println("Utlisateur : " +id_client);
			String mess;
			while(!(mess = br.readLine()).equals("DISCONNECT")){
				if(mess.equals("MENU")) {
					menu_display();
				}
				//pw.println("Pour quitter envoyer quit");
				//pw.flush();
			}
			nb_connecte--;
			pw.println("Au revoir !");
			pw.flush();
			pw.close();
			br.close();
			so.close();
		}catch (Exception e) {
			System.out.println("Error - Receive / Send failed");
		}

	}

	public Server getServer() {
		return server;
	}

	public String getPseudo() {
		return pseudo;
	}
}