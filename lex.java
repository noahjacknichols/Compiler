import java.io.IOException;
import java.util.*;


class token {
	public String type;
	public String value;
	public token(final String n, final String t) {
		value = n;
		type = t;
	}

	public token(final char n, final String t) {
		value = String.valueOf(n);
		type = t;
	}

	public String getString() {
		return "<" + value + "," + type + ">";
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

	public symbol(final token tok, final int ID) {
		symbolToken = tok;
		id = ID;
	}

	public token getSymbolToken() {
		return symbolToken;
	}

	public int getSymbolID() {
		return id;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final symbol oSymbol = (symbol) o;
		return symbolToken == oSymbol.symbolToken && id == oSymbol.id;
	}
}

class symbolTable {
	int counter;
	public ArrayList<symbol> symbols = new ArrayList<symbol>();

	public symbolTable() {
		counter = 0;
	}

	public void addSymbol(final token tk) {
		symbols.add(new symbol(tk, counter));
		counter++;
	}

	public void init(final List<String> initials) {
		for (final String term : initials) {
			this.addSymbol(new token(term, "TERMINAL"));
		}
	}

	public Boolean contains(String name) {
		for (final symbol tableInd : symbols) {
			if (name.compareTo(tableInd.getSymbolToken().getName()) == 0) {
				return true;
			}
		}
		return false;
	}

	public int index(String name) {
		int count = 0;
		for (final symbol tableInd : symbols) {
			if (name.compareTo(tableInd.getSymbolToken().getName())== 0) {
				return count;
			}
			count++;
		}
		return -1;
	}
}

public class lex {
	private static final List<String> RESERVED = Arrays.asList("if", "fi", "then", "else", "do", "od", "def", "fed",
			"int", "double", "print", "return", "or", "and", "not", "while");
	private static final List<Character> TERMINALS = Arrays.asList(';', ',', '(', ')', '[', ']', '+', '-', '*', '/',
			'%', '.', '=', '>', '<');
	private static final List<Character> WHITESPACE = Arrays.asList(' ', '\n', '\t', '\f','\r');

	// read in info
	public static void main(final String args[]) throws IOException {
		int ch;
		symbolTable symTable = new symbolTable();
		symTable.init(RESERVED);
		token temp_tk;
		ArrayList<token> allTokens = new ArrayList<token>();
		// System.out.println("processing:");
		while ((ch = System.in.read()) != -1) {
				temp_tk = getNextToken((char) ch, symTable);
				// System.out.println(temp_tk.getString());
				allTokens.add(temp_tk);
				// reached EOF
		}
		encodeHTML(allTokens);
	}

	private static void encodeHTML(ArrayList<token> tk){
		String header = "<!DOCTYPE html>\n<html>\n<style>body{\nbackground-color: #050505;}\n</style>\n";

		String comment = "<!--\n";
		for (token tkInd: tk){
			comment = comment + tkInd.getString();
		}
		comment = comment + "\n-->";

		String body = "<body>\n";
		boolean onError = false;
		for (token tkInd: tk){
			if(tkInd.getType().compareTo("ERROR") == 0){
				onError = true;
			}else if(tkInd.getType().compareTo("NEWLINE") == 0 || tkInd.getName().compareTo(";") == 0){
            	onError= false;
			}
			body = body +getColorP(tkInd, onError) + "";
		}
		System.out.print(header + body + comment);
	}
	private static String getColorP(token tk, boolean onError){
		String color = "red";	
		if(onError == true){
			return "<font color=\"red\"><bold>" + tk.getName() +"</bold></font>";
		}
		switch(tk.getType()){
			case "ERROR":
				return "<font color=\"#c50000\">" + tk.getName()+"</font>";
			case "VARIABLE":
				return "<font color=\"#edc951\">" + tk.getName()+"</font>";	
			case "COMPARATOR":
				return "<font color=\"white\">" + tk.getName()+"</font>";	
			case "TERMINAL":
				return "<font color=\"#eb6841\">" + tk.getName()+"</font>";	
			case "RESERVED":
				return "<font color=\"#087e8b\">" + tk.getName()+"</font>";	
			case "END":
				return "<font color=\"white\">" + tk.getName()+"</font></p>";	
			case "INTEGER":
				return "<font color=\"#00a0b0\">" + tk.getName()+"</font>";	
			case "FLOAT":
				return "<font color=\"brown\">" + tk.getName()+"</font>";	
			case "SPACE":
				return " ";
			case "TAB":
				return "    ";
			case "NEWLINE":
				return "</p><p>";
		}
		return "";
	}
	// 48-59 ASCII 0-9
	private static boolean isDigit(final char c) {

		return (c >= 48 && c <= 57);

	}

	// 97-122 ASCII a-Z
	private static boolean isLetter(final char c) {
		return (c >= 97 && c <= 122);
	}

	private static boolean isTerminal(final char c) {
		if (TERMINALS.contains(c)) {
			return true;
		}
		return false;
	}

	private static char readNextChar() throws IOException {
		return (char) System.in.read();
	}

	private static token getNextToken(char c, symbolTable symTable) throws IOException{
		// System.out.println("c:" +(char) c);
		
		if(WHITESPACE.contains(c)){
			// System.in.mark(10000);
			// System.out.println("found whitespace");
			if(c == '\n' || c == '\r'){
				return new token("\n", "NEWLINE");

			}else if(c == '\t'){
				return new token("\t", "TAB");
			}else{
				return new token(" ", "SPACE");
			}
			// c = readNextChar();
		}
		switch(c){
			case '=':
				System.in.mark(10000);
				c = readNextChar();
				if(c == '=') return new token("==", "COMPARATOR");
				else System.in.reset();return new token("=", "TERMINAL");
			case '<':
				System.in.mark(10000);
				c = readNextChar();
				if(c == '=') return new token("<=", "COMPARATOR");
				else if(c == '>') return new token("<>", "COMPARATOR");
				else System.in.reset();return new token("<", "TERMINAL");
			case '>':
				System.in.mark(10000);
				c = readNextChar();
				if(c == '=') return new token(">=", "COMPARATOR");
				else System.in.reset();return new token(">", "TERMINAL");
		}

		if(isDigit(c)){
			// System.out.println("isDigit");
			//continue reading until no longer number
			String digitBuffer = "";
			while(isDigit(c)){
				digitBuffer = digitBuffer + c;
				System.in.mark(10000);
				c = readNextChar();
			}
			// System.out.println("digit:" + digitBuffer);
			// System.out.println("c is now:" + (char) c);
			if(c != 'E' && c != 'e' && isLetter(c)){
				//Invalid Integer
				// System.out.println("I shouldn't be here");
				return new token(digitBuffer+c, "ERROR");
			}else if(c != '.'){
				//its not a floating point number, and is valid
				symTable.addSymbol(new token(digitBuffer, "INTEGER"));
				System.in.reset();
				return new token(digitBuffer, "INTEGER");
			}else if(c == '.'){
				//fix this garbage tmrw
				digitBuffer = digitBuffer + c;
				c = readNextChar();
				while(isDigit(c)){
					digitBuffer = digitBuffer + c;
					System.in.mark(10000);
					c = readNextChar();
				}
				if(c != 'E' && c!='e' && isLetter(c)){
					return new token(digitBuffer, "ERROR");
				}
				else {
					System.in.reset();
					return new token(digitBuffer, "FLOAT");
				}

			}
		
		}else if(isLetter(c)){
			// System.out.println("isLetter");
			//get the word, compare to reserved / symbol table
			String wordBuffer = "";
			while(isLetter(c) || isDigit(c)){
				wordBuffer = wordBuffer + c;
				System.in.mark(10000);
				c = readNextChar();
				// System.out.print(c);
			}
			if(symTable.contains(wordBuffer) == false){
				//new id
				token tk = new token(wordBuffer, "VARIABLE");
				symTable.addSymbol(tk);
				System.in.reset();
				return tk;
			} else{
				int index = symTable.index(wordBuffer);
				// System.out.println("index is:" + index);
				if(index < RESERVED.size()){
					System.in.reset();
					return new token(wordBuffer, "RESERVED");
				}else{
					System.in.reset();
					return new token(wordBuffer, "VARIABLE");
				}

			}
		} else if(TERMINALS.contains(c)){
			//figure out terminal
			// System.out.println("isTerminal");
			if(c == '.'){
				// System.in.reset();
				return new token(c, "END");
			}else {
				// System.in.reset();
				return new token(c, "TERMINAL");
			}
		} 
		final token tok = new token(c, "NON_PARSEABLE");
		return tok;
		
			
			
	}


} 