package assembler;

import java.util.HashMap;

/*
* @author Andreas Urke
* @author Magnus Lervag
* @since   05.11.2008
* @version 05.11.2008
* 
* SymbolTable contains a HashMap with the Symbols as values and their label
* as keys, and provide a get and a insert method.
*/
public class SymbolTable {
	public HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
//	
//	/**
//	 * Puts a Symbol into the symTab.
//	 * @return Null if no value was paired with the key. The value if otherwise
//	 */
//	public Symbol insertSymbol(String label, Symbol symbol){
//		return symTab.put(label, symbol);
//	}
//	
//	/**
//	 * Gets a Symbol from the symTab.
//	 * @return Null if no value was paired with the key. The value if otherwise
//	 */
//	public Symbol getSymbol(String label){
//		return symTab.get(label);
//	}
//	
//	public Symbol searchHash(String label){
//		symTab.
//		
//	}
}
