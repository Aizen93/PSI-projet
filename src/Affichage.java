import java.net.InetAddress;

public class Affichage {
    
    //reset
    public static final String ANSI_RESET = "\u001B[0m";
    
    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE
    
    // Bold High Intensity
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    
    // High Intensity backgrounds
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    
    private static String OS = System.getProperty("os.name").toLowerCase();
    
    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
        

    public static void display_load_server(String ip_address) {
        System.out.println(GREEN_BOLD_BRIGHT + "--------------Server is now running -----------------------" + ANSI_RESET);
        System.out.println(GREEN_BOLD_BRIGHT +"----------------- IP : " + ip_address+" -----------------------" + ANSI_RESET);
    }
	
    public static void display_error(String error) {
        System.out.println(RED_BOLD_BRIGHT + error + ANSI_RESET);

    }

    public static void display_command_request() {
        System.out.print(BLUE_BOLD_BRIGHT + "> Enter a command: " + ANSI_RESET);
    }
	
    public static void display_message_notification(int n) {
        System.out.println(CYAN_BOLD_BRIGHT + "You have "+ n +" new unseen message(s)" + ANSI_RESET);
    }
	
    public static void display_connected() {
        System.out.println(GREEN_BOLD_BRIGHT + "############################" + ANSI_RESET);
        System.out.println(GREEN_BOLD_BRIGHT + "# Connection successfull ! #" + ANSI_RESET);
        System.out.println(GREEN_BOLD_BRIGHT + "############################" + ANSI_RESET);
    }
	
    public static void display_disconnected() {
        System.out.println(GREEN_BOLD_BRIGHT + "################" + ANSI_RESET);
        System.out.println(GREEN_BOLD_BRIGHT + "# Disconnected #" + ANSI_RESET);
        System.out.println(GREEN_BOLD_BRIGHT + "################" + ANSI_RESET);
        System.out.println(YELLOW_BOLD_BRIGHT + "Now, you can only request public annouces but can't interact with anything else !" + ANSI_RESET);
    }
	
    public static void display_annonce(String ref, String domain, String price, String pseudo, String desc) {
        System.out.println("+----------------------------------------------+");
        System.out.println("|" + GREEN_BOLD_BRIGHT + " Reference : " + ANSI_RESET + ref);
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
        System.out.println("+-----------------------------------------------+");
    }

    public static void display_exception(String mess) {
        System.out.println(RED_BACKGROUND_BRIGHT + mess + ANSI_RESET);
    }

    public static void display_read_server(String mess) {
        System.out.println(WHITE_BRIGHT + "-------------------------------------------" + ANSI_RESET);
        System.out.println(WHITE_BRIGHT + "----> Message receive : "+mess + ANSI_RESET);
        System.out.println(WHITE_BRIGHT + "-------------------------------------------" + ANSI_RESET);
    }

    public static void display_send_server(String mess) {
        System.out.println(GREEN_BRIGHT + "-------------------------------------------" + ANSI_RESET);
        System.out.println(GREEN_BRIGHT + "----> Message send : "+ mess + ANSI_RESET);
        System.out.println(GREEN_BRIGHT + "-------------------------------------------" + ANSI_RESET);
    }

    public static void display_client_connect(int status, String pseudo, String mdp) {
        if(pseudo == null) {
            System.out.println(YELLOW_BOLD_BRIGHT + "--------------New guest -----------------------" + ANSI_RESET);
        }else{
            System.out.println(YELLOW_BOLD_BRIGHT + "-------------------------------------------" + ANSI_RESET);

            if(status == 0) System.out.println(YELLOW_BOLD_BRIGHT + "            ----> New User created" + ANSI_RESET);
            else System.out.println(YELLOW_BOLD_BRIGHT + "            ----> User connected" + ANSI_RESET);

            System.out.println("                       pseudo : "+pseudo+", mdp : "+mdp);
            System.out.println(YELLOW_BOLD_BRIGHT + "------------------------------------------- " + ANSI_RESET);
        }
    }
}
