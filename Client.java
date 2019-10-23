import java.net.*;
import java.io.*;
import java.util.*;

class main {

  public static void main(String[] args) {
    try{

        Socket so = new Socket("localhost", 4242);

        BufferedReader br=new BufferedReader(new InputStreamReader(so.getInputStream()));
        PrintWriter pw=new PrintWriter(new OutputStreamWriter(so.getOutputStream()));

        Scanner sc = new Scanner(System.in);


        String mess = br.readLine();
        System.out.println(mess);
        mess = br.readLine();
        System.out.println(mess);

        String send;

    /*    while(! ((send = sc.nextLine()).equals("quit"))) {
        	if(send.equals("menu")) {
        		mess = br.readLine();
        		String []tab = mess.split("&");
        		for(String s : tab) System.out.println(s);
        	}
        	pw.println(send);
        	pw.flush();
        }*/
        do {
        	System.out.print("> ");
        	send = sc.nextLine();
            pw.println(send);
            pw.flush();
            switch(send) {
            case "MENU" :
            	mess = br.readLine();
        		String []tab = mess.split("&");
        		for(String s : tab) System.out.println(s);
        		break;
            case "CONNECT" :
            	System.out.print("Login : ");
            	System.out.println("Password : ");
            default :
            	System.out.println("Cette commande n'existe pas");
            }
        }while(! send.equals("DISCONNECT"));

        mess = br.readLine();
        System.out.println(mess);
        pw.close();
        br.close();
        so.close();

    }
    catch (Exception e) {
          e.printStackTrace();
    }
  }
}
