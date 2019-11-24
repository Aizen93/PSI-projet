import java.net.*;
import java.io.*;
import java.util.*;

class Client {

    private Socket so;
    private BufferedReader br;
    private PrintWriter pw;
    private Scanner sc;
    private String message_received;
    private boolean exit;
    private boolean mode_disconnected;
    private String command = "";
    private String message_to_send;
    private ClientUDP client_udp;
    private int current_user_udp_port = 0;
    private InetSocketAddress address;
    private String username, password;
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public Client() {
        this.exit = false;
        this.mode_disconnected = true;
        connection();
        buffered();
        this.sc = new Scanner(System.in);
        /*Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    quitApply();
                    exit = true;
                    System.exit(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Main Client Thread interrupted - Throwed Exception catched in constructor!");
                }
            }
        });*/
    }

    public void menu() {
        try {
            message_received = br.readLine();
        }catch (IOException e) {
            System.out.println("Echec read");
        }
        String []tab = message_received.split("&");
        for(String s : tab) System.out.println(s);
    }
    

    public void communication() throws NumberFormatException, SocketException {
        //read();
        while(!exit) {
            System.out.print(ANSI_GREEN + "> Enter a command: " + ANSI_RESET);
            command = sc.nextLine();
            switch (command) {
                case "DISCONN":
                    disconnect();
                    break;
                case "CONNECT":
                    connecte();
                    break;
                case "ALLANNS":
                    getAllAnnounces("ALLANNS");
                    break;
                case "MYYANNS":
                    getAllAnnounces("MYYANNS");
                    break;
                case "ANNONCE":
                    getAllAnnounces("ANNONCE");
                    break;
                case "DELANNS":
                    deleteAnnounce();
                    break;
                case "ADDANNS":
                    addAnnounce();
                    break;
                case "READ":
                    client_udp.read(username, current_user_udp_port, sc);
                    break;
                case "QUIT":
                    quitApply();
                    break;
                case "MESSAGE":
                	message();
                	break;
                default:
                    System.out.println(ANSI_RED + "Wrong command !" + ANSI_RESET);
            }
            if(!mode_disconnected) System.out.println(ANSI_PURPLE + "You have "+ client_udp.sizeMessage()+" new unseen message(s)" + ANSI_RESET);
        }
    }
    
    private void message() {
        try{
            System.out.print(ANSI_GREEN + ">> Ref announce : " + ANSI_RESET);
            int announce_ref = Integer.parseInt(sc.nextLine());
            send("MESSAGE;"+announce_ref);
            read();
            String []tab = message_received.split(";");
            //if les bon result
            if(tab.length == 1 && tab[0].equals("MESSAGE")) {
                System.out.println(ANSI_RED + "Sorry no annouce found with ref = "+ announce_ref + ANSI_RESET);
            }else {
                if(tab[0].equals("MESSAGE")) {
                    address = new InetSocketAddress("localhost", Integer.parseInt(tab[1]));
                    System.out.print(ANSI_GREEN + ">> Message : " + ANSI_RESET);
                    message_to_send = "WRITETO;"+ username + ";" + current_user_udp_port + ";" + sc.nextLine();

                    client_udp.sendTo(address, message_to_send);
                }else {
                    System.out.println(ANSI_RED + "Error Server bad response !" + ANSI_RESET);
                }
            } 
        }catch(Exception e){
            System.out.println(ANSI_RED + "ERROR EXCEPTION : something went really wrong" + ANSI_RESET);
        }
    }
    
    private void disconnect(){
        try{
            if(!mode_disconnected){
                send(command);
                read();
                if(message_received.equals("OK")) {
                    mode_disconnected = true;
                    System.out.println(ANSI_BLUE + "################" + ANSI_RESET);
                    System.out.println(ANSI_BLUE + "# Disconnected #" + ANSI_RESET);
                    System.out.println(ANSI_BLUE + "################" + ANSI_RESET);
                    System.out.println(ANSI_BLUE + "Now, you can only request public annouces but can't interact with anything else !" + ANSI_RESET);  
                } else {
                    System.out.println(ANSI_RED + "ERROR, Bad server response, couldn't disconnect" + ANSI_RESET);
                    System.exit(1);
                }
            }else{
                System.out.println(ANSI_RED + "You are not logged in to disconnect" + ANSI_RESET);
            }
        }catch(Exception e){
            System.out.println(ANSI_RED + "ERROR EXCEPTION : something went really wrong" + ANSI_RESET);
        }
    }
    
    private void quitApply(){
        try{
            message_to_send = "QUIT";
            send(message_to_send);
            read();
            if(message_received.equals("OK")){
                System.out.println(ANSI_BLUE + "Server connexion destroyed.... see you soon... BYE !" + ANSI_RESET);
                close();
                System.exit(1);
            }else{
                System.out.println(ANSI_RED + "BAD SERVER RESPONSE, destroying everything... BYE" + ANSI_RESET);
                close();
                System.exit(1);
            }
        }catch(Exception e){
            System.out.println(ANSI_RED + "ERROR EXCEPTION : something went really wrong" + ANSI_RESET);
        }
    }
    
    private void connecte() {
        try{
            if(mode_disconnected){
                System.out.println(ANSI_YELLOW + "Please fill the form bellow :" + ANSI_RESET);
                System.out.print(ANSI_GREEN + ">> Login: " + ANSI_RESET);
                username = sc.nextLine();
                //System.out.print(">> Password: ");
                //password = sc.nextLine();
                Console console = System.console();
                password = new String(console.readPassword(ANSI_GREEN + ">> Password: " + ANSI_RESET));

                message_to_send = "CONNECT;" + username + ";" + password;
                send(message_to_send);
                read();
                String[]tab = message_received.split(";");
                if(tab[0].equals("CONNECT")){
                    mode_disconnected = false;
                    client_udp = new ClientUDP();
                    current_user_udp_port = Integer.parseInt(tab[1]);
                    client_udp.bind(current_user_udp_port);
                    client_udp.start();
                    System.out.println(ANSI_BLUE + "############################" + ANSI_RESET);
                    System.out.println(ANSI_BLUE + "# Connection successfull ! #" + ANSI_RESET);
                    System.out.println(ANSI_BLUE + "############################" + ANSI_RESET);
                }else if(message_received.equals("FAIL")){
                    System.out.println(ANSI_RED + "Couldn't connect to the server, try again !" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "SERVER BAD RESPONSE" + ANSI_RESET);
                }
            }else{
                System.out.println(ANSI_RED + "You are alrady logged in, nothing to be done" + ANSI_RESET);
            }
        }catch(Exception e){
            System.out.println(ANSI_RED + "ERROR EXCEPTION : something went really wrong" + ANSI_RESET);
        }
    }
    
    private void getAllAnnounces(String command){
        try{
            if(command.equals("MYYANNS") && mode_disconnected){
                System.out.println(ANSI_RED + "You are disconnected you can't see personnal annouces !" + ANSI_RESET);
            }else{
                message_to_send = command;
                if(command.equals("ANNONCE")){
                    System.out.println(ANSI_YELLOW + "Please fill the form bellow : " + ANSI_RESET);
                    System.out.print(ANSI_YELLOW + ">> Filter (Key word) : " + ANSI_RESET);
                    message_to_send += ";"+sc.nextLine();
                }
                send(message_to_send);
                read();
                String[] res = message_received.split(";");

                if(res.length == 2 && res[0].equals("FAIL")){
                    System.out.println(ANSI_RED + "Error : " + res[1] + ANSI_RESET);
                }else if(res[0].equals(command)){
                    if(res.length == 1 ){
                        System.out.println(ANSI_BLUE + "Nothing published yet, use ADDANNS to be the first to publish an annouce" + ANSI_RESET);
                    }else if(res.length > 1){
                        System.out.println(ANSI_YELLOW + "All announces online :" + ANSI_RESET);
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
                    System.out.println(ANSI_RED + "SERVER BAD RESPONSE" + ANSI_RESET);
                }
            }
        }catch(Exception e){
            System.out.println(ANSI_RED + "ERROR EXCEPTION : something went really wrong" + ANSI_RESET);
        }
    }
    
    private boolean isNumeric(String str) { 
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    
    private void addAnnounce(){
        try{
            if(!mode_disconnected){
                System.out.println(ANSI_YELLOW + "Please fill the form bellow: " + ANSI_RESET);
                System.out.print(ANSI_GREEN + ">> domain : " + ANSI_RESET);
                String domain = sc.nextLine();
                String price = "";
                while(!isNumeric(price)){
                    System.out.print(ANSI_GREEN + ">> price : " + ANSI_RESET);
                    price = sc.nextLine();
                }
                System.out.print(ANSI_GREEN + ">> description : " + ANSI_RESET);
                String description = sc.nextLine();

                message_to_send = "ADDANNS;" + domain + ";" + price + ";" + description;
                send(message_to_send);
                read();
                //System.out.println("DEBUG --- "+message_received);
                if(message_received.equals("OK")){
                    System.out.println(ANSI_BLUE + "Announce added succefully !" + ANSI_RESET);
                }else if(message_received.equals("FAIL")){
                    System.out.println(ANSI_RED + "Failed adding the announce !" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "SERVER BAD RESPONSE" + ANSI_RESET);
                }
            }else{
                System.out.println(ANSI_RED + "You are not logged in to add an announce" + ANSI_RESET);
            }
        }catch(Exception e){
            System.out.println(ANSI_RED + "ERROR EXCEPTION : something went really wrong" + ANSI_RESET);
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
                if(message_received.equals("OK")){
                    System.out.println("Announce deleted succesfully !");
                }else if(message_to_send.equals("FAIL")){
                    System.out.println("Failed deleting the announce");
                }else{
                    System.out.println("SERVER BAD RESPONSE");
                }  
            }else{
                System.out.println("You are not logged in to delete an announce");
            }
        }catch(Exception e){
            System.out.println("ERROR EXCEPTION : something went really wrong");
        }
    }
    

    private void close() {
        pw.close();
        try {
            br.close();
        }catch (IOException e) {
            System.out.println("Echec br.close()");
        }
        try {
            so.close();
        }catch (IOException e) {
            System.out.println("Echec so.close()");
        }
    }

    private void connection() {
        try {
            so = new Socket("localhost", 1027);
        }catch (UnknownHostException e) {
            System.out.println("Erreur host");
        }catch (IOException e) {
            System.out.println("Echec connexion au serveur");
        }
    }

    private void buffered() {
        try {
            br = new BufferedReader(new InputStreamReader(so.getInputStream()));
        } catch (IOException e) {
            System.out.println("Echec création BufferedReader");
        }
        try {
            pw = new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Echec création PrintWriter");
        }
    }

    private void read() {
        try {
            message_received = br.readLine();
        }catch (IOException e) {
            System.out.println("Echec du readLine");
        }
    }

    private void send(String message) {
        pw.println(message);
        pw.flush();
    }

    public static void main(String[] args) throws NumberFormatException, SocketException {
        Client client = new Client();
        System.out.println(ANSI_YELLOW + "####### Welcome to M2 PSI project #######" + ANSI_RESET);
        client.communication();
    }
}
