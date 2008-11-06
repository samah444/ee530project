package assembler;
/*
* @author Andreas Urke
* @author Magnus Lervag
* @since   05.11.2008
* @version 05.11.2008
* 
* Symbol contains all the info about a symbol as defined by the programmer in
* the source code. It also provides getters and setter for the info.
*/

//TODO:what the hell is type and length for. har vi bruk for dei
//TODO:slette noen av setterane når vi ser vi ikke får bruk for dei
//TODO:enums for flag og type?

public class Symbol {
	private int length;
	private int flag;
	private String type;
	private String address;
	
	public Symbol(String address, String type, int flag, int length){
		this.address = address;
		this.type = type;
		this.flag = flag;
		this.length = length;
	}
	
	//Getters and setters
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
