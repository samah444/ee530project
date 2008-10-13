package assembler;

public class ALStream {

	  private AL[] ALStr;
	  private AL errorLoc = new AL("ERROR","ERROR","ERROR"); 
	                          // Used in case any weird cases fall through
	  private int nextAL;
	  private int SIZE = 500; 
	  private int resetCount;
	  private int end;

	  // CLIENTS SHOULD NOT CALL THIS METHOD
	  public ALStream()
	  { ALStr = new AL[SIZE]; nextAL = 0; resetCount = 0; }

	  // CLIENTS SHOULD NOT CALL THIS METHOD
	  // Overloaded addInstr method for fullLineComments
	  public void addInstr(String commentText)
	  {
	    AL loc = new AL(commentText);
	    if (nextAL<SIZE)
	      ALStr[nextAL++] = loc;
	    else
	    {
	      System.out.println("Internal assembler error: Too many instructions");
	      System.out.println("No assembly");
	      System.exit(0);
	    }
	  }

	  // CLIENTS SHOULD NOT CALL THIS METHOD
	  // Overloaded addInstr method for all other instructions
	  public void addInstr(String label, String instr, 
	                       String args, String com)
	  {
	    // To build AL object need to get from above args:
	    // opmnemonic w/o annotation, directiveFlag, extendedFlag,
	    // number of args for op, one or two operand strings w/o annot.,
	    // flags from arg field for addressing mode on single arg ops

	    AL loc;  // A Line Of Code

	    boolean xtndFlag;
	    String instrWOannot;
	    if (instr.charAt(0)=='+')
	    {
	      xtndFlag = true;
	      instrWOannot = instr.substring(1,instr.length());
	    }
	    else
	    {
	      xtndFlag = false;
	      instrWOannot = instr;
	    }

	    if (Assembler.isDirective(instrWOannot))
	      // Assuming directives have no annotations, so instr=opm
	      loc = new AL(label,instr,args,com);
	    else if (Assembler.is0ArgInstr(instrWOannot))
	      // Normally 0-arg instructions have no annotation, but still...
	        loc = new AL(label,instrWOannot,com);
	    else if (Assembler.is1ArgInstr(instrWOannot))
	    {
	      // Deal with addressing annotations
	      int addrFlag = AL.AL_NIL; 
	      if (    (args.length()>=3)
	           && (args.substring(args.length()-2,args.length()).compareTo(",X")==0) )
	        addrFlag = AL.AL_NDX;
	      else
	      {
	        char addrChar = args.charAt(0);
	        switch (addrChar) {
	          case '#': addrFlag = AL.AL_IMM; break;
	          case '@': addrFlag = AL.AL_NDR; break;
	          case '=': addrFlag = AL.AL_LIT; break; }
	      }
	      String tmp;
	      if  (addrFlag==AL.AL_NDX)
	        tmp = args.substring(0,args.length()-2);
	      else if (addrFlag!=AL.AL_NIL)
	        tmp = args.substring(1,args.length());
	      else tmp = args;

	      // Remove format annotation if there...
	      if (xtndFlag)
	        loc = new AL(label,instrWOannot,tmp,com,true,addrFlag);
	      else
	        loc = new AL(label,instrWOannot,tmp,com,false,addrFlag);
	    }
	    else if (Assembler.is2ArgInstr(instrWOannot))
	    {
	      // Format is ',' always separates args w/no WS
	      String op1 = new String();
	      String  op2 = new String();
	      int commaPos = -1; char c;
	      while (((c=args.charAt(++commaPos))!=',')&&(c!='\0'));
	      if (commaPos==-1)
	      {
	        System.out.println("Internal assembler error:");
	        System.out.println("Args of a 2arg instr have no comma sep.");
	        System.out.println("No assembly");
	        System.exit(0);
	      }
	      else 
	      {
	        op1 = args.substring(0,commaPos);
	        op2 = args.substring(commaPos+1,args.length());
	      }

	      loc = new AL(label,instrWOannot,op1,op2,com,false);
	    }
	    else loc = errorLoc;


	    if (nextAL<SIZE)
	      ALStr[nextAL++] = loc;
	    else
	    {
	      System.out.println("Internal assembler error: Too many instructions");
	      System.out.println("No assembly");
	      System.exit(0);
	    }

	  }

	  public void showALStream()
	  {
	    AL currLoc;
	    System.out.println("ALStream Contents=============");
	    System.out.println();

	    for (int i=0; i<=end; i++)
	    {
	      currLoc = ALStr[i];
	      System.out.println("Label:  " + currLoc.getLabel());
	      System.out.println("Opmnem: " + currLoc.getOpmnemonic());
	      System.out.println("Arg1:   " + currLoc.getOperand1());
	      System.out.println("Arg2:   " + currLoc.getOperand2());
	      System.out.println("Comnt:  " + currLoc.getComment());
	      System.out.println("-->isFullLineCmnt?: " + currLoc.isFullComment());
	      System.out.println("-->isDirective?:    " + currLoc.isDirective());
	      System.out.println("-->isExtended?:     " + currLoc.isExtended());
	      System.out.println("-->numArgs:         " + 
	                                   currLoc.getOperandType());
	      System.out.println("-->isIndexed?:      " + currLoc.isIndexed());
	      System.out.println("-->isImmediate?:    " + currLoc.isImmediate());
	      System.out.println("-->isIndirect?:     " + currLoc.isIndirect());
	      System.out.println("-->isLiteral?:      " + currLoc.isLiteral());
	      System.out.println();
	    }
	  }

	  public void showNextAL()
	  {
	    AL currLoc = ALStr[nextAL-1];
	    System.out.println("Label:  " + currLoc.getLabel());
	    System.out.println("Opmnem: " + currLoc.getOpmnemonic());
	    System.out.println("Arg1:   " + currLoc.getOperand1());
	    System.out.println("Arg2:   " + currLoc.getOperand2());
	    System.out.println("Comnt:  " + currLoc.getComment());
	    System.out.println("-->isFullLineCmnt?: " + currLoc.isFullComment());
	    System.out.println("-->isDirective?:    " + currLoc.isDirective());
	    System.out.println("-->isExtended?:     " + currLoc.isExtended());
	    System.out.println("-->numArgs:         " + 
	                                 currLoc.getOperandType());
	    System.out.println("-->isIndexed?:      " + currLoc.isIndexed());
	    System.out.println("-->isImmediate?:    " + currLoc.isImmediate());
	    System.out.println("-->isIndirect?:     " + currLoc.isIndirect());
	    System.out.println("-->isLiteral?:      " + currLoc.isLiteral());
	    System.out.println();
	  }
	 
	  public void reset() { resetCount++; nextAL = 0; }

	  // CLIENTS SHOULD NOT CALL THIS METHOD
	  public void setEnd() { end = nextAL - 1; reset(); }

	  public boolean atEnd() { return (nextAL > end); }

	  public AL nextAL() 
	  {   
	    if (atEnd()) 
	    {
	      System.out.println("Assembler Error: past end of ALStream");
	      System.exit(0);
	    }
	    else if (resetCount>2)
	    {
	      System.out.println("Client Integrity Error: More than two passes attempted");
	      System.exit(0);
	    }

	    return ALStr[nextAL++]; 
	  }

	}
