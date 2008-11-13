/**
* InterMediateLine contains all the info about a symbol as defined by the programmer in
* the source code. It also provides getters and setter for the info.
* 
* @author Simen Hammerseth
* @author Magnus Lervaag
* @author Andreas Urke
* @version 11.12.2008
* @see TwoPass
* 
*/
package assembler;

// TODO: Auto-generated Javadoc
/**
 * The Class InterMediateLine.
 * 
 * @author Magnus
 */
public class InterMediateLine {
	
	private AL assemblyLine;
	private String locctr;
	
	/**
	 * Instantiates a new inter mediate line.
	 * 
	 * @param locctr the locctr
	 * @param assemblyLine the assembly line
	 */
	public InterMediateLine(String locctr, AL assemblyLine){
		this.assemblyLine = assemblyLine;
		this.locctr = locctr;
	}
	
	//Getters and setters
	/**
	 * Sets the assembly line.
	 * 
	 * @param assemblyLine the new assembly line
	 */
	public void setAssemblyLine(AL assemblyLine) {
		this.assemblyLine = assemblyLine;
	}
	
	/**
	 * Gets the assembly line.
	 * 
	 * @return the assembly line
	 */
	public AL getAssemblyLine() {
		return assemblyLine;
	}
	
	/**
	 * Sets the locctr.
	 * 
	 * @param locctr the new locctr
	 */
	public void setLocctr(String locctr) {
		this.locctr = locctr;
	}
	
	/**
	 * Gets the locctr.
	 * 
	 * @return the locctr
	 */
	public String getLocctr() {
		return locctr;
	}

}
