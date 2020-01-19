import java.io.IOException;
import java.util.*;


class token {
	public String type;
	public String name;
	public token(String n, String t) {name = n; type = t;}
	public token(char n, String t) { name = String.valueOf(n); type = t;}

	public String  getString() {
		return "<"+ name + ">";
	}

	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
}



public class lex{
	private static final List<String> RESERVED = Arrays.asList("if","fi","then","do", "od", "def", "fed", "int", "double", "print", "return", "or", "and", "not");
	private static final List<Character> TERMINALS = Arrays.asList(';',',','(',')','[',']','+','-', '*','/','%','.','=','>','<');
	private static final List<Character> WHITESPACE = Arrays.asList(' ', '\n','\t');
	private char end = '.';
//read in info
	public static void main(String args[]) throws IOException {
		int ch;
		System.out.println("processing:");
		while((ch = System.in.read()) != -1){
			if(ch != '.'){
				getNextToken((char)ch);
			}else {
				//reached EOF
				return 0;
			}

		}
	}
	//48-59 ASCII 0-9
	private static boolean isDigit(char c){
		return (c >= 48 && c<=57);

	}
	//97-122 ASCII a-z
	private static boolean isLetter(char c){
		return (c >= 97 && c<=57);
	}
	 private static void getNextToken(char c){
		System.out.print(c);
		while(WHITESPACE.contains(c){
			
			
	}


} 
