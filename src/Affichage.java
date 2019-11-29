import java.net.InetAddress;

public class Affichage {

	public static void display_load_server(String ip_address) {
		System.out.println(Color.GREEN_BOLD_BRIGHT + "--------------Server is now running -----------------------" + Color.ANSI_RESET);
        System.out.println(Color.GREEN_BOLD_BRIGHT +"----------------- IP : " + ip_address+" -----------------------" + Color.ANSI_RESET);
	}
	
	public static void display_error(String error) {
		System.out.println(Color.RED_BRIGHT + error + Color.ANSI_RESET);

	}

	public static void display_command_request() {
        System.out.print(Color.BLUE_BRIGHT + "> Enter a command: " + Color.ANSI_RESET);
	}
	
	public static void display_message_notification(int n) {
		System.out.println(Color.CYAN_BRIGHT + "You have "+ n +" new unseen message(s)" + Color.ANSI_RESET);
	}
	
	public static void display_connected() {
        System.out.println(Color.GREEN_BRIGHT + "############################" + Color.ANSI_RESET);
        System.out.println(Color.GREEN_BRIGHT + "# Connection successfull ! #" + Color.ANSI_RESET);
        System.out.println(Color.GREEN_BRIGHT + "############################" + Color.ANSI_RESET);
	}
	
	public static void display_disconnected() {
        System.out.println(Color.GREEN_BRIGHT + "################" + Color.ANSI_RESET);
        System.out.println(Color.GREEN_BRIGHT + "# Disconnected #" + Color.ANSI_RESET);
        System.out.println(Color.GREEN_BRIGHT + "################" + Color.ANSI_RESET);
        System.out.println(Color.YELLOW_BRIGHT + "Now, you can only request public annouces but can't interact with anything else !" + Color.ANSI_RESET);
	}
	
	public static void display_annonce(String ref, String domain, String price, String pseudo, String desc) {
        System.out.println("+----------------------------------------------+");
        System.out.println("|" + Color.GREEN_BOLD_BRIGHT + " Reference : " + Color.ANSI_RESET + ref);
        System.out.println("| Domain : " + domain);
        System.out.println("| Price: " + price);
        System.out.println("| Owner: " + pseudo);
        System.out.println("| Description : " + desc);
        System.out.println("+----------------------------------------------+");
    }
	
    public static void printCommands(){
        System.out.println("+-----------------------------------------------+");
        System.out.println("| You can use the following commands :");
        System.out.println("| - CONNECT : to log in");
        System.out.println("| - DISCONN : to disconnect (offline mode");
        System.out.println("| - ADDANNS : to add an anounce");
        System.out.println("| - ALLANNS : to see all anounces");
    }

    public static void display_read_server(String mess) {
        System.out.println(Color.WHITE_BRIGHT + "-------------------------------------------" + Color.ANSI_RESET);
        System.out.println(Color.WHITE_BRIGHT + "----> Message receive : "+mess + Color.ANSI_RESET);
        System.out.println(Color.WHITE_BRIGHT + "-------------------------------------------" + Color.ANSI_RESET);
    }

    public static void display_send_server(String mess) {
        System.out.println(Color.GREEN_BRIGHT + "-------------------------------------------" + Color.ANSI_RESET);
        System.out.println(Color.GREEN_BRIGHT + "----> Message send : "+ mess + Color.ANSI_RESET);
        System.out.println(Color.GREEN_BRIGHT + "-------------------------------------------" + Color.ANSI_RESET);
    }

    public static void display_client_connect(int status, String pseudo, String mdp) {
	    if(pseudo == null) {
            System.out.println(Color.YELLOW_BRIGHT + "--------------New guest -----------------------" + Color.ANSI_RESET);
        }else{
            System.out.println(Color.YELLOW_BRIGHT + "-------------------------------------------" + Color.ANSI_RESET);

            if(status == 0) System.out.println(Color.YELLOW_BRIGHT + "            ----> New User created" + Color.ANSI_RESET);
            else System.out.println(Color.YELLOW_BRIGHT + "            ----> User connected" + Color.ANSI_RESET);

            System.out.println("                       pseudo : "+pseudo+", mdp : "+mdp);
            System.out.println(Color.YELLOW_BRIGHT + "------------------------------------------- + Color.ANSI_RESET");
        }
    }
}
