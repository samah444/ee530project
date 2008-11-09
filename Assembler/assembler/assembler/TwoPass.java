package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;


//You'll be filling this one out
public class TwoPass
{
	private ALStream alstr;
	private OpTable optab;
	private AL assemblyLine;
	private int lineNumber = 0;
	private BufferedWriter outOverview;
	private AL interMediateAssemblyLine;
	private String locctr = "000000";
	private ArrayList<InterMediateLine> intermediateLines = new ArrayList<InterMediateLine>();
	private boolean baseFlag = false;
	private ListIterator<InterMediateLine> iter;
	private String base = "";
	private String objectCodeString = "";
	private int lengthOfTextRec = 0;
	private String startAddress;
	private String programLength;
	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();


	public TwoPass(OpTable opt, ALStream alstream)
	{ alstr = alstream; optab = opt; }

	public void assemble()
	{
		// 2 pass algorithm to assemble the "code" in
		// successive elements of ALStream object

		// i.e.  Y O U R  C O D E ! ! ! !
		
		secondPass();
		
		try {
			outOverview.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	//The Second pass
	public void secondPass(){
		iter = intermediateLines.listIterator();
		String operand1;
		String operand2;
		String objectCode = "";
		while(!iter.hasNext()){
			InterMediateLine currentInterMediateLine = iter.next();
			interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
			locctr = currentInterMediateLine.getLocctr();
			assemblyLine = alstr.nextAL();
			//IF OPCODE IS NOT END
			if(!interMediateAssemblyLine.getOpmnemonic().equals("END")){
				operand1 = interMediateAssemblyLine.getOperand1();
				operand2 = interMediateAssemblyLine.getOperand2();
				if(interMediateAssemblyLine.getOpmnemonic().equals("START")){
					printHeaderRecord(interMediateAssemblyLine);
				}
				//IF COMMENT
				if(interMediateAssemblyLine.isFullComment()){}
				//IF NOT COMMENT
				else{
					searchAndProcessBase(interMediateAssemblyLine);
					//IF BYTE OR WORD
					if(interMediateAssemblyLine.getOpmnemonic().equals("BYTE")
							|| (interMediateAssemblyLine.getOpmnemonic().equals("WORD"))){
						objectCode = constantToHex(interMediateAssemblyLine.getOperand1());
					}
					//IF NOT BYTE OR WORD
					else{
						if(isSymbol(operand1))operand1 = findSymbolAddress(operand1);
						if(isSymbol(operand2))operand2 = findSymbolAddress(operand2);
						objectCode = makeObjectCode();
						
					}
					//WRITE TO TEXTRECORD
					if(lengthOfTextRec == 0)initializeTextRecord();
					if((fitIntoTextRec(objectCode)) 
							&& (!interMediateAssemblyLine.getOpmnemonic().equals("RESW"))
							&& (!interMediateAssemblyLine.getOpmnemonic().equals("RESB")))
							writeObjectCode(objectCode);
					//IF OBJECTCODE DOESNT FIT INTO CURRENT TEXTRECORD OR OPERATOR IS RESW OR RESB
					else {
						fixLengthInTextRecord();
						printToRecord(objectCodeString + "\n");
						initializeTextRecord();
						writeObjectCode(objectCode);
					}
				}
			}
			//IF OPCODE = END
			else printEndRecord();
			interMediateAssemblyLine.setAssembledOpcode(objectCode);
			printToOverviewFile(interMediateAssemblyLine);
		}
	}
	
	//Searches for BASE and NOBASE and processes it.
	public void searchAndProcessBase(AL assemblyLine){
		//IF BASE
		if(assemblyLine.getOpmnemonic().equals("BASE")){
			baseFlag = true;
			if(isSymbol(assemblyLine.getOperand1()))
				base = findSymbolAddress(assemblyLine.getOperand1());
			else base = assemblyLine.getOperand1();
		}
		//IF NOT BASE
		else if(assemblyLine.getOpmnemonic().equals("NOBASE")){
			baseFlag = false;
			base = "";
		}
	}
	
	//Corrects the length in the text record.
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
	
	//Writes the given objectCode to record and increases the lengthOfTextRec.
	public void writeObjectCode(String objectCode){
		objectCodeString += objectCode;
		lengthOfTextRec += objectCode.length();
	}
	
	//Will objecCode fit into current TextRecord? True if Yes, False otherwise.
	public boolean fitIntoTextRec(String objectCode){
		if((objectCode.length()+lengthOfTextRec)<70)
			return false;
		else
			return true;
	}
	
	//Initializes a Text Record and writes it to record.
	public void initializeTextRecord(){
		lengthOfTextRec = 0;
		correctLOCCTRformat();
		objectCodeString = "T" +  locctr + "00";
		lengthOfTextRec += 9;
	}
	//Corrects the format of LOCCTR to 6 alphanumerical.
	public void correctLOCCTRformat(){
		while(locctr.length() < 6)locctr = "0" + locctr;
	}

	//Creates the end record and prints it to file.
	public void printEndRecord(){
		printToRecord("E" + startAddress);
	}

	//Returns true if operand is a Symbol, false otherwise.
	public boolean isSymbol(String operand){
		if(!operand.equals("")){
			if(((operand.matches("[a-zA-Z]*"))&&
					(!operand.matches(".'"))) || (operand.matches("*"))){
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

	

	//Overf�rer bokstavene i formen X'ABC' eller C'ABC' til hex
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

	//Creates the header record and prints it to file.
	public void printHeaderRecord(AL assemblyLine){
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
	
	//Prints a line to the Overview text file.
	public void printToOverviewFile(AL assemblyLine){
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
	public void correctLOCCTR(AL assemblyLine){
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
	
	//Finds number of bytes in given constant and returns it.
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
