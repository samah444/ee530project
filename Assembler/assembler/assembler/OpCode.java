package assembler;

import java.util.*;

//Class OpCode encapsulates all the relevant information for an individual
//instruction opcode for the SIC/XE machine (which our assembler is built for).

public class OpCode 
{
private String mnem;      // Assembly lanuage mnemonic
	private int format;       // Which of the four formats is this instruction
	private String opCode;    // Hex value as a String
	private int offset;       // Number of bytes the opcode takes up
	private int numOperands;  // Number of operands used by instruction

// CLIENTS SHOULD NOT CALL THIS METHOD
public OpCode (String s)
{
 StringTokenizer stoks = new StringTokenizer(s);
 String str;

 // External opcodes file assumed correct
 // and in fixed 5-field format
 for (int fieldIndex = 0; fieldIndex < 5 ; fieldIndex++ )
   {
     str = stoks.nextToken();
     switch (fieldIndex) 
     {
       case 0: mnem = str; break;
       case 1: format = Integer.parseInt(str); break;
       case 2: opCode = str; break;
       case 3: offset = Integer.parseInt(str); break;
       case 4: numOperands = Integer.parseInt(str); break;
     }
   }
}

public String getMnemonic() { return mnem; }
public int getFormat()      { return format; }
public String getOpCode()   { return opCode; }
public int getOffset()      { return offset; }
public int getNumOperands() { return numOperands; }


// CLIENTS SHOULD NOT (TYPICALLY) CALL THIS METHOD
// (it is called automatically upon use as a String)
// This method not strictly necessary for assembly, 
// but may be useful for debugging
	public String toString() 
{
 return mnem + " " + format  + " " + opCode  +
 " " + offset  + " " + numOperands;
}

}

