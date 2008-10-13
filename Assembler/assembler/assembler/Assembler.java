package assembler;

import java.io.*;

public class Assembler
{

  // CLIENTS DO NOT CALL THIS METHOD DIRECTLY
  // It is invoked when the Assembler is run as described in README
  public static void main(String[] args) throws IOException
  {
    if (args.length == 0) 
    {
      System.out.println("No source file specified");
      System.exit(0);
    }
    if (args.length > 1) 
    {
      System.out.println("Too many source files specified");
      System.exit(0);
    }

    // ELSE one argument was specified as the source file
    // Create an input file stream
    FileInputStream sourceFile = new FileInputStream(args[0]);

    // Create a buffered reader on source file
    // Will have to use this for comment handling; see below
    BufferedReader sourceStream = 
        new BufferedReader(new InputStreamReader(sourceFile));

    // Setup optable
    optab = new OpTable();

    String nxtok;  // Used to get the 3 fields
                   // and the remaining comments
                   // The ALStream object will analyze substructure
                   // in the fields discovered
                   // The variable nxtok is filled successively
		   // by the tokens for LABEL field, OPCODE field
                   // (w/ annotations), OPERAND field (w/ annotations)

    ALStream alstr = new ALStream();

    // Index map for the fields expected
    // 0 - Label
    // 1 - Instruction (opcode)
    // 2 - Operand
    // 3 - Comment

    String commentLine = new String(); // For whole-line comment text
    int fieldIndex = 0; // To index the four strings as they are found
    boolean foundEOL = false;
    String[] fields = new String[4];  // Always expects 4 fields
      fields[0] = new String();  // Label field
      fields[1] = new String();  // Opcode field (instruction)
      fields[2] = new String();  // Operand field
      fields[3] = new String();  // Comment field

    // Now begin looking for the real tokens; 
    // using readComment after every newline
    // Supplied test files will contain valid instruction
    // mnemonics in the second field
    // Test files provided by client should adhere to this
    // Any error checking performed should pertain to 
    // the assembly process and not input validation
    // This scanner expects labels will never be valid mnemonics

    nxline=sourceStream.readLine(); 
    while (nxline!=null)
    {

      // Check for full line comments
      if ((commentLine=readComment())!=null) 
      {
        alstr.addInstr(commentLine);
        fieldIndex = 0;
        foundEOL = true;
      }

      // Get the label field with some error checking
      else if (fieldIndex==0) // Label field
      {
        nxtok = nextToken();
        if (nxtok.compareTo(EOL)==0)
          errorReport("Empty line");
        else
        {
          fields[fieldIndex] = nxtok;
          if (optab.lookupOp(remPlus(fields[0])))
          {
            fields[1] = fields[0];   // If first token is OPCODE
            fields[0] = "";          // then move it all over one
            if (optab.getOpCode(remPlus(fields[1])).getNumOperands()>0)
              fieldIndex =  2;
            else
            {
              // Expect comment; use stream methods to read comment to end of line
              // Check first for no comment. Definitely no operand field
              fields[2] = "";

              if (nxtok.compareTo(EOL)==0)
                fields[3] = "";
              else
                fields[3] = eolComment();

              fieldIndex = 0;
              foundEOL = true;
            }
          } 
          else
            fieldIndex = 1;
        }
      }


      // Get the instruction field with some error checking
      else if (fieldIndex==1) // Instruction field
      {
        nxtok = nextToken();
        if (nxtok.compareTo(EOL)==0)
          errorReport("No instruction specified");
        else
          fields[fieldIndex++] = nxtok;

        if (!optab.lookupOp(remPlus(fields[1])))
          errorReport("Invalid instruction mnemonic");
        else if (optab.getOpCode(remPlus(fields[1])).getNumOperands()==0)
        {
          // Expect comment; use stream methods to read comment to end of line
          fields[fieldIndex++] = "";
          fields[fieldIndex] = eolComment();

          fieldIndex = 0;      
          foundEOL = true;
        }
      }


      // Get the operand field with some error checking
      else if (fieldIndex==2) // Operand field
      {
        nxtok = nextToken();
        if (nxtok.compareTo(EOL)==0)
        { 
          // No operand or comment specified; adjust fields
          fields[fieldIndex++] = ""; // Operand field
          fields[fieldIndex] = "";   // Comment field

          fieldIndex = 0;
          foundEOL = true;
        }
        else
          fields[fieldIndex++] = nxtok;
      }


      // Get the comment field
      else if (fieldIndex==3) // Comment field
      {
        fields[fieldIndex] = eolComment();

        fieldIndex = 0;      
        foundEOL = true;
      }
      
      // Produce output
      if (foundEOL)
      {
        if (commentLine==null)
          alstr.addInstr(fields[0],fields[1],fields[2],fields[3]);
        else commentLine = null;
        foundEOL = false;
        nxline=sourceStream.readLine(); 
      }

   }

   alstr.setEnd();



   // Uncomment the line below to show the lexical results
   // without needing to implement TwoPass 



    alstr.showALStream();



   // Uncomment the line below to have main call your
   // TwoPass assemble method and complete the assembly

   //TwoPass tp = new TwoPass(optab,alstr);
    //tp.assemble();


  } 

  // CLIENTS SHOULD NOT ACCESS THESE public static VARIABLES
  public static OpTable optab;
  public static String nxline;  
  public static int nxlen = 0;
  public static int nxpos = 0;
  public static String EOL = "EOL"; 

  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static String nextToken()
  {
    boolean done = false;
    boolean inToken = false;
    int tokStart = 0;
    String tok = "";

    if (nxpos==0) nxlen = nxline.length();
    do
    {
      if (!inToken && (nxpos==nxlen))
        { nxpos = 0; nxlen = 0; tok = EOL; done = true; }
      else if (inToken && (nxpos==nxlen))
        { tok = nxline.substring(tokStart,nxpos); done = true; }
      else if (    (nxline.charAt(nxpos)==' ')
                || (nxline.charAt(nxpos)=='\t') )
      {
        if (inToken) { tok = nxline.substring(tokStart,nxpos); done = true; }
        else nxpos++;
      }
      else
      {
        if (!inToken)
        { inToken = true; tokStart = nxpos; }
        nxpos++;
      }
    } while (!done);
    return tok;
  }

  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static String eolComment()
  {
    // Subsequent to call of eolComment and before next call to nextToken
    // nxline should be filled with new line from source stream

    if (nxpos==nxlen)
      { nxpos = 0; nxlen = 0; return ""; }
    else
    {
      char c; int i=0; String comment;
        // Consume whitespace up to first real token
        while (((c=nxline.charAt(nxpos++))==' ') || (c=='\t')) 
        {
          if (nxpos==nxlen) { nxpos = 0; nxlen = 0; return ""; }
          i++;
        }
      comment = nxline.substring(nxpos-1);
      nxpos = 0; nxlen = 0;
      return comment;
    }
  }


  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static String remPlus(String instr)
  {
    if (instr.charAt(0)=='+') return instr.substring(1,instr.length());
    else return instr;
  }

  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static boolean is0ArgInstr(String s)
  { return (optab.getOpCode(s).getNumOperands()==0); }

  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static boolean is1ArgInstr(String s)
  { return (optab.getOpCode(s).getNumOperands()==1); }

  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static boolean is2ArgInstr(String s)
  { return (optab.getOpCode(s).getNumOperands()==2); }

  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static boolean isDirective(String s)
  { return (optab.lookup_AsHex(s).compareTo("0")==0); }

  // CLIENTS MAY WISH TO CALL THIS METHOD UPON DISCOVERY 
  // OF NON-RECOVERABLE ERRORS IN THE INPUT
  public static void errorReport(String msg)
  {
    System.out.println("Error: "+msg);
    System.out.println("No assembly");
    System.exit(0);
  }

  // CLIENTS SHOULD NOT CALL THIS METHOD
  public static String readComment()
  {
    if (nxline.charAt(0)=='.') return nxline;
    else return null;
  }

}

