package src;

import java.net.*;
import java.io.*;

public class User{

	private String pseudo;
	private String mdp;
	private boolean connect;
  
	public User(String pseudo, String mdp) {
		this.pseudo = pseudo;
		this.mdp = mdp;
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

	public void setConnect(boolean connect) {
		this.connect = connect;
	}
}