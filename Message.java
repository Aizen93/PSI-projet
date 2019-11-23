
public class Message {

	private String pseudo, message;
	
	public Message(String pseudo, String mess) {
		this.pseudo = pseudo;
		this.message = mess;
	}
/*
	public String getPseudo() {
		return pseudo;
	}

	public String getMessage() {
		return message;
	}
*/	
	public void afficher() {
		System.out.println(pseudo+" : "+message);
	}
	
}
