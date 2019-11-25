import java.io.*;
import java.net.Socket;
import java.util.*;

public class Fonctions implements Runnable{ 
    
    private Socket socket;
    private PrintWriter out ;
    private BufferedReader in ;
    private String protocole;
    private static int ref = 0;      
    private static ArrayList<Users> listUsersConnected = new ArrayList<Users>() ;
    private static ArrayList<Annonce> annoncesAll = new ArrayList<Annonce>();
    private static int portUDP = 8531;

    
    public Fonctions(Socket _socket) {
        this.socket = _socket;
    }

    @Override
    public void run() {
    	Users u = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));             
            String str = "";
            String [] token = null;
            while ((str = in.readLine()) != null) {
                System.out.println("Message: "+ str);
                if(!str.contains(";")){
                    protocole = str; 
                }else{
                    token = str.split(";");
                    protocole = token[0];
                }   
                switch (protocole) {
                    case "CONNECT":
                        u = connect(token);
                        break;
                    case "ALLANNS":
                        afficherAllAnnonces();
                        break;
                    case "DISCONN":
                        u = disconnection(u);
                        break;
                    case "ADDANNS":
                        addAnnonce(u,token);
                        break;
                    case "MYYANNS":
                        afficherMesAnnoneces(u);
                        break;
                    case "ANNONCE":
                        afficherAnnonceParType(token[1]);
                        break;
                    case "DELANNS":
                        deleteAnnonceParRef(u,token[1]);
                        break;
                    case "MESSAGE":
                        envoiLePort(token[1]);
                        break;
                    case "QUIT":
                    	quit();
                        break;
                    default:
                        out.println("FAIL;Veuillez respecter le protocole");
                        out.flush();
                }
            }

        } catch (IOException e) {
        	String nameClient;
        	if(u==null)	nameClient = "invite";
        	else	nameClient = u.getPseudo();
        	System.out.println("Le Client " + nameClient +  " s'est déconnecté de manière brutale");
            quit();
        }
    }
    
    private void quit() {
        try{
        	out.println("OK");
        	out.flush();
            out.close();
            in.close();
            socket.close();
            return;
        }catch(IOException e){
            out.println("FAIL;vous n'êtes pas déconnectés");
            out.flush();
            System.exit(1);
        }
	}

	private Users connect(String msg[]) {
        String pseudo="",mdp="",ip = "";
        try{
            pseudo = msg[1].trim();
        }catch(NullPointerException e){
            out.println("FAIL;ous n'avez pas tapé votre pseudo.");
            System.out.println("Vous n'avez pas tapé votre pseudo.");
        }
        try{
            mdp = msg[2].trim();
        }catch(NullPointerException e){
            out.println("FAIL;Vous n'avez pas tapé votre mot de passe.");
            System.out.println("Vous n'avez pas tapé votre mot de passe.");
        }
        try{
            ip = msg[3];
        }catch(NullPointerException e){
            out.println("FAIL;Vous n'avez pas tapé votre IP.");
            System.out.println("Vous n'avez pas tapé votre IP.");
        }
        Users user = null;
        for(Users u : listUsersConnected) {
            if( pseudo.equals(u.getPseudo())) {
            	if(u.getConnect()) {
            		out.println("FAIL;Vous êtes déjà connecté");
            		out.flush();
            		user = u;
            		break;
            	}
            	else if (mdp.equals(u.getMdp())) {
                    user = u;
                    user.setConnect(true);
                    break;
                }else{
                	out.println("FAIL;mot de passe incorrect");
                	out.flush();
                    return null;
                }
            }
        }
        if(user == null){
        	user = new Users(pseudo, mdp,portUDP,ip);
        	portUDP++;
        	listUsersConnected.add(user);        	
        }
        out.println("CONNECT;"+user.getPortUDP());
        out.flush();
        return user;
    }
    private synchronized void addAnnonce(Users user,String[]token){
        String domaine="", desc = "";
        if(user == null) {
            out.println("FAIL;vous n'êtes pas connectés");
        } else if(user!= null && token[2].trim().matches("\\d+")){
            ref++;
             try{
                domaine = token[1].trim().toLowerCase();
            }catch(NullPointerException e){
                out.println("FAIL;Le domaine est vide.");
                System.out.println("Le domaine est vide.");
            }
            try{
                desc = token[3].trim();
            }catch(NullPointerException e){
                out.println("FAIL;La description est vide.");
                System.out.println("La description est vide.");
            }
            Annonce a = new Annonce(ref,user.getPseudo().trim(),domaine, Integer.parseInt(token[2].trim()),desc);
            annoncesAll.add(a);
            out.println("OK");
        } else {
            out.println("FAIL;le prix indiqué n'est pas un nombre");
        }
        out.flush();
    }
    private synchronized void deleteAnnonceParRef(Users u,String ref){
        try{
            if(u == null){
            out.println("FAIL;vous êtes déconnectés.");
            out.flush();
            }else if( u !=  null && (annoncesAll.size() > 0)){
                for (int i = 0; i < annoncesAll.size() ; i++) {
                    if(annoncesAll.get(i).getRef() == Integer.parseInt(ref)){
                        if(annoncesAll.get(i).getLogin().equals(u.getPseudo())) {
                            annoncesAll.remove(i);
                            out.println("OK");
                            out.flush();
                        }else{
                            out.println("FAIL;vous n'êtes pas le propriétaire de l'annonce.");
                            out.flush();
                        }
                        break;
                    }
                }
            } else if( u!=null && annoncesAll.size() == 0 && !ref.trim().matches("\\d+")){
                out.println("FAIL;Vous n'avez pas tapés un nombre et il n'a pas d'annonces.");
                out.flush();
            } else if( u!=null && annoncesAll.size() == 0){
                out.println("FAIL;Il n'y a pas d'annonces.");
                out.flush(); 
            }     
        }catch(NullPointerException e){
            System.out.println("La référence est vide.");
        }
        
    }
    private synchronized void afficherAllAnnonces(){
        if( annoncesAll.size() > 0 ){
            String message ="ALLANNS;";
            for(int i = 0;i < annoncesAll.size();i++){
                message += annoncesAll.get(i).getDomaine()+"***"+ annoncesAll.get(i).getContenu()+
                "***"+annoncesAll.get(i).getRef()+"***"+annoncesAll.get(i).getPrix() + "***"+annoncesAll.get(i).getLogin()+"###";
            }  
            out.println(message);
            out.flush();
        }else{
            out.println("ALLANNS;");
            out.flush();
        }     
    }
    private synchronized void afficherMesAnnoneces(Users usr){
        if(usr != null){
            String message ="MYYANNS;";
            for(int i = 0;i < annoncesAll.size();i++){
                if(annoncesAll.get(i).getLogin().equals(usr.getPseudo())){
                    message += annoncesAll.get(i).getDomaine()+"***"+ annoncesAll.get(i).getContenu()+
                    "***"+annoncesAll.get(i).getRef()+"***"+annoncesAll.get(i).getPrix() + "***"+annoncesAll.get(i).getLogin()+"###";    
                }
            }    
            out.println(message);
            out.flush();
        }else{
            out.println("FAIL;vous n'êtes pas connectés");
            out.flush();
        }
    }   
    private synchronized void afficherAnnonceParType(String type){
        try{
            String message = "ANNONCE;";
            for(int i = 0;i < annoncesAll.size();i++){                
                if(annoncesAll.get(i).getDomaine().equals(type.toLowerCase())){
                    message += annoncesAll.get(i).getDomaine()+"***"+ annoncesAll.get(i).getContenu()+
                    "***"+annoncesAll.get(i).getRef()+"***"+annoncesAll.get(i).getPrix() + "***"+annoncesAll.get(i).getLogin()+"###";        
                }
            }
            out.println(message);
            out.flush();
        }catch(Exception e){
            System.out.println("Le domaine (ou le type) de recherche est vide.");
        }
           
    }
    private synchronized void envoiLePort(String id_annonce){
        try{
            String envoi = "MESSAGE;";
            int portUDP = 0;
            String ip = "";
            for(int i = 0; i < annoncesAll.size(); i++){
                if(annoncesAll.get(i).getRef( ) == Integer.parseInt(id_annonce)){
                    String login = annoncesAll.get(i).getLogin();
                    for(int j = 0; j < listUsersConnected.size(); j++){
                        if(listUsersConnected.get(j).getPseudo().equals(login)){
                        portUDP=listUsersConnected.get(j).getPortUDP();
                        ip = listUsersConnected.get(j).getIP();
                        break;
                        }
                    }
                    break;
                }
            }
            if(portUDP==0) {
                out.println("FAIL;L'annonce n'existe pas");
            } else {
                out.println(envoi+portUDP+ip);
            }
            out.flush();    
        }catch(NullPointerException e){
            System.out.println("L'ID d'annonce est vide.");
        }    
    }
    private synchronized Users disconnection(Users usr){
        if(usr != null) {
            usr.setConnect(false);
            usr = null;
            out.println("OK");
        }else {
            out.println("FAIL;vous êtes déjà déconnectés");
        }
        out.flush();
        return usr;
    }
   
}