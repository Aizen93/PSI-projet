import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Client {

    private SSLSocket so;
    private BufferedReader br;
    private PrintWriter pw;
    private final Scanner sc;
    private String message_received;
    private boolean exit;
    private boolean mode_disconnected;
    private String command = "";
    private String message_to_send;
    private ClientUDP client_udp;
    private int current_user_udp_port = 0;
    private int serverTCPPort = 1027;
    private InetSocketAddress address;
    private String username, password, serverIP, current_user_IP;

    public Client() {
        Crypto.generateKeyPair();
        this.exit = false;
        this.mode_disconnected = true;
        this.sc = new Scanner(System.in);
        connection();
        buffered();
    }
    
    /**
     * A function that finds a free port for the client.
     *
     * @return The first free port found
     */
    private int getFreePort() {
        int port;
        DatagramSocket server;
        for (port = 1; port <= 9999; port++) {
            try {
                server = new DatagramSocket(port);
                server.close();
                break;
            } catch (IOException ex) {
                // System.out.println("The following port is not free " + port + ".");
            }
        }
        if (port == 10000) {
            return -1;
        }
        return port;
    }

    public void menu() {
        try {
            message_received = br.readLine();
        }catch (IOException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Echec read" + Affichage.ANSI_RESET);
        }
        String []tab = message_received.split("&");
        for(String s : tab) System.out.println(s);
    }
    

    public void communication() throws NumberFormatException, SocketException {
        while(!exit) {
            Affichage.display_command_request();
            command = sc.nextLine();
            switch (command) {
                case "DISCONN":
                    disconnect();
                    break;
                case "CONNECT":
                    connecte();
                    break;
                case "ALLANNS":
                    announcesHandler("ALLANNS");
                    break;
                case "MYYANNS":
                    announcesHandler("MYYANNS");
                    break;
                case "ANNONCE":
                    announcesHandler("ANNONCE");
                    break;
                case "DELANNS":
                    deleteAnnounce();
                    break;
                case "ADDANNS":
                    addAnnounce();
                    break;
                case "READ":
                    client_udp.read(username, current_user_udp_port, current_user_IP, sc);
                    break;
                case "READALL" :
                    client_udp.readAll();
                    break;
                case "QUIT":
                    quitApply();
                    break;
                case "MESSAGE":
                    message();
                    break;
                default:
                    Affichage.display_error("Wrong command !");
            }
            if(!mode_disconnected) Affichage.display_message_notification(client_udp.sizeMessage());
        }
    }
    
    private void message() {
        try{
            System.out.print(">> Ref announce : ");
            int announce_ref = Integer.parseInt(sc.nextLine());
            send("MESSAGE;"+announce_ref);
            read();
            String []tab = message_received.split(";");
            //if les bon result
            if(tab.length == 2 && tab[0].equals("FAIL")) {
                //System.out.println(Affichage.RED_BRIGHT + "Sorry no annouce found with ref = "+ announce_ref + Affichage.ANSI_RESET);
                Affichage.display_error(tab[1] + announce_ref);
            }else {
                if(tab[0].equals("MESSAGE") && tab.length == 3) {
                    address = new InetSocketAddress(tab[2], Integer.parseInt(tab[1]));
                    System.out.print(">> Message : ");
                    message_to_send = "WRITETO;"+ username + ";" + current_user_udp_port + ";" + current_user_IP + ";" + sc.nextLine();

                    client_udp.sendTo(address, message_to_send);
                }else {
                    Affichage.display_error("Error Server bad response !");
                }
            } 
        }catch(Exception e){
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "ERROR EXCEPTION : something went really wrong" + Affichage.ANSI_RESET);
            e.printStackTrace();
        }
    }
    
    private void disconnect(){
        try{
            if(!mode_disconnected){
                send(command);
                read();
                if(message_received.equals("OK")) {
                    mode_disconnected = true;
                    client_udp.stop();
                    Affichage.display_disconnected();
                } else {
                    Affichage.display_error("Sorry, couldn't disconnect");
                    System.exit(1);
                }
            }else{
                System.out.println(Affichage.YELLOW_BRIGHT + "You are not logged in to disconnect" + Affichage.ANSI_RESET);
            }
        }catch(Exception e){
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "ERROR EXCEPTION : something went really wrong" + Affichage.ANSI_RESET);
        }
    }
    
    private void quitApply(){
        try{
            message_to_send = "QUIT";
            send(message_to_send);
            read();
            if(message_received.equals("OK")){
                System.out.println(Affichage.YELLOW_BRIGHT + "Server connexion destroyed.... see you soon... BYE !" + Affichage.ANSI_RESET);
                close();
                System.exit(1);
            }else{
                Affichage.display_error("BAD SERVER RESPONSE, destroying everything... BYE");
                close();
                System.exit(1);
            }
        }catch(Exception e){
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "ERROR EXCEPTION : something went really wrong" + Affichage.ANSI_RESET);
        }
    }
    
    private void connecte() {
        try{
            if(mode_disconnected){
                System.out.println("Please fill the form bellow :");
                System.out.print(">> Login: ");
                username = sc.nextLine();
                //System.out.print(">> Password: ");
                //password = sc.nextLine();
                Console console = System.console();
                password = new String(console.readPassword(">> Password: "));
                current_user_udp_port = getFreePort();
                if(current_user_udp_port == -1){
                    Affichage.display_error("Sorry there is no free port available... Try again later !");
                    System.exit(1);
                }
                current_user_IP = so.getLocalAddress().getHostAddress();
                message_to_send = "CONNECT;" + username + ";" + password + ";" + current_user_udp_port + ";" + current_user_IP;
                send(message_to_send);
                
                read();
                String[]tab = message_received.split(";");
                if(tab[0].equals("CONNECT") && tab.length == 1){
                    mode_disconnected = false;
                    System.out.println("Attributed port : "+ Affichage.GREEN_BOLD_BRIGHT + current_user_udp_port + Affichage.ANSI_RESET + " !");
                    client_udp = new ClientUDP();
                    client_udp.create_datagram_socket(current_user_udp_port);
                    client_udp.start();
                    Affichage.display_connected();
                }else if(tab[0].equals("FAIL") && tab.length == 2){
                    current_user_udp_port = 0;
                    System.out.println(Affichage.YELLOW_BRIGHT + tab[1] + Affichage.ANSI_RESET);
                }else{
                    current_user_udp_port = 0;
                    Affichage.display_error("Server bad response !");
                }
            }else{
                System.out.println(Affichage.YELLOW_BRIGHT + "You are alrady logged in, nothing to be done" + Affichage.ANSI_RESET);
            }
        }catch(Exception e){
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "ERROR EXCEPTION : something went really wrong" + Affichage.ANSI_RESET);
        }
    }
    
    private void announcesHandler(String command){
        try{
            if(command.equals("MYYANNS") && mode_disconnected){
                Affichage.display_error("You are disconnected you can't see personnal annouces !");
            }else{
                message_to_send = command;
                if(command.equals("ANNONCE")){
                    System.out.println("Please fill the form bellow : ");
                    System.out.print(">> Filter (Key word) : ");
                    message_to_send += ";"+sc.nextLine();
                }
                send(message_to_send);
                read();
                String[] res = message_received.split(";");

                if(res.length == 2 && res[0].equals("FAIL")){
                    Affichage.display_error("Error : " + res[1]);
                }else if(res[0].equals(command)){
                    if(res.length == 1 ){
                        System.out.println(Affichage.YELLOW_BRIGHT + "Nothing published yet, use ADDANNS to be the first to publish an annouce" + Affichage.ANSI_RESET);
                    }else if(res.length > 1){
                        System.out.println("All announces online :");
                        String[] tmp = res[1].split("###");
                        for(int i = 0; i < tmp.length; i++){
                            String [] src = tmp[i].split("\\*\\*\\*");
                            Affichage.display_annonce(src[2], src[0], src[3], src[4], src[1]);
                        }
                    }
                }else{
                    if(res[0].equals("FAIL") && res.length == 2) Affichage.display_error(res[1]);
                    else Affichage.display_error("SERVER BAD RESPONSE");
                }
            }
        }catch(Exception e){
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "ERROR EXCEPTION : something went really wrong" + Affichage.ANSI_RESET);
        }
    }
    
    private boolean isNumeric(String str) { 
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    
    private void addAnnounce(){
        try{
            if(!mode_disconnected){
                System.out.println("Please fill the form bellow: ");
                System.out.print(">> domain : ");
                String domain = sc.nextLine();
                String price = "";
                while(!isNumeric(price)){
                    System.out.print(">> price : ");
                    price = sc.nextLine();
                }
                System.out.print(">> description : ");
                String description = sc.nextLine();

                message_to_send = "ADDANNS;" + domain + ";" + price + ";" + description;
                send(message_to_send);
                read();
                String[] tab = message_received.split(";");
                if(tab[0].equals("OK") && tab.length == 1){
                    System.out.println(Affichage.GREEN_BRIGHT + "Announce added succefully !" + Affichage.ANSI_RESET);
                }else if(tab[0].equals("FAIL") && tab.length == 2){
                    Affichage.display_error(tab[1]);
                }else{
                    Affichage.display_error("Server bad response !");
                }
            }else{
                System.out.println(Affichage.YELLOW_BRIGHT + "You are not logged in to add an announce" + Affichage.ANSI_RESET);
            }
        }catch(Exception e){
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "ERROR EXCEPTION : something went really wrong" + Affichage.ANSI_RESET);
        }
    }
    
    private void deleteAnnounce(){
        try{
            if(!mode_disconnected){
                System.out.print(">> enter the announce's reference : ");
                message_to_send = "DELANNS;";
                message_to_send += sc.nextLine();
                send(message_to_send);
                read();
                String[] tab = message_received.split(";");
                if(tab[0].equals("OK") && tab.length == 1){
                    System.out.println(Affichage.GREEN_BRIGHT + "Announce deleted succesfully !" + Affichage.ANSI_RESET);
                }else if(tab[0].equals("FAIL") && tab.length == 2){
                    Affichage.display_error("Failed deleting the announce");
                }else{
                    Affichage.display_error("Server bad response !");
                }  
            }else{
                System.out.println(Affichage.YELLOW_BRIGHT + "You are not logged in to delete an announce" + Affichage.ANSI_RESET);
            }
        }catch(Exception e){
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "ERROR EXCEPTION : something went really wrong" + Affichage.ANSI_RESET);
        }
    }
    

    private void close() {
        pw.close();
        try {
            br.close();
        }catch (IOException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Echec br.close()" + Affichage.ANSI_RESET);
        }
        try {
            so.close();
        }catch (IOException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Echec so.close()" + Affichage.ANSI_RESET);
        }
    }

    private void connection() {
        try {
            
            serverIP = "";
            while(!isIPAddress(serverIP)){
                System.out.print(">> Please enter a valid server's IP address : ");
                serverIP = sc.nextLine();
            }
            System.setProperty("javax.net.ssl.trustStore", "client.jsk");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            so = (SSLSocket)socketFactory.createSocket(serverIP, serverTCPPort);

            //so = new Socket(serverIP, serverTCPPort);
        }catch (UnknownHostException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Error host" + Affichage.ANSI_RESET);
        }catch (IOException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Echec connexion au serveur" + Affichage.ANSI_RESET);
        }
    }

    private void buffered() {
        try {
            br = new BufferedReader(new InputStreamReader(so.getInputStream()));
        } catch (IOException | NullPointerException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Echec création BufferedReader" + Affichage.ANSI_RESET);
        }
        try {
            pw = new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
        } catch (IOException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Echec création PrintWriter" + Affichage.ANSI_RESET);
        }
    }

    private void read() {
        try {
            message_received = br.readLine();
        }catch (IOException e) {
            System.out.println(Affichage.RED_BACKGROUND_BRIGHT + "Echec du readLine" + Affichage.ANSI_RESET);
        }
    }

    private void send(String message) {
        pw.println(message);
        pw.flush();
    }
    
    /**
     * A function that handles the reception of the username and its validty.
     *
     * @return The valid username as a string.
     */
    private String username() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a username of 8 alphanumerical characters: ");
        String pseudo = sc.nextLine();
        Pattern p = Pattern.compile("[a-zA-Z_0-9]*");
        Matcher m = p.matcher(pseudo);
        boolean b1 = m.matches();
        while (pseudo.length() != 8 || !b1) {
            System.out.print("Enter a username of 8 alphanumerical characters: ");
            pseudo = sc.nextLine();
            m = p.matcher(pseudo);
            b1 = m.matches();
        }
        return pseudo;
    }
    
    private boolean isIPAddress(String ip){
        Pattern p = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        Matcher match = p.matcher(ip);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws NumberFormatException, SocketException, UnknownHostException {
        Client client = new Client();
        System.out.println(Affichage.YELLOW_BRIGHT + "####### Welcome to M2 PSI project #######" + Affichage.ANSI_RESET);
        //System.out.println(Affichage.isMac() + " - " + Affichage.isWindows() + " - " +Affichage.isUnix());
        client.communication();
    }
}
