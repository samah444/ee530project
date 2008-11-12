package assembler;

public class Literal {
	
	private int length;
	private String value;
	private String address;
	
	public Literal(String address, String value, int length){
		this.address = address;
		this.value = value;
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
