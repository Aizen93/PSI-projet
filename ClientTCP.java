import java.io.*;
import java.net.*;
import java.util.Scanner;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientTCP {
	//Instanciation des options du serveur auquel on se connecte
	private int portTCPServer = 1027;

	//Instanciation des elements de communications avec le serveurs
	private Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	public Scanner inFromUser;


	public ClientTCP() {
		try {
			this.socket = new Socket("localhost", this.portTCPServer);
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			inFromUser = new Scanner(System.in);
		} catch(Exception e) {
			System.out.println("Connexion impossible");
			// e.printStackTrace(); //DEBUG
		}	
	}

	public static void main(String[] args) throws Exception {
		//TODO: Fermer le programme (return dans main) si le serveur est eteint
		ClientTCP clientTCP = new ClientTCP();
		clientTCP.inFromUser = new Scanner(System.in);
		boolean connected = true;
		String commande;
		while(connected) {
			System.out.println("Entrez votre choix : 1 - Connect, 2 - Disconnect, 3 - Add annonce, 4 - Voir toutes les annonces,"
					+ " 5 - Voir mes annonces,\n 6 - Voir les annonces avec filtre, 7 - Supprimer une annonce");
			commande = clientTCP.inFromUser.nextLine();
			switch(commande) {
			case "1":
				clientTCP.connect();
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
			default:
				connected = false; 
				break;

			}
		}
	}


	public boolean connect() {
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
			if(reception.equals("OK")) {
				System.out.println("Connection réussi");
			} else if(reception.equals("FAIL")) {
				System.out.println("Connection echoué");
			}
			else {
				System.out.println("Message Inconnu");
				return false;
			}
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Mauvaise lecture");
			return false;
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
			}			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Deconnection impossible");
			return false;
		}
	}

	public boolean allAnnonce() {
		try {
			String message = "ALLANNS";
			printWriter.println(message);
			printWriter.flush();
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}

	public boolean myAnnonce() {
		try {
			String message = "MYYANNS";
			printWriter.println(message);
			printWriter.flush();
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
	        String[] res = reception.split(";");
	        if(res[0].equals("FAIL")){
	            System.out.println("Error receiving " + annonce );
	        }else if(res[0].equals(annonce)){
	            if(res.length == 1 ){
	                System.out.println("Nothing published yet, use ADDANNS to be the first to publish an annouce");
	            }else if(res.length > 1){
	                System.out.println("All announces online :");
	                String[] tmp = res[1].split("###");
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
			if(reception.equals("OK")) {
				System.out.println("Annonce supprimé");
			} else if(reception.equals("FAIL")){
				System.out.println("Annonce pas supprimé");
			}
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
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
			if(reception.equals("OK")) {
				System.out.println("Annonce ajouté");
			} else if(reception.equals("FAIL")){
				System.out.println("Annonce rejeté");
			}
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}

	public boolean consultMess() {
		try {
			String message = "CONSU";
			printWriter.println(message);
			printWriter.flush();
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
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


}	

