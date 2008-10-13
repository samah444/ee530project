package assembler;

import java.util.*;
import java.io.*;

// NOTE: Some of the error-checking discussed in class referred to 
// the detection and reporting of items like invalid memory addresses, 
// undefined references, and the like. These are checked by client
// assembler code. The given front-end assembler code ensures that, 
// from a lexical point of view, the ALStream contains only valid 
// lines of SIC/XE assembly code. So, not much error-checking happens
// here on the instructions themselves, which should be correct already.
// Floating point instructions are recognized here, even though we
// do not support them in the assemblers we develop in 530.
// Directives are in here because some information about them is relevant
// and expressible in similar fashion to regular instructions. See
// the OpCodes.def file for further exposition.

public class OpTable 
{
  private Vector ops = new Vector(26); // Indexable, 'dynamic' memory 
  private int x;                       // For use in binary search

  // CLIENTS SHOULD NOT CALL THIS METHOD
  // Constructor builds table from external opCodeFile: opCodes.def
  // Exception handling is nil--it is assumed that correct lexical 
  // structure exists in the input file. In these methods, what
  // error checking there is supports the assembler lexical analyzer,
  // and nothing more.
  public OpTable() throws IOException
  {
    String s;
    FileInputStream opCodeFile = new FileInputStream("OpCodes.def");
    BufferedReader opCodeDefStream =  
        new BufferedReader(new InputStreamReader(opCodeFile));
    while ((s = opCodeDefStream.readLine())!=null)
      if (s.charAt(0)!='#')
          ops.add(new OpCode(s));
  }

  // CLIENTS SHOULD NOT CALL THIS METHOD
  // Method lookupOp is an internal method used
  // a) to move x to a particular opCode for use by client
  //   methods lookup_AsHex and lookup_AsInt
  // b) and by the lexical analyzer in Assembler.java to verify
  //   the correctness of the instruction stream and predict fields
  // This method performs a binary search on ops
  // Even though it is internal, it is still public so Assembler can see it
  public boolean lookupOp(String s) 
  {
    int Max = ops.size() - 1 ;
    int Min = 0; int compRes = 1;
    x = Max/2;

    compRes = ((OpCode) ops.elementAt(x)).getMnemonic().compareTo(s);
    while(Max != Min && compRes != 0)
    {
      if(compRes < 0) Min = x;
      else Max = x-1;
      x = Max - ((Max - Min)/2);
      compRes = ((OpCode) ops.elementAt(x)).getMnemonic().compareTo(s);
    }

    if (compRes == 0) return true;
    else  // Again, note, this condition will be used by the scanner, not client
    { return false; }
  }





  // CLIENTS MAY WISH TO CALL THIS METHOD
  // Method getOpCode is used by the lexical analyzer and possibly
  // assembler developers to get an opCode instance and ask it
  // its numOperands, format, and offset. As with the others,
  // the error checks are only relevant to the lexical analyzer
  public OpCode getOpCode(String opmnemonic) 
  {
    if (! lookupOp(opmnemonic))
    { 
      System.out.println("Internal assembler error--opTable");
      System.exit(0);
    } 

    return (OpCode) ops.elementAt(x);
	}

  // CLIENTS MAY WISH TO CALL THIS METHOD
  // Method lookup_AsHex will be called by client (assembler developers),
  // who will have been given only valid opCode lexemes, hence no error checks
  public String lookup_AsHex(String opmnemonic)
  {
    if (! lookupOp(opmnemonic)) 
    { 
      //Pretty harsh error response here
      System.out.println("Internal assembler error--opTable");
      System.exit(0);
    }

    return ((OpCode) ops.elementAt(x)).getOpCode();
  }

  // CLIENTS MAY WISH TO CALL THIS METHOD
  // This method not strictly necessary for assembly,
  // but may be useful for debugging
  public void show() 
  {
    for (int i = 0; i < ops.size() ; i++ )
      System.out.println(ops.elementAt(i));
  }

}

