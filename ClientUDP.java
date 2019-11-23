import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ClientUDP  implements Runnable {
	private DatagramSocket socket;
	private boolean running;
	private LinkedList<Message> messages;
	
	public void bind(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}
	
	public void start(){
		this.messages = new LinkedList<>();
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop()
	{
		running = false;
		socket.close();
	}

	@Override
	public void run()
	{
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		running = true;
		while(running)
		{
			try
			{
				socket.receive(packet);
				
				String msg = new String(buffer, 0, packet.getLength());
				String []tab = msg.split(";");
				if(tab[0].equals("WRITETO")) {
					messages.add(new Message(tab[1], tab[2]));
				}
			} 
			catch (IOException e)
			{
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
	
	public synchronized void  read() {
		for(Message mess : messages) {
			mess.afficher();
			messages.remove(mess);
		}
	}
	
	public int sizeMessage() {
		return messages.size();
	}
}
