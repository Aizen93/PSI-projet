public class Users {

    private String pseudo,mdp,ip;
    private int portUDP; 
    private boolean connect;

		
    public Users(String _pseudo, String _mdp,int _portUDP,String _ip) {
        this.pseudo = _pseudo;
        this.mdp = _mdp;
        this.portUDP = _portUDP;
        this.ip = _ip;
        this.connect = true;
    }

	public String getMdp() { 
		return this.mdp; 
    }
    public String getPseudo(){
        return this.pseudo;
    }
    public boolean getConnect(){
        return this.connect;
    }
    public void setConnect(boolean v){
        this.connect = v;
    }
    public int getPortUDP(){
        return this.portUDP;
    }
    public String getIP(){
        return this.ip;
    }
}