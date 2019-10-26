import java.net.*;
import java.io.*;
import java.util.*;

class Client {

	private Socket so;
	private BufferedReader br;
	private PrintWriter pw;
	private Scanner sc;
	private String mess, send;
	
	public Client() {
		connection();
		buffered();
        sc = new Scanner(System.in);
	}
	
	public void menu() {
		try {
			mess = br.readLine();
		} catch (IOException e) {
			System.out.println("Echec read");
		}
		String []tab = mess.split("&");
		for(String s : tab) System.out.println(s);
	}
	
	public void communication() {

		read();
		read();
		do {
			send();
			switch(send) {
				case "MENU" :
					menu();
	        		break;
	        	case "CONNECT" :
	        		//login
	            	read();
	            	send();
	            	//password
	            	read();
	            	send();
	            	//connexion
	            	read();
	            default :
	            	System.out.println("Cette commande n'existe pas");
	            }
	        }while(! send.equals("DISCONNECT"));
	        read();
	        close();
	}
	
	private void close() {
        pw.close();
        try {
			br.close();
		} catch (IOException e) {
			System.out.println("Echec br.close()");
		}
        try {
			so.close();
		} catch (IOException e) {
			System.out.println("Echec so.close()");
		}
	}
	
	private void connection() {
		try {
			so = new Socket("localhost", 4242);
		} catch (UnknownHostException e) {
			System.out.println("Erreur host");
		} catch (IOException e) {
			System.out.println("Echec connexion au serveur");
		}
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
        String mess;
		try {
			mess = br.readLine();
	        System.out.println(mess);

		} catch (IOException e) {
			System.out.println("Echec du readLine");
		}
	}
	
	private void send() {
    	System.out.print("> ");
    	send = sc.nextLine();
        pw.println(send);
        pw.flush();
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.communication();
	}
}
