package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;

public class Magnus {

	private ALStream alstr;
	private AL assemblyLine;
	private Integer disp = 0;
	private Boolean baseFlag;
	private String startAddress, programLength, locctr, targetAddress, base;
	private HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	private String operand1, operand2;
	private ArrayList<InterMediateLine> intermediateLines = new ArrayList<InterMediateLine>();

	Boolean isPCPossible(){
		//		When program-counter relative mode is used, disp
		//		is a 12-bits signed integer
		//		2’s complement

		//		TA=(PC)+ disp 
		//		Program-counter relative b=0,p=1

		if ((-2048 <= Integer.parseInt(targetAddress, 16) && Integer.parseInt(targetAddress, 16) <= 2047))
			return true;
		else return false;
	}
	Boolean isBasePossible(){
		//		When base relative mode is used, disp is a 12-bits
		//		unsigned integer

		//		TA=(B)+disp
		//		Base relative b=1,p=0 

		if(0 <= Integer.parseInt(targetAddress, 16) && Integer.parseInt(targetAddress, 16) <=4095 && baseFlag == true)
			return true;
		else return false;
	}

	public static String intToHex(int inputFromUser){
		//		Takes a decimal int from user and converts it to hex and returns string

		int i = inputFromUser;
		String s = Integer.toHexString(i);
		return s;
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

	//	PASS ONE
	public void passOne(){

		assemblyLine = alstr.nextAL();
		

		if (assemblyLine.getOpmnemonic() == "START"){

			startAddress = assemblyLine.getOperand1();
			while(startAddress.length() < 6) startAddress = "0" + startAddress;

			locctr = intToHex((Integer.parseInt(startAddress)));
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
		else if(opmnemonic. equals("BYTE")){
			int change = findNumberOfBytesInConstant(assemblyLine.getOperand1());
			locctr = hexMath(locctr, '+', intToHex(change));
		}
		else {
			OpCode tempOpCode = new OpCode(opmnemonic);
			locctr = hexMath(locctr, '+', intToHex(tempOpCode.getFormat()));
		}
	}

	//	Makes object code
	public String makeObjectCode() throws IOException{

		String objectCode, opCodeValue, programCounter, base, targetAddress, disp, tempLocctr, x="0", b="0", p="0", e="0";

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
				AL tempAssemblyLine = alstr.nextAL();
				tempLocctr = locctr;
				correctLOCCTR(tempAssemblyLine);
				programCounter = locctr;

				locctr = tempLocctr;	

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
	public String findSymbolAddress(String operand){
		Symbol aSymbol = symTab.get(operand);
		if (aSymbol == null){return "";}//TODO: Throw undefined Symbol exception. Set error flag?
		else return aSymbol.getAddress();	
	}
//	public void makeAlToArrayList(){
//		ArrayList assemblyLines = new ArrayList();
//		while(!alstr.atEnd()){
//			assemblyLine = alstr.nextAL();
//			correctLOCCTR(assemblyLine);
//			assemblyLines.add(Integer.parseInt(locctr), assemblyLine)
//		}
//		
//	}

}
