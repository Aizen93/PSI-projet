public class User{

	private String pseudo;
	private String mdp;
	private boolean connect;
	private int port_udp;
  
	public User(String pseudo, String mdp, int port_udp) {
		this.pseudo = pseudo;
		this.mdp = mdp;
		this.port_udp = port_udp;
		setConnect(true);
	}

	public String getPseudo() {
		return pseudo;
	}

	public String getMdp() {
		return mdp;
	}

	public boolean isConnect() {
		return connect;
	}
	
	public int getPortUDP() {
		return port_udp;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}
}