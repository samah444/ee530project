package assembler;

public class Simen {

	public int findNumberOfBytesInConstant(String constant){
		int LengthOfByte;
		char[] byteContent = constant.toCharArray();
		//if(constant.equals("BYTE")){

		if(byteContent[0]== 'X'){
			LengthOfByte = ((constant.length()-3)/2);
		}
		else LengthOfByte = (constant.length()-3);
		return LengthOfByte;
	}
}

