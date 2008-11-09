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
	private BufferedWriter outRecord;
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
		
		firstPass();
		secondPass();
		
		try {
			outOverview.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
//	PASS ONE
	public void firstPass(){

		assemblyLine = alstr.nextAL();
		

		if (assemblyLine.getOpmnemonic() == "START"){

			startAddress = assemblyLine.getOperand1();
			while(startAddress.length() < 6) startAddress = "0" + startAddress;

			locctr = intToHex((Integer.parseInt(startAddress)));
			
			InterMediateLine currentInterMediateLine = new InterMediateLine(locctr, assemblyLine);
			intermediateLines.add(currentInterMediateLine);
			
			assemblyLine = alstr.nextAL();
		}
		else locctr = "000000";
		
		
		
		while(!alstr.atEnd()){
			InterMediateLine currentInterMediateLine = new InterMediateLine(locctr, assemblyLine);
			intermediateLines.add(currentInterMediateLine);
			
			if(!assemblyLine.isFullComment()){
				if(assemblyLine.getLabel() != ""){
					if(symTab.containsKey(assemblyLine.getLabel())){
						//						TODO: finne ut korsjen vi kasta skikkeelige exeptions
						//						throw new IllegalDuplicateError;
					}
					else {
						Symbol sym = new Symbol(locctr, "", 0, 0);
						symTab.put(assemblyLine.getLabel(), sym);
					}
				}
				if(assemblyLine.getOpmnemonic() != ""){
					correctLOCCTR(assemblyLine);
				}
				//				else throw new InvalidOpcode;
			}
			
			assemblyLine = alstr.nextAL();
		}
		String programLength = hexMath(locctr, '-' ,startAddress);
		while(programLength.length() < 6) programLength = "0" + programLength;
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
//	Makes object code
	public String makeObjectCode() throws IOException{

		String objectCode, opCodeValue, programCounter, base, targetAddress, disp, x="0", b="0", p="0", e="0";

		//		get value of mnemonic in optable

		OpCode tempOpCode = new OpCode(assemblyLine.getOpmnemonic());
		OpTable opTable = new OpTable();
		opCodeValue = opTable.lookup_AsHex(assemblyLine.getOpmnemonic());

		//		get value of targetAddress(TA)
		targetAddress = operand1;

		if (tempOpCode.getFormat() == 2){
			opCodeValue = hexMath(opCodeValue, '+', "3");
			while (opCodeValue.length() < 2) opCodeValue = "0" + opCodeValue;
			if(operand1 == "") operand1 = "0";
			if(operand2 == "") operand2 = "0";
			objectCode = opCodeValue + operand1 + operand2;
		}

		else{
			//		check indirect and immediate
			if(assemblyLine.isIndirect() && !assemblyLine.isImmediate())	
				opCodeValue = hexMath(opCodeValue, '+', "2");

			else if(!assemblyLine.isIndirect() && assemblyLine.isImmediate())
				opCodeValue = hexMath(opCodeValue, '+', "1");

			else opCodeValue = hexMath(opCodeValue, '+', "3");

			//		check index
			if(assemblyLine.isIndexed())
				x="1";

			//		check if pcPossible and if true, get PC
			if(isPCPossible()){
				p="1";
				b="0";

				//			getting the PC from next lines locctr then resetting the locctr
				
				
				
				InterMediateLine currentInterMediateLine = iter.next();
				interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
				programCounter = currentInterMediateLine.getLocctr();				
				
				currentInterMediateLine = iter.previous();	

				//			setting disp for PC-relative(=TA-PC)
				disp = hexMath(targetAddress, '-', programCounter);
				while (disp.length() < 3) disp = "0" + disp;
			}

			//		check if basePossible and if true, get basevalue
			else if (isBasePossible()){
				p="0";
				b="1";

				//			get basevalue
				base = this.base;

				//			setting disp for base-relative(=TA-BASE)
				disp = hexMath(targetAddress, '-', base);
				while (disp.length() < 3) disp = "0" + disp;

			}
			else {
				p="0";
				b="0";
				disp = targetAddress;
			}
			//		check if extended
			if(assemblyLine.isExtended()){
				e="1";
				disp = targetAddress;
				while (disp.length() < 5) disp = "0" + disp;	
			}


			String flagHex = x+p+b+e; 
			int i= Integer.parseInt(flagHex,2);
			flagHex = Integer.toHexString(i);

			//		make objectcode
			while (opCodeValue.length() < 2) opCodeValue = "0" + opCodeValue;

			objectCode = opCodeValue + flagHex + disp;
		}

		return objectCode;
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
	public void printToRecord(String objectCode){
		//		Takes an objectcode string and prints it as a new line in the RecordFile
		try {
			if(outRecord == null) 
				outRecord = new BufferedWriter(new FileWriter("Record", true));
			outRecord.write(objectCode);
		} catch (IOException e) {
		}
	}
	public Boolean isPCPossible(){
		//		When program-counter relative mode is used, disp
		//		is a 12-bits signed integer
		//		2’s complement

		//		TA=(PC)+ disp 
		//		Program-counter relative b=0,p=1

		if ((-2048 <= Integer.parseInt(targetAddress, 16) && Integer.parseInt(targetAddress, 16) <= 2047))
			return true;
		else return false;
	}
	public Boolean isBasePossible(){
		//		When base relative mode is used, disp is a 12-bits
		//		unsigned integer

		//		TA=(B)+disp
		//		Base relative b=1,p=0 

		if(0 <= Integer.parseInt(targetAddress, 16) && Integer.parseInt(targetAddress, 16) <=4095 && baseFlag == true)
			return true;
		else return false;
	}

	public String intToHex(int inputFromUser){
		//		Takes a decimal int from user and converts it to hex and returns string

		int i = inputFromUser;
		String s = Integer.toHexString(i);
		return s;
	}

}
