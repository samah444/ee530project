/**
 * 
 */
package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Andreas
 *
 */
public class Andreas {
	private AL assemblyLine;
	private int locctr = 0;
	private int lineNumber = 0;
	private BufferedWriter outOverview;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	//TODO: outOverview.close på slutten av Assembleren.
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


	public void correctLOCCTR(){
		String opmnemonic = assemblyLine.getOpmnemonic();
		if(opmnemonic.equals("WORD"))locctr+=3;
		else if(opmnemonic.equals("RESW"))
			locctr+= (3 * Integer.parseInt(assemblyLine.getOperand1()));
		else if(opmnemonic.equals("RESB"))	
			locctr+= Integer.parseInt(assemblyLine.getOperand1());
		else if(opmnemonic.equals("BYTE")){
			// 	FIND LENGTH OF CONSTANT AND ADD TO LOCCTR
			//	TODO: For BYTE: trenger en metode som: Find length of constant in bytes.
		}
		else {
			OpCode tempOpCode = new OpCode(opmnemonic);
			locctr+=tempOpCode.getFormat();
		}
	}

}
