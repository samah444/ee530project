package assembler;

import java.util.HashMap;

public class Simen {

	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	private AL assemblyLine;

	//Overfører bokstavene i formen X'ABC' eller C'ABC' til hex
	public String constantToHex(String constant){
		int decContent = 0;
		char[] byteContent = constant.toCharArray();
		String hex = "";
		for(int i = 2; i<constant.length()-1; i++){
			decContent = Integer.parseInt("" + byteContent[i]);
			hex += intToHex(decContent);
		}
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

