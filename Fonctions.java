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
//            System.exit(1);
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
        String pseudo = msg[1];
        String mdp = msg[2];
        Users user = null;
        for(Users u : listUsersConnected) {
            if( pseudo.equals(u.getPseudo())) {
            	if(u.getConnect()) {
            		out.println("FAIL;Vous etes deja connecté");
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
        	user = new Users(pseudo, mdp,portUDP);
        	portUDP++;
        	listUsersConnected.add(user);        	
        }
        out.println("CONNECT;"+user.getPortUDP());
        out.flush();
        return user;
    }
    private synchronized void addAnnonce(Users user,String[]token){
        if(user == null) {
            out.println("FAIL;vous n'êtes pas connectés");
        } else if(user!= null && token[2].trim().matches("\\d+")){
            ref++;
            Annonce a = new Annonce(ref,user.getPseudo().trim(),token[1].trim().toLowerCase(), Integer.parseInt(token[2].trim()),token[3].trim());
            annoncesAll.add(a);
            out.println("OK");
        } else {
        	out.println("FAIL;le prix indiqué n'est pas un nombre");
        }
        out.flush();
    }
    private synchronized void deleteAnnonceParRef(Users u,String ref){
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
                	}/* else if(annoncesAll.contains(Integer.parseInt(ref))) {
                		out.println("FAIL;l'annonce a été supprimé.");
                        out.flush();
                    }*/else{
                        out.println("FAIL;vous n'êtes pas le propriétaire de l'annonce.");
                        out.flush();
                    }
                	break;
                }/*else if(!annoncesAll.contains(Integer.parseInt(ref)) && ref.trim().matches("\\d+") ){
                    out.println("FAIL;Il n'a pas d'annonce avec le numero de ref que vous aves donnés.");
                    out.flush();
                    break;
                }*//*else if(!ref.trim().matches("\\d+")){
                    out.println("FAIL;Vous n'avez pas tapés un nombre.");
                    out.flush();
                }*/
            }
        } else if( u!=null && annoncesAll.size() == 0 && !ref.trim().matches("\\d+")){
            out.println("FAIL;Vous n'avez pas tapés un nombre et il n'a pas d'annonces.");
            out.flush();
        } else if( u!=null && annoncesAll.size() == 0){
            out.println("FAIL;Il n'y a pas d'annonces.");
            out.flush(); 
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
//        if(annoncesAll.size() > 0){
            String message = "ANNONCE;";
            for(int i = 0;i < annoncesAll.size();i++){                
                if(annoncesAll.get(i).getDomaine().equals(type.toLowerCase())){
                    message += annoncesAll.get(i).getDomaine()+"***"+ annoncesAll.get(i).getContenu()+
                    "***"+annoncesAll.get(i).getRef()+"***"+annoncesAll.get(i).getPrix() + "***"+annoncesAll.get(i).getLogin()+"###";        
                }
            }
//            if(message.equals("ANNONCE;"))
//            out.println("FAIL;Il n' a pas d'annonces à ce type que vous avez choisi.");
//            out.flush();
            out.println(message);
            out.flush();
//        }
//        else{ 
//            out.println("FAIL;Il n'y a pas d'annonces.");
//            out.flush();
//        }
    }
    private synchronized void envoiLePort(String id_annonce){
        String envoi = "MESSAGE;";
        int portUDP = 0;
        for(int i = 0; i < annoncesAll.size(); i++){
            if(annoncesAll.get(i).getRef( ) == Integer.parseInt(id_annonce)){
                String login = annoncesAll.get(i).getLogin();
                System.out.println("DEBUG - " + login);
                for(int j = 0; j < listUsersConnected.size(); j++){
                    if(listUsersConnected.get(j).getPseudo().equals(login)){
                       portUDP=listUsersConnected.get(j).getPortUDP();
                       break;
                    }
                }
                break;
            }
        }
        if(portUDP==0) {
        	out.println("FAIL;L'annonce n'existe pas");
        } else {
        	out.println(envoi+portUDP);
        }
        out.flush();
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