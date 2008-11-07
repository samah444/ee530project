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
	private String locctr = "0000";
	private int lineNumber = 0;
	private BufferedWriter outOverview;

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
		while(!alstr.atEnd()){
			assemblyLine = alstr.nextAL();
			if(assemblyLine.getOpmnemonic().equals("START")){
				printToRecord(makeHeaderRecord());
			}
			if(assemblyLine.isFullComment()){}
			else{
				//if(assemblyLine.getOperand1())
				
				
			}
			
		}

	}
	//Creates the header record and returns it as a string.
	public String makeHeaderRecord(){
		String programName = assemblyLine.getLabel();
		boolean shortened = false;
		if (programName.equals(""))programName = "PROG  ";
		while(programName.length() > 6){
			shortened = true;
			programName = programName.substring(0, programName.length()-1);
		}
		while(programName.length() < 7) programName += " ";
		if(shortened){
			System.out.println("Program name too long, has been cut to: "
					+ programName);
		}
		return ("H" + programName + startAddress + programLength );
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
