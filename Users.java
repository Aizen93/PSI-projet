public class Users {

    private String pseudo,mdp;
    private int portUDP; 
    private boolean connect;

		
    public Users(String _pseudo, String _mdp,int _portUDP) {
        this.pseudo = _pseudo;
        this.mdp = _mdp;
        this.portUDP = _portUDP;
        this.connect = true;
    }

	public String getMdp() { 
		return this.mdp; 
    }
    public String getPseudo(){
        return this.pseudo;
    }
    public boolean getConnect(){
        return connect;
    }
    public void setConnect(boolean v){
        this.connect = v;
    }
    public int getPortUDP(){
        return portUDP;
    }
}