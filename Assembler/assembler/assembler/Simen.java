package assembler;

import java.util.HashMap;

public class Simen {
	
	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	private AL assemblyLine;
	
	private String[] regName = {"A", "X", "L", "B", "S", "T", "F", "PC", "SW"};
	private String[] value = {"0", "1", "2", "3", "4", "5", "6", "8", "9"}; 
	
	public void fillSymTabWithRegisters(){
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

