package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Magnus {

	
	
	Boolean isPCPossible(){
//		When program-counter relative mode is used, disp
//		is a 12-bits signed integer
//		2’s complement
		 
//		TA=(PC)+ disp 
//		Program-counter relative b=0,p=1
		
		if ((-2048 <= disp <= 2047))
		return true;
		else return false;
	}
	Boolean isBasePossible(){
//		When base relative mode is used, disp is a 12-bits
//		unsigned integer
		
//		TA=(B)+disp
//		Base relative b=1,p=0 
		
		if(0 <= disp <=4095 && baseFlag == 1 && noBaseFlag == 0)
		return true;
		else return false;
	}
	
	String intToHex(int inputFromUser){
//		Takes a decimal int from user and converts it to hex and returns string
		
		int i = inputFromUser;
	    String s = Integer.toHexString(i);
		return s;
	}
	void printToRecord(String objectCode){
//		Takes an objectcode string and prints it as a new line in the RecordFile
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("RecordFile"));
	        out.write(objectCode);
	        out.close();
	    } catch (IOException e) {
	    }

	}

}
