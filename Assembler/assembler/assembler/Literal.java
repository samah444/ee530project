/*
 * @author Simen Hammerseth
 * @author Magnus Lervaag
 * @author Andreas Urke
 * @version 11.12.2008
 * @see TwoPass
 */
package assembler;

// TODO: Auto-generated Javadoc
/**
 * The Class Literal.
 */
public class Literal {
	private int length;
	private String value;
	private String address;
	
	/**
	 * Instantiates a new literal.
	 * 
	 * @param address the address
	 * @param value the value
	 * @param length the length
	 */
	public Literal(String address, String value, int length){
		this.address = address;
		this.value = value;
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
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
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
