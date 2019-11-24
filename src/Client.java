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
            System.out.print("> Enter a command: ");
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
                    System.out.println("Wrong command !");
            }
            if(!mode_disconnected) System.out.println("You have "+ client_udp.sizeMessage()+" new unseen message(s)");
        }
    }
    
    private void message() {
        System.out.print(">> Ref announce : ");
        int announce_ref = Integer.parseInt(sc.nextLine());
    	send("MESSAGE;"+announce_ref);
    	read();
    	String []tab = message_received.split(";");
    	//if les bon result
    	if(tab.length == 1 && tab[0].equals("MESSAGE")) {
            System.out.println("Sorry no annouce found with ref = "+ announce_ref);
    	}else {
            if(tab[0].equals("MESSAGE")) {
                address = new InetSocketAddress("localhost", Integer.parseInt(tab[1]));
                System.out.print(">> Message : ");
                message_to_send = "WRITETO;"+ username + ";" + current_user_udp_port + ";" + sc.nextLine();

                client_udp.sendTo(address, message_to_send);
            }else {
                System.out.println("Error Server bad response !");
            }
    	}  	
    }
    
    private void disconnect(){
        if(!mode_disconnected){
            send(command);
            read();
            if(message_received.equals("OK")) {
                mode_disconnected = true;
                System.out.println("################");
                System.out.println("# Disconnected #");
                System.out.println("################");
                System.out.println("Now, you can only request public annouces but can't interact with anything else !");  
            } else {
                System.out.println("ERROR, Bad server response, couldn't disconnect");
                System.exit(1);
            }
        }else{
            System.out.println("You are not logged in to disconnect");
        }
    }
    
    private void quitApply(){
        message_to_send = "QUIT";
        send(message_to_send);
        read();
        if(message_received.equals("OK")){
            System.out.println("Server connexion destroyed.... see you soon... BYE !");
            close();
            System.exit(1);
        }else{
            System.out.println("BAD SERVER RESPONSE, destroying everything... BYE");
            close();
            System.exit(1);
        }
    }
    
    private void connecte() throws NumberFormatException, SocketException{
        //int port_udp;
        if(mode_disconnected){
            System.out.println("Please fill the form bellow :");
            System.out.print(">> Login: ");
            username = sc.nextLine();
            //System.out.print(">> Password: ");
            //password = sc.nextLine();
            Console console = System.console();
            password = new String(console.readPassword(">> Password: "));

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
                System.out.println("############################");
                System.out.println("# Connection successfull ! #");
                System.out.println("############################");
            }else if(message_received.equals("FAIL")){
                System.out.println("Couldn't connect to the server, try again !");
            }else{
                System.out.println("SERVER BAD RESPONSE");
            }
        }else{
            System.out.println("You are alrady logged in, nothing to be done");
        }
    }
    
    private void getAllAnnounces(String command){
        message_to_send = command;
        if(command.equals("ANNONCE")){
            System.out.println("Please fill the form bellow : ");
            System.out.print(">> Filter (Key word) : ");
            message_to_send += ";"+sc.nextLine();
        }
        send(message_to_send);
        read();
        String[] res = message_received.split(";");

        if(res[0].equals("FAIL")){
            System.out.println("Error receiving " + command );
        }else if(res[0].equals(command)){
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
    }
    
    private boolean isNumeric(String str) { 
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    
    private void addAnnounce(){
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
            //System.out.println("DEBUG --- "+message_received);
            if(message_received.equals("OK")){
                System.out.println("Announce added succefully !");
            }else if(message_received.equals("FAIL")){
                System.out.println("Failed adding the announce !");
            }else{
                System.out.println("SERVER BAD RESPONSE");
            }
        }else{
            System.out.println("You are not logged in to add an announce");
        }
    }
    
    private void deleteAnnounce(){
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
        client.communication();
    }
}
