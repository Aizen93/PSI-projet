import java.io.*;
import java.net.Socket;
import java.util.*;

public class Fonctions implements Runnable{ 
    
    private Socket socket;
    private PrintWriter out ;
    private BufferedReader in ;
    private String protocole;
    private static int ref = 0;      
    private static ArrayList<Users> listUsersConncted = new ArrayList<>() ;
    private static ArrayList<Annonce> annoncesAll = new ArrayList<>();
    private static String login = "";
    private static int portUDP = 8531;

    
    public Fonctions(Socket _socket) {
        this.socket = _socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));             
            String str = "";
            Users u = null;
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
                        if(u != null){
                            out.println("OK");
                            out.flush();
                        }else{
                            out.println("FAIL;mot de pass n'est pas correct");
                            out.flush();
                        }
                        break;
                    case "ALLANNS":
                        if(annoncesAll.size()>0){
                            afficherAllAnnonces();
                        }else{
                            out.println("ALLANNS;");
                            out.flush();
                        } 
                        break;

                    case "DISCONN":
                        if(u != null) {
                            u.setConnecte(false);
                            u = null;
                            out.println("OK");
                            out.flush();
                        }else {
                            out.println("FAIL;vous êtes déjà déconnectés");
                            out.flush();
                        }
                        break;

                    case "ADDANNS":
                        if(u != null){
                            boolean b = addAnnonce(u,token);
                            if(b){
                                out.println("OK");
                                out.flush();
                            }
                        }else{
                            out.println("FAIL;vous n'êtes pas connectés");
                            out.flush();
                        }
                        break;
                    case "MYYANNS":
                        if(u != null){
                            afficherMesAnnoneces(u);
                        }else{
                            out.println("FAIL;vous n'êtes pas connectés");
                            out.flush();
                        }
                        
                        break;
                    case "ANNONCE":
                        afficherAnnonceParType(token[1]);
                        break;
                    case "DELANNS":
                        deleteAnnonceParRef(u,token[1]);
                        break;
                    case "MESSAGE":
                        out.println("MESSAGE;"+portUDP);
                        out.flush();
                        portUDP++;
                        break;
                    case "QUIT":
                        try{
                            out.close();
                            in.close();
                            socket.close();
                            return;
                        }catch(IOException e){
                            out.println("FAIL;vous n'êtes pas déconnectés");
                            out.flush();
                            System.out.println("Pobleme dans le socket");
                            System.exit(1);
                        }
                        break;
                    default:
                        out.println("FIAL;vous n'avez pas bien saisié votre protocole");
                        out.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace(); 
            System.exit(1);
        }
    }
    private Users connect(String msg[]) {
        String pseudo = msg[1];
        String mdp = msg[2];
        Users user = null;
        for(Users u : listUsersConncted) {
            if( pseudo.equals(u.getPseudo())) {
                if (mdp.equals(u.getMdp())) {
                    user = u;
                    user.setConnecte(true);

                    return user;
                }else{
                    return user;
                }
            }
        }
        user = new Users(pseudo, mdp);
        listUsersConncted.add(user);
        return user;
    }
  
    private synchronized boolean addAnnonce(Users user,String[]token){
        if(user == null) {
            return false;
        } else if(user!= null && token[2].trim().matches("\\d+")){
            ref++;
            Annonce a = new Annonce(ref,user.getPseudo().trim(),token[1].trim(), Integer.parseInt(token[2].trim()),token[3].trim());
            annoncesAll.add(a);
            return true;
        }else{
            return false;
        }
        
    }
    private synchronized void deleteAnnonceParRef(Users u,String ref){
        if(u == null){
            out.println("FAIL;vous êtes déconnectés");
            out.flush();
        }else {
            for (int i = 0; i < annoncesAll.size() ; i++) {
                if(annoncesAll.get(i).getRef() == Integer.parseInt(ref)){
                	if(annoncesAll.get(i).getLogin().equals(u.getPseudo())) {
                		annoncesAll.remove(i);
                		out.println("OK");
                		out.flush();                		
                	} else if(annoncesAll.contains(Integer.parseInt(ref))) {
                		out.println("FAIL;l'annonce a été supprimé");
                		out.flush();
                	}else{
                        out.println("FAIL;vous n'êtes pas le propriétaire de l'annonce");
                		out.flush();
                    }
                	break;
                }
            }

        }        
    }

    private synchronized void afficherAllAnnonces(){
        String message ="ALLANNS;";
        for(int i = 0;i < annoncesAll.size();i++){
            message += annoncesAll.get(i).getDomaine()+"***"+ annoncesAll.get(i).getContenu()+
            "***"+annoncesAll.get(i).getRef()+"***"+annoncesAll.get(i).getPrix() + "***"+annoncesAll.get(i).getLogin()+"###";
        }  
        out.println(message);
        out.flush();
    }
    private synchronized void afficherMesAnnoneces(Users usr){
        String message ="MYYANNS;";
        for(int i = 0;i < annoncesAll.size();i++){
            if(annoncesAll.get(i).getLogin().equals(usr.getPseudo())){
                message += annoncesAll.get(i).getDomaine()+"***"+ annoncesAll.get(i).getContenu()+
                "***"+annoncesAll.get(i).getRef()+"***"+annoncesAll.get(i).getPrix() + "***"+annoncesAll.get(i).getLogin()+"###";    
            }
        }    
        out.println(message);
        out.flush();
    }   
    private synchronized void afficherAnnonceParType(String type){
        String message = "ANNONCE;";
        for(int i = 0;i < annoncesAll.size();i++){
            if(annoncesAll.get(i).getDomaine().equals(type)){
                message += annoncesAll.get(i).getDomaine()+"***"+ annoncesAll.get(i).getContenu()+
                "***"+annoncesAll.get(i).getRef()+"***"+annoncesAll.get(i).getPrix() + "***"+annoncesAll.get(i).getLogin()+"###";
                }         
        }    
        out.println(message);
        out.flush();

    }
    private Users isExiste(String p){
      
        for(int i = 0; i < listUsersConncted.size(); i++){
            if(!listUsersConncted.get(i).getPseudo().equals(p)){
                return null;
            } else{
                return listUsersConncted.get(i);
            }    
        }
       return null;
    }
}