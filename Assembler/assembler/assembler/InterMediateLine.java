/**
 * 
 */
package assembler;

/**
 * @author Magnus
 *
 */
public class InterMediateLine {
	private AL assemblyLine;
	private String locctr;
	
	public InterMediateLine(String locctr, AL assemblyLine){
		this.assemblyLine = assemblyLine;
		this.locctr = locctr;
	}
	
	//Getters and setters
	public void setAssemblyLine(AL assemblyLine) {
		this.assemblyLine = assemblyLine;
	}
	public AL getAssemblyLine() {
		return assemblyLine;
	}
	public void setLocctr(String locctr) {
		this.locctr = locctr;
	}
	public String getLocctr() {
		return locctr;
	}

}
