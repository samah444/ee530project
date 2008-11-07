package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.IllegalFormatException;

public class Magnus {

	private ALStream alstr;
	private AL assemblyLine;
	private Integer disp = 0;
	private Boolean baseFlag;
	private String startAddress, programLength, locctr;
	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	
	Boolean isPCPossible(){
//		When program-counter relative mode is used, disp
//		is a 12-bits signed integer
//		2’s complement
		 
//		TA=(PC)+ disp 
//		Program-counter relative b=0,p=1
		
		if ((-2048 <= disp && disp <= 2047))
		return true;
		else return false;
	}
	Boolean isBasePossible(){
//		When base relative mode is used, disp is a 12-bits
//		unsigned integer
		
//		TA=(B)+disp
//		Base relative b=1,p=0 
		
		if(0 <= disp && disp <=4095 && baseFlag == true)
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
					correctLOCCTR();
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
	
	public void correctLOCCTR(){
		String opmnemonic = assemblyLine.getOpmnemonic();
		if(opmnemonic.equals("WORD"))locctr+=3;
		else if(opmnemonic.equals("RESW"))
			locctr+= (3 * Integer.parseInt(assemblyLine.getOperand1()));
		else if(opmnemonic.equals("RESB"))	
			locctr+= Integer.parseInt(assemblyLine.getOperand1());
		else if(opmnemonic.equals("BYTE")){
			locctr+= findNumberOfBytesInConstant(assemblyLine.getOperand1());
		}
		else {
			OpCode tempOpCode = new OpCode(opmnemonic);
			locctr+=tempOpCode.getFormat();
		}
		else {
			OpCode tempOpCode = new OpCode(opmnemonic);
			LOCCTR+=tempOpCode.getFormat();
			locctr+=tempOpCode.getFormat();
		}
	}
}
