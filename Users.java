import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class Users {

    private String psdo,mdp;
    private int portUDP; 
    private boolean connet;

		
    public Users(String _psdo, String _mdp,int _portUDP) {
        this.psdo = _psdo;
        this.mdp = _mdp;
        this.portUDP = _portUDP;
        this.connet = true;
    }

	public String getMdp() { 
		return this.mdp; 
    }
    public String getPseudo(){
        return this.psdo;
    }
    public boolean getConnect(){
        return connet;
    }
    public void setConnecte(boolean v){
        this.connet = v;
    }
    public int getPortUDP(){
        return portUDP;
    }
}