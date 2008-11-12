package assembler;

public class Literal {
	
	private int length;
	private int value;
	private String address;
	
	public Literal(String address, int value, int length){
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
