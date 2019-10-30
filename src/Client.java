import java.net.*;
import java.io.*;
import java.util.*;

class Client {

    private Socket so;
    private BufferedReader br;
    private PrintWriter pw;
    private Scanner sc;
    private String message_received, send;
    private boolean exit;
    private boolean mode_disconnected;
    private String command = "";
    private String message_to_send = "";

    public Client() {
        exit = false;
        mode_disconnected = true;
        connection();
        buffered();
        sc = new Scanner(System.in);
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
    

    public void communication() {
        //read();
        while(!exit) {
            System.out.print("> Enter a command: ");
            command = sc.nextLine();
            switch (command) {
                case "DISCONNECT":
                    disconnect();
                    break;
                case "CONNECT":
                    connecte();
                    break;
                case "ANNS":
                    getAllAnnounces();
                    break;
                case "MYANNS":
                    break;
                case "ANN":
                    break;
                case "DELETE":
                    break;
                case "ADD":
                    break;
                case "SEND":
                    //TO DO FOR NEXT VERSION
                    break;
                default:
                    System.out.println("Wrong command !");
                    break;
            }
        }
    }
    
    private void disconnect(){
        if(!mode_disconnected){
            send(command);
            read();
            if(message_received.equals("OK")) {
                close();
                mode_disconnected = true;
                System.out.println("################");
                System.out.println("# Disconnected #");
                System.out.println("################");
                System.out.println("Now, you can only request public annouces but can't interact with anything else !");  
            } else {
                System.out.println("ERROR, Bad server response, couldn't disconnect");
                close();
                System.exit(1);
            }
        }else{
            System.out.println("You are not logged in to disconnect");
        }
    }
    
    private void connecte(){
        String username = "";
        String password = "";
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
            if(message_received.equals("OK")){
                mode_disconnected = false;
                System.out.println("############################");
                System.out.println("# Connection successfull ! #");
                System.out.println("############################");
            }else if(message_received.equals("FAIL")){
                System.out.println("Couldn't connect to the server, try again !");
            }
        }else{
            System.out.println("You are alrady logged in, nothing to be done");
        }
    }
    
    private void getAllAnnounces(){
        message_to_send = "ANNS";
        send(message_to_send);
        read();
        String[] res = message_received.split(";");
        if(res[0].equals("FAIL")){
            System.out.println("Error receiving ANNS - cause : "+res[1]);
        }else if(res[0].equals("MYANNS")){
            if(res[1].length() == 0){
                System.out.println("Nothing published yet, use ADD to be the first to publish an annouce");
            }else{
                System.out.println("All announces online :");
                String[] tmp = res[1].split("***");
                for(int i = 0; i < tmp.length; i++){
                    System.out.println("+----------------------------------------------+");
                    System.out.println("| Reference : " + tmp[2]);
                    System.out.println("| Domain : " + tmp[0]);
                    System.out.println("| Price: " + tmp[3]);
                    System.out.println("| Owner: " + tmp[4]);
                    System.out.println("| Description : " + tmp[1]);
                    System.out.println("+----------------------------------------------+");
                }
            }
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
            br=new BufferedReader(new InputStreamReader(so.getInputStream()));
        } catch (IOException e) {
            System.out.println("Echec création BufferedReader");
        }
        try {
            pw=new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Echec création PrintWriter");
        }
    }

    private void read() {
        try {
            message_received = br.readLine();
            //System.out.println(mess);
        }catch (IOException e) {
            System.out.println("Echec du readLine");
        }
    }

    private void send(String message) {
        pw.println(message);
        pw.flush();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.communication();
    }
}
