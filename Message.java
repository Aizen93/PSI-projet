public class Message {
	private String pseudo;
	private int portUDP;
	private String message;
	
	public Message(String pseudo, int portUDP, String message) {
		this.pseudo = pseudo;
		this.portUDP = portUDP;
		this.message = message;
	}
	
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public int getPortUDP() {
		return portUDP;
	}
	public void setPortUDP(int portUDP) {
		this.portUDP = portUDP;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
