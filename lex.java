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
		if(this.getName().compareTo(",") == 0){
			return "<COMMA>";
		}else if(this.getName().compareTo("(") == 0){
			return "<LEFT_BRACKET>";
		}else if(this.getName().compareTo(")") == 0){
			return "<RIGHT_BRACKET>";
		}else if(this.getName().compareTo(";") == 0){
			return "<SEMICOLON>";
		}else if(this.getName().compareTo(".") == 0){
			return "<END>";
		}
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
		parse(allTokens);
		//no longer need to parse into HTML
		// encodeHTML(allTokens, symTable);
	}

	public static Hashtable<String, List<String>> initFIRST(Hashtable<String, List<String>> first){
		first.put("program", Arrays.asList("def", "int", "double", "fed", "if", "fi", "while", "print"));
		first.put("fdecls", Arrays.asList("def"));
		first.put("fdecls_r", Arrays.asList(""));
		first.put("decl", Arrays.asList(""));
		first.put("declr_r", Arrays.asList(""));
		first.put("expr", Arrays.asList(""));
		first.put("expr_r", Arrays.asList(""));
		first.put("term", Arrays.asList(""));
		first.put("term_r", Arrays.asList(""));
		first.put("bexpr", Arrays.asList(""));
		return first;

	}
	public static Hashtable<String, List<String>> initFOLLOW(Hashtable<String, List<String>> follow){
		follow.put("program", Arrays.asList("int", "double", "if", "while", "print"));
		return follow;
	}

	public static boolean match(ArrayList<token> allTokens, token current){

		return false;
	}
	public static String parse(ArrayList<token> allTokens){
		Hashtable<String, List<String>> FIRST = new Hashtable<String, List<String>>();
		Hashtable<String, List<String>> FOLLOW = new Hashtable<String, List<String>>();
		initFIRST(FIRST);
		initFOLLOW(FOLLOW);
		token first = allTokens.remove(0);
		token follow = allTokens.remove(0);
		if(first == null || follow == null){
			return "error";	
		}
		

		return "EOF";
	}

	public static boolean match(String toMatch, ArrayList<token> allTokens){
		if(allTokens.get(0).getName().equals(toMatch)){
			allTokens.remove(0);
			return true;
		}
		return false;
	}
	public static boolean program(ArrayList<token> allTokens){
		if(allTokens.get(0) != null){
			return fdecls(allTokens) && declarations(allTokens) && statement_seq(allTokens) && match('.');
		}else{
			return false;
		}
	}
	public static boolean fdecls(ArrayList<token> allTokens){
		//<fdec> | <fdecls_r>
		token nextToken = allTokens.remove(0);
		if(nextToken.getType().equals("<fdec>")){
			return fdec(allTokens) && match(";", allTokens);
		}else if(nextToken.getType() == "<fdecls_r>"){
			return fdecls_r(allTokens) && match(";", allTokens);
		}else{
			return true;
		}

	}
	public static boolean fdecls_r(ArrayList<token> allTokens){
		token nextToken = allTokens.remove(0);
		if(nextToken.getType().equals("<fdec>")){
			return fdecls_r(allTokens);
		}else{ //epsilon check
			return true;
		}
	}

	public static boolean fdec(ArrayList<token> allTokens){
		token nextToken = allTokens.remove(0);
		if(nextToken.getType().equals("<def>")){
			//now we need to match all this stuff
			return type(allTokens) && fname(allTokens) && match("(", allTokens) && params(allTokens) && match(")", allTokens) && declarations(allTokens) && state_seq(allTokens) && match("fed", allTokens);
		}
		return false;
	}

	public static boolean declarations(ArrayList<token> allTokens){
		token nextToken = allTokens.remove(0);
		if(nextToken.getType().equals("<decl>")){
			return decl(allTokens) && match(";", allTokens);
		}else if(nextToken.getType().equals("<declarations>")){
			return declarations(allTokens) && match(";", allTokens);
			
		}else{
			return true;
		}
	}

	public static boolean decl(ArrayList<token> allTokens){
		token nextToken = allTokens.get(0);
		if(nextToken.getType().equals("<type>")){
			return type(allTokens);
		}
		return false;
	}
	public static boolean type(ArrayList<token> allTokens){
		token nextToken = allTokens.remove(0);
		if(nextToken.getName().equals("INT") || nextToken.getName().equals("DOUBLE")){
			return true;
		}
		return false;
	}

	public static boolean varlist(ArrayList<token> allTokens){

		return var(allTokens) && varlist_r(allTokens);

	}
	public static boolean varlist_r(ArrayList<token> allTokens){
		boolean x = match(",", allTokens) && varlist(allTokens);
		return true;
	}


	private static void encodeHTML(ArrayList<token> tk, symbolTable symTable){
		String header = "<!DOCTYPE html>\n<html>\n<style>body{\nbackground-color: #050505;}\n</style>\n";

		String comment = "<!--\n";
		int count = 1;
		for (token tkInd: tk){
			if(!((tkInd.getType().compareTo("NEWLINE") == 0) || (tkInd.getType().compareTo("SPACE") == 0) || (tkInd.getType().compareTo("TAB") == 0))){
				// System.out.println("token is:" + tkInd.getType()); // System.out.println("token added."); if(tkInd.getType().compareTo("VARIABLE")==0){
					comment = comment + "<ID," +symTable.index(tkInd.getName()) + "> ";
				}else{
				comment = comment + tkInd.getString() + " ";
				if(count % 6 == 0){
					comment = comment + "\n";
					}
				}
				count++;
			}

		comment = comment + "\n-->\n";

		String body = "<body>\n";
		boolean onError = false;

		for (token tkInd: tk){
			// System.out.println(tkInd.getType().compareTo("NEWLINE"));
			if(tkInd.getType().compareTo("ERROR") == 0){
				onError = true;
			}else if(tkInd.getType().compareTo("NEWLINE") == 0 || tkInd.getName().compareTo(";") == 0){
            	onError= false;
			}
			body = body +getColorP(tkInd, onError) + "";
		}
		System.out.print(header + comment + body + "</html>");
	}
	private static String getColorP(token tk, boolean onError){
		if(onError == true){
			return "<font color=\"red\" size = \"12\"><bold>" + tk.getName() +"</bold></font>";
		}
		switch(tk.getType()){
			case "ERROR":
				return "<font color=\"#c50000\" size = \"12\">" + tk.getName()+"</font>\n";
			case "VARIABLE":
				return "<font color=\"#edc951\"size = \"12\">" + tk.getName()+"</font>\n";
			case "COMPARATOR":
				return "<font color=\"white\"size = \"12\">" + tk.getName()+"</font>\n";
			case "TERMINAL":
				return "<font color=\"#eb6841\"size = \"12\">" + tk.getName()+"</font>\n";
			case "RESERVED":
				return "<font color=\"#087e8b\"size = \"12\">" + tk.getName()+"</font>\n";
			case "END":
				return "<font color=\"white\"size = \"12\">" + tk.getName()+"</font></p>\n";
			case "INTEGER":
				return "<font color=\"#00a0b0\"size = \"12\">" + tk.getName()+"</font>\n";
			case "FLOAT":
				return "<font color=\"brown\"size = \"12\">" + tk.getName()+"</font>\n";
			case "SPACE":
				return "<font size = \"12\">&nbsp</font>";
			case "TAB":
				return "<font size = \"12\">&nbsp&nbsp&nbsp&nbsp</font>";
			case "NEWLINE":
				return "</p>\n<p>\n";
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
					// System.out.println("c is:" + c);
					if(c == 'E' || c=='e' || isLetter(c) != true){
					digitBuffer =digitBuffer + c;
					System.in.mark(10000);
					c=readNextChar();
					while(c == '-' || c=='+' || isDigit(c)){
						digitBuffer = digitBuffer +c;
						System.in.mark(10000);
						c = readNextChar();
					}


					System.in.reset();

					return new token(digitBuffer, "FLOAT");
					}else{
						// System.out.println("wrong else");
						System.in.reset();
						return new token(digitBuffer, "ERROR");
					}
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
