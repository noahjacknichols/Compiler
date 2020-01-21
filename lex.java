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

class symbol {
	public token symbolToken;
	public int id;
	public symbol(token tok, int ID) {symbolToken = tok; id = ID;}

	public token getSymbolToken() {
		return symbolToken;
	}
	public int getSymbolID(){
		return id;
	}
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		symbol oSymbol = (symbol) o;
		return symbolToken == oSymbol.symbolToken && id == oSymbol.id;
		}
}
class symbolTable {
	int counter;
	private  ArrayList<symbol> symbols = new ArrayList<symbol>();
	public symbolTable(){
		counter = 0;
	}

	public void addSymbol(symbol sy){
		symbols.add(sy);
	}

	public Boolean contains(symbol sy){
		for (symbol tableInd : symbols){
			if(sy == tableInd){
				return true;
			}
		}
		return false;
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
				//reached EOF
			}
		}
	}
	//48-59 ASCII 0-9
	private static boolean isDigit(char c){
		return (c >= 48 && c<=57);

	}
	//97-122 ASCII a-Z
	private static boolean isLetter(char c){
		return (c >= 97 && c<=57);
	}
	 private static void getNextToken(char c){
		System.out.print(c);
		if(WHITESPACE.contains(c)){
			return; 
		}

		if(isDigit(c)){
			//get the number
		}else if(isLetter(c)){
			//get the word, compare to reserved / symbol table
		}
			
			
	}


} 