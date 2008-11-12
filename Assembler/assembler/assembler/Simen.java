package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Simen {

	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	//private AL assemblyLine;
	private BufferedWriter outRecord;
	private int lengthOfTextRec;
	private String objectCodeString;
	private AL interMediateAssemblyLine;
	private HashMap<String, Literal> litTab = new HashMap<String, Literal>();

	//literalobject . getvalue
	//g¨r gjennom littab, kaller writeObjectcode
	//input, value in littab
	//insertLiterals
	
	public void insertLiterals(){
		while(true){

		}
	}

	public String stripToValue(String string){
		return string.substring(string.indexOf("'")+1).substring(0, string.indexOf("'"));	
	}

	public void writeReferRec(){
		printToRecord("\nR");
		int length = 0;
		String operand = interMediateAssemblyLine.getOperand1();
		while(true){
			int index = operand.indexOf(',');
			String symbol = operand.substring(0, index);
			while(symbol.length()<6) symbol += " ";
			printToRecord(symbol);
			length++;
			if(length==12) {
				printToRecord("\nR");
				length = 0;
			}			
			operand = operand.substring(index+1);
			if(index==-1) break;
		}
	}

	public void writeDefineRec(AL interMediateAssemblyLine){

		printToRecord("\nD");
		int length = 0;
		String operand = interMediateAssemblyLine.getOperand1();
		while(true){
			int index = operand.indexOf(',');
			String symbol = operand.substring(0, index);
			String address = symTab.get(symbol).getAddress();
			while(address.length()<6) address = "0" + address;
			while(symbol.length()<6) symbol += " ";
			printToRecord(symbol + address);
			length++;

			if(length==6){ 
				printToRecord("\nD");
				length = 0;
			}

			operand = operand.substring(index+1);
			if(index==-1) break;
		}
	}

	public void fixLengthInTextRecord(){
		char[] objectCodeArray = objectCodeString.toCharArray();
		String hex = intToHex(lengthOfTextRec-9);
		if(hex.length()==1){
			objectCodeArray[8]='0';
			objectCodeArray[9]=(hex.toCharArray())[0];
		}
		else
			objectCodeArray[8]=(hex.toCharArray())[0];
		objectCodeArray[9]=(hex.toCharArray())[1];	
	}


	//Will objecCode fit into current TextRecord? True if Yes, False otherwise.
	public boolean fitIntoTextRec(String objectCode){
		if((objectCode.length()+lengthOfTextRec)<70)
			return false;
		else
			return true;
	}

	public void printToRecord(String objectCode){
		//		Takes an objectcode string and prints it as a new line in the RecordFile
		try {
			if(outRecord == null) 
				outRecord = new BufferedWriter(new FileWriter("Overview", true));
			outRecord.write(objectCode);
		} catch (IOException e) {
		}
	}

	//Overfører bokstavene i formen X'ABC' eller C'ABC' til hex
	public String constantToHex(String constant){
		int decContent = 0;
		String content = "";
		char[] byteContent = constant.toCharArray();
		String hex = "";
		for(int i = 2; i<constant.length()-1; i++){
			content += byteContent[i];
			decContent = Integer.parseInt("" + content);
			hex += intToHex(decContent);
		}
		if(byteContent[0]=='C') return hex;
		else return content;
	}

	public static String intToHex(int inputFromUser){
		//		Takes a decimal int from user and converts it to hex and returns string

		int i = inputFromUser;
		String s = Integer.toHexString(i);
		return s;
	}
	//Fills the SymTab with the register addresses.
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

