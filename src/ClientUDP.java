import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Scanner;

public class ClientUDP  implements Runnable {
    private DatagramSocket socket;
    private boolean running;
    private LinkedList<Message> messages;

    public void bind(int port) throws SocketException {
        //socket.close();
        socket = new DatagramSocket(port);
    }

    public void start(){
        this.messages = new LinkedList<>();
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop(){
        running = false;
        socket.close();
    }

    @Override
    public void run(){
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        running = true;
        while(running){
            try{
                socket.receive(packet);

                String msg = new String(buffer, 0, packet.getLength());
                String []tab = msg.split(";");
                if(tab.length == 4 && tab[0].equals("WRITETO")) {
                    messages.add(new Message(tab[1], Integer.parseInt(tab[2]), tab[3]));
                }
            } 
            catch (IOException e){
                break;
            }
        }
    }

    public void sendTo(InetSocketAddress address, String msg)  {
        byte[] buffer = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(address);

        try {
            socket.send(packet);
        } catch (IOException e) {
            System.out.println("Error sending message to "+ address);
        }
    }

    public synchronized void  read(String pseudo, int port, Scanner sc) {
        if(messages.size() > 0){
            System.out.println("Mesage received : ");
            Message mess = messages.get(0);
            mess.afficher();
            messages.remove(mess);
            
            String answer;
            while(true){
                System.out.print(">> Would you like to answer ? (y/n): ");
                answer = sc.nextLine();
                if(answer.equals("y") || answer.equals("Y") || answer.equals("yes") || answer.equals("YES")){
                    InetSocketAddress address = new InetSocketAddress("localhost", mess.getPortUDP());
                    System.out.print(">> Message : ");
                    answer = "WRITETO;"+ pseudo + ";" + port + ";" + sc.nextLine();

                    sendTo(address, answer);
                    break;
                }else if(answer.equals("n") || answer.equals("N") || answer.equals("no") || answer.equals("NO")){
                    break;
                }
            }
        }
    }

    public int sizeMessage() {
        return messages.size();
    }
}
