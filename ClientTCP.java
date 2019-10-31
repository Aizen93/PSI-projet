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
	private InputStream inputStream;
	private OutputStream outputStream;
	private PrintWriter pw;

	public ClientTCP() {
		try {
			this.socket = new Socket("localhost", this.portTCPServer);
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
            pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch(Exception e) {
			System.out.println("Connexion impossible");
			// e.printStackTrace(); //DEBUG
		}
		
	}
	
	public boolean connect() {
		Scanner inFromUser = new Scanner(System.in);
		System.out.println("Entrez votre pseudo : ");
		String pseudo = inFromUser.nextLine();
		System.out.println("Entrez votre mdp");
		String mdp = inFromUser.nextLine();
		inFromUser.close();
		try {
			String message = "CONNECT;"+pseudo+";"+mdp;
			pw.write(message);
			// Ecriture et envoi du message
//			outputStream.write(message.getBytes());
//			outputStream.flush();
            // Tableau de byte qu'on recoit avec le read()
//            byte[] msgReceivedBytes = new byte[4];
//            // Lecture du message
//            inputStream.read(msgReceivedBytes);
//			String msgReceived = byteToString(msgReceivedBytes);
//            System.out.println();
			return true;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
			return false;
		}
		
	}
	
	public boolean disconnect() {	
		try {
			String message = "DISCONNECT";
			outputStream.write(message.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("Impossible de se connecter au serveur");
			return false;
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
			return false;
		}
	}
	
	public boolean allAnnonce() {
		try {
			String message = "ANNS";
			outputStream.write(message.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("Impossible de se connecter au serveur");
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}
	
	public boolean myAnnonce() {
		try {
			String message = "MYANNS";
			outputStream.write(message.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("Impossible de se connecter au serveur");
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
		
	}
	
	public boolean filterAnnonce() {
		Scanner inFromUser = new Scanner(System.in);
		System.out.println("Entrez le type : ");
		String type = inFromUser.nextLine();
		inFromUser.close();
		try {
			String message = "ANN;"+type;
			outputStream.write(message.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("Impossible de se connecter au serveur");
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}

	}
	
	public boolean deleteAnnonce() {
		Scanner inFromUser = new Scanner(System.in);
		System.out.println("Entrez la ref : ");
		String ref = inFromUser.nextLine();
		inFromUser.close();
		try {
			String message = "DELETE;"+ref;
			outputStream.write(message.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("Impossible de se connecter au serveur");
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}
	
	public boolean addAnnonce() {
		Scanner inFromUser = new Scanner(System.in);
		System.out.println("Entrez le domaine : ");
		String domaine = inFromUser.nextLine();
		System.out.println("Entrez le prix : ");
		String prix = inFromUser.nextLine();
		System.out.println("Entrez la description : ");
		String description = inFromUser.nextLine();
		inFromUser.close();
		try {
			String message = "ADD;"+domaine+";"+prix+";"+description;
			outputStream.write(message.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("Impossible de se connecter au serveur");
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}
	
	public boolean consultMess() {
		try {
			String message = "CONSU";
			outputStream.write(message.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("Impossible de se connecter au serveur");
		} catch (NullPointerException e) {
			System.err.println("Vous n'etes plus connecté au serveur");
		} finally {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		ClientTCP clientTCP = new ClientTCP();
		Scanner inFromUser = new Scanner(System.in);
		System.out.println("Entrez votre choix : 1 - Connect, 2 - Disconnect, 3 - Add annonce, 4 - Voir toutes les annonces,"
				+ " 5 - Voir mes annonces, 6 - Voir les annonces avec filtre, 7 - ");
		
		if(clientTCP.connect()) {
			
		} else {
			System.err.println("Connection echoué");
		}
		
//		Socket socket = null;
//		BufferedReader br = null;
//		BufferedReader is = null;
//		PrintWriter os = null;
//
//		try {
//			socket = new Socket("localhost", 1027); 
//			br = new BufferedReader(new InputStreamReader(System.in));
//			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			os = new PrintWriter(socket.getOutputStream());
//		}
//		catch (IOException e){
//			System.err.print("Impossible de se connecter au serveur");
//		}

//		System.out.println("Pour quitter votre session taper la lettre d s'il vous plait");
//
//		try{
//			String response = is.readLine();
//			System.out.println("Serveur Respond : "+response);
//			boolean b = false;
//			while(!b){
//				String str = br.readLine();
//				os.println(str);
//				os.flush();
//				if(str.equals("d")){
//					try{
//						socket.close();
//						b = true;
//						return;
//					}catch(Exception e){
//
//					}
//				}
//				response = is.readLine();
//				System.out.println("Serveur Respond : "+response);
//
//			}
//			is.close();
//			os.close();
//			socket.close();
//		}catch(IOException e){
//			System.out.println("Socket lit une erreur");
//		}catch(NullPointerException e) {
//			System.out.println("Serveur introuvable");
//		}
//		finally{
//			is.close();
//			os.close();
//			socket.close();
//			System.out.println("connexion fermée");
//		}
	}
	
	//------------------------------------------------------------------------------------------------------------

	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
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

