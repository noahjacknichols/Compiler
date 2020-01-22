import java.io.IOException;
import java.util.*;


class token {
	public String type;
	public String value;
	public token(String n, String t) {value = n; type = t;}
	public token(char n, String t) { value = String.valueOf(n); type = t;}

	public String  getString() {
		return "<"+ value + "," + type +">";
	}

	public String getType() {
		return type;
	}
	public String getName() {
		return value;
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

	public void addSymbol(token tk){
		symbols.add(new symbol(tk, counter));
		counter++;
	}
	public void init(List<String> initials){
		for (String term : initials){		
			this.addSymbol(new token(term, "TERMINAL"));
		}
	}

	public Boolean contains(symbol sy){
		for (symbol tableInd : symbols){
			if(sy == tableInd){
				return true;
			}
		}
		return false;
	}
	public int index(symbol sy){
		int count = 0;
		for (symbol tableInd: symbols){
			if(sy == tableInd){
				return count;
			}
			count++;
		}
		return -1;
	}
}



public class lex{
	private static final List<String> RESERVED = Arrays.asList("if","fi","then","else","do", "od", "def", "fed", "int", "double", "print", "return", "or", "and", "not", "while");
	private static final List<Character> TERMINALS = Arrays.asList(';',',','(',')','[',']','+','-', '*','/','%','.','=','>','<');
	private static final List<Character> WHITESPACE = Arrays.asList(' ', '\n','\t');
	private char end = '.';
	private symbolTable symTable = new symbolTable();
	symTable.init(RESERVED);
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
	private static boolean isTerminal(char c){
		if(TERMINALS.contains(c)){
			return true;
		}
		return false;
	}
	private static char readNextChar() throws IOException {
		return (char) System.in.read();
	}
	 private static token getNextToken(char c) throws IOException{
		System.out.print(c);
		if(WHITESPACE.contains(c)){
			c = readNextChar();
		}
		switch(c){
			case '=':
				c = readNextChar();
				if(c == '=') return new token("==", "COMPARATOR");
				else return new token("=", "TERMINAL");
			case '<':
				c = readNextChar();
				if(c == '=') return new token("<=", "COMPARATOR");
				else if(c == '>') return new token("<>", "COMPARATOR");
				else return new token("=", "TERMINAL");
			case '>':
				c = readNextChar();
				if(c == '=') return new token(">=", "COMPARATOR");
				else return new token("=", "TERMINAL");
		}

		if(isDigit(c)){
			//continue reading until no longer number
			String digitBuffer = "";
			while(isDigit(c)){
				digitBuffer = digitBuffer + c;
				c = readNextChar();
			}
			if(c != 'E' && c != 'e' && isLetter(c)){
				//Invalid Integer
				return new token(digitBuffer, "ERROR");
			}else if(c != '.'){
				//its not a floating point number

			}
		}else if(isLetter(c)){
			//get the word, compare to reserved / symbol table
		} else if(isTerminal(c)){
			//figure out terminal
		}
		token tok = new token(c, "ERROR");
		return tok;
		
			
			
	}


} 