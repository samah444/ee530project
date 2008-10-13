package assembler;

//AL class encapsulates information obtained from lexical analysis of
//a line of code in the source assembly code (an "assembly line" object).
//The front-end of the assembler will read the input text and produce
//a stream (array) of these objects which contain all the same information
//that can be derived from the text. Then the client assembler has to 
//query these objects in two passes over the stream of them to build the
//object code.

public class AL {

// Features common to all opcode types

//Variables
private String label;                  //May be empty
private String opmnemonic;   
private String operand1;               //May be empty
private String operand2;               //May be empty
private String fullLineCommentText;    //If that's all this instruction is,
                                     //then the rest would be empty
private String endCommentText;         //May be empty
private String assembledObjectCode;    //This is just characters in a string

//Flags
private boolean fullLineComment;
private boolean directive;
private boolean extendedFormat;
private boolean[] flagBits = new boolean[4];  //Not the same as flag bits in machine
                                            //These are what can be detected
                                            //directly from the text
                                            // b and p must be determined
                                            // by the assembler

private int operandType;

// Ints for three opcode classes
public static int AL_0ARG = 0;
public static int AL_1ARG = 1;
public static int AL_2ARG = 2;

// Ints for applicable 1ARG flags
// Ignore the non-intuitive ordering
public static int AL_NIL = -1;
public static int AL_NDX = 0;
public static int AL_IMM = 1;
public static int AL_NDR = 2;
public static int AL_LIT = 3;

// CLIENTS SHOULD NOT CALL THIS METHOD
// Constructor for full-line comments
public AL(String commentText)
{
for (int i=AL_NDX; i<=AL_LIT; i++) flagBits[i] = false;
label = ""; opmnemonic = ""; endCommentText = "";
fullLineCommentText = commentText;
extendedFormat = false; operandType = AL_0ARG;
directive = false;
operand1 = ""; operand2 = "";
fullLineComment = true;
}

// CLIENTS SHOULD NOT CALL THIS METHOD
// Constructor for no argument instructions 
public AL(String lab, String opm, String com)
{
for (int i=AL_NDX; i<=AL_LIT; i++) flagBits[i] = false;
label = lab; opmnemonic = opm; fullLineCommentText = "";
endCommentText = com;
extendedFormat = false; operandType = AL_0ARG;
directive = false;
operand1 = ""; operand2 = "";
fullLineComment = false;
}

// CLIENTS SHOULD NOT CALL THIS METHOD
// Constructor for single argument instructions
public AL(String lab, String opm, String op1of1, String com, 
        boolean xform, int whichAddrFlagTrue)
{
for (int i=AL_NDX; i<=AL_LIT; i++) flagBits[i] = false;
label = lab; opmnemonic = opm; fullLineCommentText = "";
endCommentText = com;
extendedFormat = xform; operandType = AL_1ARG;
directive = false;
operand1 = op1of1; operand2 = "";
if (whichAddrFlagTrue!=AL_NIL)
  flagBits[whichAddrFlagTrue] = true;
fullLineComment = false;
}

// CLIENTS SHOULD NOT CALL THIS METHOD
// Constructor for two-argument instructions
public AL(String lab, String opm, String op1of2, String op2of2,
        String com, boolean xform)
{
for (int i=AL_NDX; i<=AL_LIT; i++) flagBits[i] = false;
label = lab; opmnemonic = opm; fullLineCommentText = "";
endCommentText = com;
extendedFormat = xform; operandType = AL_2ARG;
directive = false;
operand1 = op1of2; operand2 = op2of2;
fullLineComment = false;
}

// CLIENTS SHOULD NOT CALL THIS METHOD
// Constructor for directives
public AL(String lab, String opm, String op1of1, String com)
{
for (int i=AL_NDX; i<=AL_LIT; i++) flagBits[i] = false;
label = lab; opmnemonic = opm; fullLineCommentText = "";
endCommentText = com;
extendedFormat = false; 
directive = true;
operand1 = op1of1; operand2 = "";
if (operand1.compareTo("")==0) operandType = AL_0ARG;
else operandType = AL_1ARG;
fullLineComment = false;
}

public String getLabel() { return label; }
public String getOpmnemonic() { return opmnemonic; }
public String getOperand1() { return operand1; }
public String getOperand2() { return operand2; } // empty if not 2ARG
public String getComment() { if (fullLineComment) return fullLineCommentText;
                             else return endCommentText;}
public String getAssembledObjectCode() { return assembledObjectCode; }
public void setAssembledOpcode(String objectCode) { assembledObjectCode = objectCode; }

public boolean isFullComment() { return fullLineComment; }
public boolean isDirective() { return directive; }
public boolean isExtended() { return extendedFormat; }
public int getOperandType() { return operandType; }

public boolean isIndexed()
{ if (operandType==AL_1ARG) return flagBits[AL_NDX]; 
else return false;
}
public boolean isImmediate()
{ if (operandType==AL_1ARG) return flagBits[AL_IMM];
else return false;
}
public boolean isIndirect()
{ if (operandType==AL_1ARG) return flagBits[AL_NDR];
else return false;
}
public boolean isLiteral()
{ if (operandType==AL_1ARG) return flagBits[AL_LIT];
else return false;
}

}
