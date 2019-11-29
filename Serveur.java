import java.net.ServerSocket;
import java.net.Socket;

public class Serveur{

    public static void main(String[] args){
        
        try{
                    
            ServerSocket server = new ServerSocket(1027);
            System.out.println("..... Bienvenue sur notre Serveur ....");
            while(true){
            	System.out.println(server.getInetAddress().getLocalHost().getHostAddress());
                Socket socket = server.accept();
                Fonctions serv = new Fonctions(socket);
                Thread t = new Thread(serv);
                t.start();  
            }
        }catch(Exception e){
            System.out.println("Serveur ne peut pas connecter");
        }       
    } 
     
}