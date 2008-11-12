/**
* @author Simen Hammerseth
* @author Magnus Lervaag
* @author Andreas Urke
* @version 11.12.2008
* @see TwoPass
* 
* Symbol contains all the info about a symbol as defined by the programmer in
* the source code. It also provides getters and setter for the info.
*/
package assembler;

public class Symbol {
	
	private int length;
	private int flag;
	private String type;
	private String address;
	
	/**
	 * Instantiates a new symbol.
	 * 
	 * @param address the address
	 * @param type the type
	 * @param flag the flag
	 * @param length the length
	 */
	public Symbol(String address, String type, int flag, int length){
		this.address = address;
		this.type = type;
		this.flag = flag;
		this.length = length;
	}
	
	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Sets the length.
	 * 
	 * @param length the new length
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * Gets the flag.
	 * 
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}
	
	/**
	 * Sets the flag.
	 * 
	 * @param flag the new flag
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Sets the address.
	 * 
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
}
