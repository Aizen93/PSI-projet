import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Message {

    private final String pseudo, message, IP;
    private final int portUDP;
    private final DateTimeFormatter dtf;
    private final LocalDateTime now;

    public Message(String pseudo, int portudp, String IP, String mess) {
        this.pseudo = pseudo;
        this.message = mess;
        this.portUDP = portudp;
        this.IP = IP;
        this.dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.now = LocalDateTime.now();  
    }
    	
    public void afficher() {
        System.out.println(">> ["+ dtf.format(now) +"]["+ Affichage.GREEN_BRIGHT + pseudo + Affichage.ANSI_RESET + "] : " + message);
    }
    
    public String getPseudo() {
        return pseudo;
    }

    public int getPortUDP() {
        return portUDP;
    }
    
    public String getIP(){
        return IP;
    }
}
