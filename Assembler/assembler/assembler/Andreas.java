/**
 * 
 */
package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.IllegalFormatException;

/**
 * @author Andreas
 *
 */
public class Andreas {
	private AL assemblyLine;
	private String locctr = "000000";
	private int lineNumber = 0;
	private BufferedWriter outOverview;

	private int lengthOfTextRec = 0;
	private String startAddress;
	private String programLength;
	private ALStream alstr;
	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();

	public static void main(String[] args) {



	}

	//	Assemble instructions (generate opcode and look up addresses)
	//	 Generate data values defined by BYTE, WORD
	//	 Perform processing of assembler directives not done during Pass 1
	//	 Write the object program and the assembly listing
	public void secondPass(){
		String operand1;
		String operand2;
		String objectCode = "";
		while(!alstr.atEnd()){
			assemblyLine = alstr.nextAL();
			//IF OPCODE IS NOT END
			if(!assemblyLine.getOpmnemonic().equals("END")){
				operand1 = assemblyLine.getOperand1();
				operand2 = assemblyLine.getOperand2();
				if(assemblyLine.getOpmnemonic().equals("START")){
					printHeaderRecord();
				}
				//IF COMMENT
				if(assemblyLine.isFullComment()){}
				//IF NOT COMMENT
				else{
					//IF BYTE OR WORD
					if(assemblyLine.getOpmnemonic().equals("BYTE")
							|| (assemblyLine.getOpmnemonic().equals("WORD"))){
						objectCode = constantToHex(assemblyLine.getOperand1());
					}
					//IF NOT BYTE OR WORD
					else{
						if(isSymbol(operand1))operand1 = findSymbolAddress(operand1);
						if(isSymbol(operand2))operand2 = findSymbolAddress(operand2);
						//TODO:kall magnus-metoden
						//objectCode = assembleObjectCode();
						
					}
					if(lengthOfTextRec == 0)initializeTextRecord();
					if((fitIntoTextRec(objectCode)) 
							&& (!assemblyLine.getOpmnemonic().equals("RESW"))
							&& (!assemblyLine.getOpmnemonic().equals("RESB")))
							printObjectCodeToRecord(objectCode);
					//IF OBJECTCODE DOESNT FIT INTO CURRENT TEXTRECORD OR OPERATOR IS RESW OR RESB
					else {
						fixLengthInTextRecord();
						initializeTextRecord();
						printObjectCodeToRecord(objectCode);
					}
				}
			}
			//IF OPCODE = END
			else printEndRecord();
			printToOverviewFile();
		}
	}
	
	//Prints the given objectCode to record and increases the lengthOfTextRec.
	public void printObjectCodeToRecord(String objectCode){
		printToRecord(objectCode);
		lengthOfTextRec += objectCode.length();
	}
	
	//Will objecCode fit into current TextRecord? True if Yes, False otherwise.
	public boolean fitIntoTextRec(String objectCode){
		if((objectCode.length()+lengthOfTextRec)<70)
			return false;
		else
			return true;
	}
	
	//Initializes a Text Record and writes it to file.
	public void initializeTextRecord(){
		lengthOfTextRec = 0;
		correctLOCCTRformat();
		printToRecord("\nT" +  locctr + "00");
		lengthOfTextRec += 9;
	}

	public void correctLOCCTRformat(){
		while(locctr.length() < 6)locctr = "0" + locctr;
	}

	//Creates the end record and returns it as a string.
	public void printEndRecord(){
		printToRecord("E" + startAddress);
	}

	//Returns true if operand is a Symbol, false otherwise.
	public boolean isSymbol(String operand){
		if(!operand.equals("")){
			if((operand.matches("[a-zA-Z]*"))&&
					(!operand.matches(".'"))){
				return true;
			}
			else return false;
		}
		return false;
	}

	public String findSymbolAddress(String operand){
		Symbol aSymbol = symTab.get(operand);
		if (aSymbol == null){return "";}//TODO: Throw undefined Symbol exception. Set error flag?
		else return aSymbol.getAddress();	
	}

	

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

	//Creates the header record and returns it as a string.
	public void printHeaderRecord(){
		String programName = assemblyLine.getLabel();
		boolean shortened = false;
		if (programName.equals(""))programName = "PROG  ";
		while(programName.length() > 6){
			shortened = true;
			programName = programName.substring(0, programName.length()-1);
		}
		while(programName.length() < 7) programName += " ";
		if(shortened){
			//TODO: kanskje annen behandling av dette
			System.out.println("Program name too long, has been cut to: "
					+ programName);
		}
		printToRecord("H" + programName + startAddress + programLength );
	}

	public static void printToRecord(String objectCode){
		//		Takes an objectcode string and prints it as a new line in the RecordFile
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("RecordFile", true));
			out.write(objectCode);
			out.close();
		} catch (IOException e) {
		}

	}

	//TODO: outOverview.close på slutten av Assembleren.
	//Prints a line to the Overview text file.
	public void printToOverviewFile(){
		try {
			//OPEN FILE
			if(outOverview == null) 
				outOverview = new BufferedWriter(new FileWriter("Overview", true));
			//LINE NUMBER
			outOverview.write(lineNumber + "\t");
			lineNumber++;

			//IF COMMENT
			if(assemblyLine.isFullComment())
				//TODO: Skaffe CommentText. Den er Private i AL. Mail til lewis?
				outOverview.write("CommentText");
			//IF NOT COMMENT
			else{
				//LOCCTR
				outOverview.write(locctr + "\t");
				//LABEL
				outOverview.write(assemblyLine.getLabel() + "\t");
				//IF EXTENDED
				if(assemblyLine.isExtended())outOverview.write("+");
				//IF LITERAL
				if(assemblyLine.isLiteral()){
					outOverview.write("=");
					outOverview.write(assemblyLine.getOperand1());
					outOverview.write("\t\t");
				}
				//IF NOT LITERAL
				else {
					//OPMNEMONIC
					outOverview.write(assemblyLine.getOpmnemonic() + "\t");
					//IF ADDRESSING
					if(assemblyLine.isIndirect())outOverview.write("@");
					else if(assemblyLine.isImmediate())outOverview.write("#");
					//IF DIRECTIVE
					if(assemblyLine.isDirective()){
						//DIRECTIVE OPERANDS
						if (assemblyLine.getOperand2().equals(""))
							outOverview.write(assemblyLine.getOperand1());
						else outOverview.write(assemblyLine.getOperand2());
					}
					//IF NOT DIRECTIVE
					else {
						//OPERANDS
						switch(assemblyLine.getOperandType()){
						case 0:
							break;
						case 1:
							outOverview.write(assemblyLine.getOperand1());
							break;
						case 2:
							outOverview.write(assemblyLine.getOperand1() + "," +
									assemblyLine.getOperand2());
							break;
						}
					}
					//INDEX
					if(assemblyLine.isIndexed())outOverview.write(",X" + "\t");
					else outOverview.write("\t");
				}
				//OBJECTCODE
				outOverview.write(assemblyLine.getAssembledObjectCode());
			}
			outOverview.write("\n");

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error printing to Overview text file.");
		}

	}

	//Sets the LOCCTR to its correct position.
	public void correctLOCCTR(){
		String opmnemonic = assemblyLine.getOpmnemonic();
		if(opmnemonic.equals("WORD"))locctr = hexMath(locctr, '+', "3");
		else if(opmnemonic.equals("RESW")){
			int change = (3 * Integer.parseInt(assemblyLine.getOperand1()));
			locctr = hexMath(locctr, '+', intToHex(change));
		}
		else if(opmnemonic.equals("RESB")){	
			int change = (Integer.parseInt(assemblyLine.getOperand1()));
			locctr = hexMath(locctr, '+', intToHex(change));
		}
		else if(opmnemonic.equals("BYTE")){
			int change = findNumberOfBytesInConstant(assemblyLine.getOperand1());
			locctr = hexMath(locctr, '+', intToHex(change));
		}
		else {
			OpCode tempOpCode = new OpCode(opmnemonic);
			locctr = hexMath(locctr, '+', intToHex(tempOpCode.getFormat()));
		}
	}

	public String hexMath(String hex1, char operator, String hex2){
		if (operator=='+'){
			int i1= Integer.parseInt(hex1,16);
			int i2= Integer.parseInt(hex2,16);
			hex1 = Integer.toHexString(i1+i2);
		}
		if(operator=='-'){
			int i1= Integer.parseInt(hex1,16);
			int i2= Integer.parseInt(hex2,16);
			hex1 = Integer.toHexString(i1-i2);
		}
		//		else throw IllegalOperatorExeption;
		return hex1;	
	}

	//Takes a decimal int from user and converts it to hex and returns string
	public static String intToHex(int inputFromUser){
		int i = inputFromUser;
		String s = Integer.toHexString(i);
		return s;
	}

	public int findNumberOfBytesInConstant(String constant){
		int LengthOfByte;
		char[] byteContent = constant.toCharArray();
		if(byteContent[0]== 'X'){
			LengthOfByte = ((constant.length()-3)/2);
		}
		else LengthOfByte = (constant.length()-3);
		return LengthOfByte;

	}


}

////Searches for symbol in operand and replaces it with value from symTab
//public void searchAndReplaceSymbol(){
//	if(!assemblyLine.getOperand1().equals("")){
//		if((assemblyLine.getOperand1().matches("[a-zA-Z]*"))&&
//				(!assemblyLine.getOperand1().matches(".'"))){
//			Symbol tempSym = symTab.get(assemblyLine.getOperand1());
//			if(tempSym == null){return;}//TODO: Throw undefined symbol excep. Set error-flag?
//
//		}
//		char[] tempCharArr = assemblyLine.getOperand1().toCharArray();
//		//if(tempCharArr[0]){}
//
//	}
//	return;
//}


//startAddress = assemblyLine.getOperand1();
//char[] startAddressArray = new char[6];
//for (int i = 0; i < startAddressArray.length; i++){
//	if (startAddress.length() < 6){
//		while(i < (6 - startAddress.length())){
//			startAddressArray[i] = 0;
//		}
//	}
//	else startAddressArray[i] = startAddress.charAt ;
//}
//
//	startAddress = assemblyLine.getOperand1();
//char[] startAddressArray = new char[6];
//for (int i = 0; i < startAddressArray.length; i++){
//	int j = 0;
//	if (startAddress.length() < 6){
//		while(i < (6 - startAddress.length())){
//			startAddressArray[i] = 0;
//			
//		}
//	}
//	else startAddressArray[i] = startAddress.charAt(i-j);
//}
//
//
