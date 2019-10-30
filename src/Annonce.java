package src;


public class Annonce {
	
	private int ref,prix;
	private String login, type, description;
	private boolean dispo;
	
	public Annonce(int ref, String login, int prix, String type, String description) {
		this.ref = ref;
		this.login = login;
		this.setType(type);
		this.setDescription(description);
		dispo = true;
	}

	public int getRef() {
		return ref;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogin() {
		return login;
	}

	public boolean isDispo() {
		return dispo;
	}

	public void setDispo(boolean dispo) {
		this.dispo = dispo;
	}

	public int getPrix() {
		return prix;
	}
	
	
	

}
