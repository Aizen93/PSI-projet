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
        String command = "";
        String message_to_send = "";
        String username = "";
        String password = "";
        while(!exit) {
            System.out.print("> Enter a command: ");
            command = sc.nextLine();
            switch (command) {
                case "DISCONNECT":
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
                    break;
                case "CONNECT":
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
                    break;
                default:
                    System.out.println("Wrong command !");
                    break;
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
