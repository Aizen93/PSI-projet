import java.net.ServerSocket;
import java.net.Socket;

public class Connexion{

    public static void main(String[] args){
        
        try{
                    
            ServerSocket server = new ServerSocket(1027);
            System.out.println("..... Bienvenue sur notre Serveur ....");
            while(true){
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