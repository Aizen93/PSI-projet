import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientTCP {
	//Instanciation des options du serveur auquel on se connecte
	private static int portTCPServer = 1027;

	//Instanciation des elements de communications avec le serveurs
	private Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	private Scanner inFromUser;
	private String pseudoCourant;
	private ClientUDP clientUDP;
	private boolean isConnected;


	public ClientTCP() {
		try {
			this.socket = new Socket("localhost", this.portTCPServer);
			this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.inFromUser = new Scanner(System.in);
			this.pseudoCourant = "invite";
			this.isConnected = false;
		} catch(Exception e) {
			System.out.println("Connexion impossible, le Serveur n'a peut etre pas démarré");
		}	
	}

	public static void main(String[] args) {
		ClientTCP clientTCP = new ClientTCP();
		if(clientTCP.socket==null)	return;
		clientTCP.inFromUser = new Scanner(System.in);
		boolean connected = true;
		String commande;
		int portUDP = 0;
		while(connected) {
			System.out.println("Entrez votre choix : 0 - Quitter l'application, 1 - Connect, 2 - Disconnect, 3 - Add annonce,"
					+ " 4 - Voir toutes les annonces, 5 - Voir mes annonces,\n 6 - Voir les annonces avec filtre,"
					+ " 7 - Supprimer une annonce, 8 - Envoyer un message à un utilisateur, 9 - Lire un message, "
					+ "10 - Lire tout ses messages");
			commande = clientTCP.inFromUser.nextLine();
			switch(commande) {
			case "0":
				clientTCP.quit();
				break;
			case "1":
				if(!clientTCP.isConnected)	portUDP = clientTCP.connect();
				else	System.out.println("Vous etes deja connecte");
//				clientTCP.createUDP(portUDP);
				break;
			case "2":
				clientTCP.disconnect();
				break;
			case "3":
				clientTCP.addAnnonce();
				break;
			case "4":
				clientTCP.allAnnonce("ALLANNS");
				break;
			case "5":
				clientTCP.allAnnonce("MYYANNS");
				break;
			case "6":
				clientTCP.allAnnonce("ANNONCE");
				break;
			case "7":
				clientTCP.deleteAnnonce();
				break;
			case "8":
				portUDP = clientTCP.sendMessage();
				System.out.println(portUDP);
				if(portUDP!=-1)	clientTCP.sendUDP(portUDP);
				else	System.out.println("L'annonce n'existe pas");
				break;
			case "9":
				Message m = clientTCP.clientUDP.readOne();
				clientTCP.repondre(m);
				break;
			case "10":
				clientTCP.clientUDP.readAll();
				break;
			default:
				break;

			}
		}
	}

	public void quit() {
		try {
			// Ecriture et envoi du message
			String message = "QUIT";
			printWriter.println(message);
			printWriter.flush();
			//Lecture
			String reception = bufferedReader.readLine();
			if(reception.equals("OK")) {
				System.out.println("Déconnection réussi");
			}
			else {
				System.out.println("La déconnection ne s'est pas passé comme prévu");
			}
			printWriter.close();
			bufferedReader.close();
			socket.close();
			System.exit(1);
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Mauvaise lecture");
		}
	}

	public int connect() {
		System.out.println("Entrez votre pseudo : ");
		String pseudo = inFromUser.nextLine();
		System.out.println("Entrez votre mdp");
		String mdp = inFromUser.nextLine();
		try {
			// Ecriture et envoi du message
			String message = "CONNECT;"+pseudo+";"+mdp;
			printWriter.println(message);
			printWriter.flush();
			//Lecture
			String reception = bufferedReader.readLine();
			String[] receptionSplit = reception.split(";");
			if(receptionSplit[0].equals("CONNECT")) {
				System.out.println("Connection réussi");
				setPseudoCourant(pseudo);
				createUDP(Integer.parseInt(receptionSplit[1]));	//Demarre l'ecoute en UDP
				this.isConnected = true;
			} else if(receptionSplit[0].equals("FAIL")) {
				System.out.println(receptionSplit[1]);
				return -1;
			}
			else {
				System.out.println("Message Inconnu : " + reception);
				return -1;
			}
			System.out.println(Integer.parseInt(receptionSplit[1]));
			return Integer.parseInt(receptionSplit[1]);
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Mauvaise lecture");
			return -1;
		}

	}

	public boolean disconnect() {	
		try {
			String message = "DISCONN";
			printWriter.println(message);
			printWriter.flush();
			//Lecture
			String reception = bufferedReader.readLine();
			if(reception.equals("OK")) {
				System.out.println("Déconnection réussi");
				pseudoCourant = "invite";
				if(clientUDP!=null)	clientUDP.stop();
				isConnected = false;
			}			
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Deconnection impossible");
			return false;
		}
	}
	
	public boolean addAnnonce() {
		System.out.println("Entrez le domaine : ");
		String domaine = inFromUser.nextLine();
		System.out.println("Entrez le prix : ");
		String prix = inFromUser.nextLine();
		System.out.println("Entrez la description : ");
		String description = inFromUser.nextLine();
		try {
			String message = "ADDANNS;"+domaine+";"+prix+";"+description;
			printWriter.println(message);
			printWriter.flush();
			//Lecture
			String reception = bufferedReader.readLine();
			String[] receptionSplit = reception.split(";");
			if(receptionSplit[0].equals("OK")) {
				System.out.println("Annonce ajouté");
			} else if(receptionSplit[0].equals("FAIL")){
				System.out.println(receptionSplit[1]);
			}
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}

	public boolean allAnnonce(String annonce) {
		String message = annonce;
		if(annonce.equals("ANNONCE")) {
			System.out.println("Entrez le type : ");
			String type = inFromUser.nextLine();			
			message +=";" + type;
		} 
		try {
			printWriter.println(message);
			printWriter.flush();
			//Lecture
			String reception = bufferedReader.readLine();
			String[] receptionSplit = reception.split(";");
			if(receptionSplit[0].equals("FAIL")){
				System.out.println(receptionSplit[1]);
			}else if(receptionSplit[0].equals(annonce)){
				if(receptionSplit.length == 1 ){
					System.out.println("Il n'y a pas encore d'annonce");
				}else if(receptionSplit.length > 1){
					System.out.println("All announces online :");
					String[] tmp = receptionSplit[1].split("###");
					for(int i = 0; i < tmp.length; i++){
						String [] src = tmp[i].split("\\*\\*\\*");

						System.out.println("+----------------------------------------------+");
						System.out.println("| Reference : " + src[2]);
						System.out.println("| Domain : " + src[0]);
						System.out.println("| Price: " + src[3]);
						System.out.println("| Owner: " + src[4]);
						System.out.println("| Description : " + src[1]);
						System.out.println("+----------------------------------------------+");
					}
				}
			}else{
				System.out.println("SERVER BAD RESPONSE");
			}
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}

	}

	public boolean deleteAnnonce() {
		System.out.println("Entrez la ref : ");
		String ref = inFromUser.nextLine();
		try {
			String message = "DELANNS;"+ref;
			printWriter.println(message);
			printWriter.flush();
			//Lecture
			String reception = bufferedReader.readLine();
			String[] receptionSplit = reception.split(";");
			if(receptionSplit[0].equals("OK")) {
				System.out.println("Annonce supprimé");
			} else if(receptionSplit[0].equals("FAIL")){
				System.out.println(receptionSplit[1]);
			}
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}

	//TODO: A modifier si on recoit @IP + portUDP
	public int sendMessage() {
		System.out.println("Entrez la ref de l'annonce de l'utilisateur à contacter : ");
		String ref = inFromUser.nextLine();
		try {
			int portUDP = -1;
			String message = "MESSAGE;"+ref;
			printWriter.println(message);
			printWriter.flush();
			//Lecture
			String reception = bufferedReader.readLine();
			String[] receptionSplit = reception.split(";");
			if(receptionSplit[0].equals("MESSAGE")) {
				portUDP = Integer.parseInt(receptionSplit[1]);
//				System.out.println("DEBUG - Port UDP récupéré : " + receptionSplit[1]);
			} else if(receptionSplit[0].equals("FAIL")){
				System.out.println(receptionSplit[1]);
			}
//			System.out.println("DEBUG - portUDP dans sendMessage() : " + portUDP);
			return portUDP;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
			return -1;
		} catch (IOException e) {
			System.out.println("Erreur de readline");
			return -1;
		}
	}
	
	public void sendUDP(int portUDP) {
		try {
			System.out.println("Entrez votre message :");
			String msg = inFromUser.nextLine();
			String msgSend = "WRITETO;"+pseudoCourant+";"+portUDP+";"+msg;
			clientUDP.sendTo(portUDP, msgSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createUDP(int portUDP) {
		try {
			clientUDP = new ClientUDP(portUDP);
			clientUDP.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void repondre(Message m) {
		System.out.println("Voulez vous répondre ? (oui|non)");
		
	}

	//------------------------------------------------------------------------------------------------------------

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public int getPortTCPServer() {
		return portTCPServer;
	}

	public void setPortTCPServer(int portTCPServer) {
		this.portTCPServer = portTCPServer;
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}

	public void setPrintWriter(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}

	//------------------------------------------------------------------------------------------------------------
	//Cree un String a partir d'un tableau de Bytes
	public String byteToString(byte[] messageByte) {
		return new String(messageByte);
	}

	//Cree un String en prenant les valeurs du tableau de byte de debut a offset(fin)
	public String byteToString(byte[] messageByte,int debut, int offset) {
		return new String(messageByte,debut,offset);
	}

	public String getPseudoCourant() {
		return pseudoCourant;
	}

	public void setPseudoCourant(String pseudoCourant) {
		this.pseudoCourant = pseudoCourant;
	}


}	

