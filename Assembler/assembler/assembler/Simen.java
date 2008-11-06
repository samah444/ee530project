package assembler;

import java.util.HashMap;

public class Simen {
	
	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	private AL assemblyLine;
	
	public void fillSymTabWithRegisters(){
		//slide 8 på sp2-2
		symTab.put(A, sym);
//		
//		if (A) 
//			Symbol sym = new Symbol(0, "", "", "");
//		symTab.put(A,sym);
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

