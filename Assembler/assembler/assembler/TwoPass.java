package assembler;

import java.io.BufferedWriter;


//You'll be filling this one out
public class TwoPass
{
  private ALStream alstr;
  private OpTable optab;
  
	private AL assemblyLine;
	private int locctr = 0;
	private int lineNumber = 0;
	private BufferedWriter outOverview;

  public TwoPass(OpTable opt, ALStream alstream)
  { alstr = alstream; optab = opt; }

  public void assemble()
  {
    // 2 pass algorithm to assemble the "code" in
    // successive elements of ALStream object

    // i.e.  Y O U R  C O D E ! ! ! !

  }

}
