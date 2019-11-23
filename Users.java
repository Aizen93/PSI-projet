import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class Users {

    public String psdo;
    private String mdp; 
    private boolean connet;
		
    public Users(String _psdo, String _mdp) {
        this.psdo = _psdo;
        this.mdp = _mdp;
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
}