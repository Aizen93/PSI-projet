
public class Annonce {
	
	private int id;
	private String login, type, description;
	private boolean dispo;
	
	public Annonce(int id, String login, String type, String description) {
		this.id = id;
		this.login = login;
		this.setType(type);
		this.setDescription(description);
	}

	public int getId() {
		return id;
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
	
	
	

}
