package assembler;

import java.util.HashMap;

public class Simen {
	
	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	private AL assemblyLine;
	
	
	public String constantToHex(String constant){
		//constant har form: X'ABC' eller C'ABC'; 
		int decimalRepresentasjonAvBokstaven = Integer.parseInt(constant-skrell);
		//i er da en decimal. putt desimalen inn i magnus sin og vips du har en hex.
		String hex = intToHex(decimalRepresentasjonAvBokstaven);
		return hex;
	}
	
	public static String intToHex(int inputFromUser){
//		Takes a decimal int from user and converts it to hex and returns string
		
		int i = inputFromUser;
	    String s = Integer.toHexString(i);
		return s;
	}
	
	public void fillSymTabWithRegisters(){
		String[] regName = {"A", "X", "L", "B", "S", "T", "F", "PC", "SW"};
		String[] value = {"0", "1", "2", "3", "4", "5", "6", "8", "9"}; 
		for(int i = 0; i<regName.length; i++){
			Symbol sym = new Symbol(value[i], "", 0, 0);
			symTab.put(regName[i], sym);
		}
	}
	

	public int findNumberOfBytesInConstant(String constant){
		int LengthOfByte;
		char[] byteContent = constant.toCharArray();
		//if(constant.equals("BYTE")){

		if(byteContent[0]== 'X'){
			LengthOfByte = ((constant.length()-3)/2);
		}
		else LengthOfByte = (constant.length()-3);
		return LengthOfByte;
	}
	
	
}

