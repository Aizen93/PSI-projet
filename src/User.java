public class User{

    public void setPort_udp(int port_udp) {
        this.port_udp = port_udp;
    }

    public String getIp() {
        return ip;
    }

    public int getPort_udp() {
        return port_udp;
    }

	private String pseudo;
	private String mdp;
        private String ip;
	private boolean connect;
	private int port_udp;
  
	public User(String pseudo, String mdp, int port_udp, String ip) {
		this.pseudo = pseudo;
		this.mdp = mdp;
		this.port_udp = port_udp;
                this.ip = ip;
		setConnect(true);
	}

	public String getPseudo() {
		return pseudo;
	}

	public String getMdp() {
		return mdp;
	}
        
        public void setIp(String address) {
		this.ip = address;
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