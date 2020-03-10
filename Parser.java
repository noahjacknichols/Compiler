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
	public String getRepresentation(){
        // System.out.print(this.getString());
		if(this.type.compareTo("VARIABLE") == 0){
			return "ID";
        }else if(this.value.equals("<") || this.value.equals("=") || this.value.equals(">")){
            // System.out.print("here");
            return "COMPARATOR";

        }else if(this.type.compareTo("TERMINAL") == 0 || this.type.compareTo("RESERVED") == 0){
			return this.value;
		}else if(this.type.compareTo("INTEGER") == 0){
			return "NUMBER";
        }else if(this.type.compareTo("DOUBLE") == 0){
            return "NUMBER";
        }else if(this.type.compareTo("COMPARATOR") == 0){
            return this.type;
        }else{
			return this.value;
		}
	}
}

class symbol {
	public token symbolToken;
	public int id;
	public String value = "";
	public String type = "";

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
    public String printSymbol(){
        // return "hello";
        return this.symbolToken.getName() + ", " + this.id + ", " + this.type + ", " + this.value;
	}
	public void setValue(String s){
		this.value = s;
	}
	public void setType(String s){
		this.type = s;
	}
}

class symbolTable {
    int counter;
    String name;
	public ArrayList<symbol> symbols = new ArrayList<symbol>();

	public symbolTable(String s) {
        name = s;
		counter = 0;
	}

	public void addSymbol(final token tk) {
		symbols.add(new symbol(tk, counter));
		counter++;
    }
    public String getName(){
        return this.name;
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
	public symbolTable dupe(){
		symbolTable duplicate = new symbolTable(this.name + "_rec");
		for(int i = 0; i < this.counter; i++){
			duplicate.addSymbol(this.symbols.get(i).getSymbolToken());
		}
		return duplicate;
	}
    public void printSymbolTable(){
        for(int i = 0; i < symbols.size(); i++){
            System.out.println(symbols.get(i).printSymbol());
        }
	}
	public symbol getSymbol(String name){
		return this.symbols.get(this.index(name));
	}

}

class node{
	node parent = null;
	String type = "";
	node right = null;
	node left = null;
	String value = "";
	public ArrayList<node> nodes = new ArrayList<node>();
	ArrayList<token> conditional = new ArrayList<token>(); // x <> y 
	public node(String Type){
		this.type = Type;
		
	}
	public void setValue(String value){
		this.value = value;
	}
	public void setLeft(node left){
		this.left = left;
	}

	public void setRight(node right){
		this.right = right;
	}
	public void addNode(node n){
		nodes.add(n);
	}
	public node getNode(int index){
		return nodes.get(index);
	}

}


public class Parser {
    private static final List<String> RESERVED = Arrays.asList("if", "fi", "then", "else", "do", "od", "def", "fed",
    "int", "double", "print", "return", "or", "and", "not", "while");
	private lex lexer = new lex();
	private token lookahead = null;
	private token token = null;
    private static Hashtable<String, List<String>> FIRST = new Hashtable<String, List<String>>();
    private static Hashtable<String, List<String>> FOLLOW = new Hashtable<String, List<String>>();
    private String currentName, currentFuncName, currentType, currentValue;
    private int tabCount = 0;
    private String addSpace = " ";
	public static ArrayList<symbolTable> symboltables = new ArrayList<symbolTable>();
	String getLastType = "";
	node currentNode = null;
	// createSymbolTable("global");
	
	public static ArrayList<node> functionNodes = new ArrayList<node>();

	private void initializeFIRST() {
        FIRST.put("program", Arrays.asList("def", "int", "double", "if", "while", "print", "return", "ID"));
        FIRST.put("fdecls", Arrays.asList("def", "EPSILON"));
        FIRST.put("fdec", Arrays.asList("def", "EPSILON"));
        FIRST.put("fdec_r", Arrays.asList("def", "EPSILON"));
        FIRST.put("params", Arrays.asList("int", "double", "EPSILON"));
		FIRST.put("params_r", Arrays.asList(",", "EPSILON"));
		FIRST.put("fname", Arrays.asList("ID"));
		FIRST.put("declarations", Arrays.asList("int", "double", "EPSILON"));
        FIRST.put("decl", Arrays.asList("int", "double"));
        FIRST.put("decl_r", Arrays.asList("int", "double", "EPSILON"));
        FIRST.put("type", Arrays.asList("int", "double"));
		FIRST.put("varlist", Arrays.asList("ID"));
		FIRST.put("varlist_r", Arrays.asList(",", "EPSILON"));
		FIRST.put("statement_seq", Arrays.asList("if", "while", "print", "return", "ID", "EPSILON"));
        FIRST.put("statement", Arrays.asList("if","while","print","return","ID","EPSILON"));
        FIRST.put("statement_seq_r", Arrays.asList(";", "EPSILON"));
		FIRST.put("opt_else", Arrays.asList("else", "EPSILON"));
		FIRST.put("expr", Arrays.asList("ID", "NUMBER", "("));
		FIRST.put("term", Arrays.asList("ID", "NUMBER", "("));
		FIRST.put("term_r", Arrays.asList("+", "-", "EPSILON"));
		FIRST.put("var_r", Arrays.asList("[","EPSILON"));
        FIRST.put("var", Arrays.asList("ID"));
        FIRST.put("comp", Arrays.asList("COMPARATOR", "<", "=", ">"));
        FIRST.put("bfactor_r_p", Arrays.asList("(", "not", "ID", "NUMBER", "EPSILON"));
        FIRST.put("bfactor", Arrays.asList("(", "not"));
        FIRST.put("bfactor_r", Arrays.asList("and", "EPSILON"));
        FIRST.put("bterm", Arrays.asList("(", "not"));
        FIRST.put("bterm_r", Arrays.asList("or", "EPSILON"));
        FIRST.put("bexpr", Arrays.asList("(", "not"));
        FIRST.put("exprseq_r", Arrays.asList(",", "EPSILON"));
        FIRST.put("exprseq", Arrays.asList("(", "ID", "NUMBER"));
        FIRST.put("factor", Arrays.asList("(", "ID", "NUMBER"));
        FIRST.put("factor_r", Arrays.asList("*", "/", "%", "EPSILON"));
        FIRST.put("factor_r_p", Arrays.asList("(","EPSILON"));
    }
    private void initializeFOLLOW() {
        FOLLOW.put("program", Arrays.asList("$"));
        FOLLOW.put("fdecls", Arrays.asList("int", "double", "if", "while", "print", "return", "ID"));
        FOLLOW.put("fdec", Arrays.asList(";"));
        FOLLOW.put("fdec_r", Arrays.asList(";"));
        FOLLOW.put("params", Arrays.asList(")"));
		FOLLOW.put("params_r", Arrays.asList(")"));
		FOLLOW.put("fname", Arrays.asList("("));
		FOLLOW.put("declarations", Arrays.asList("if","while","print","return","ID"));
        FOLLOW.put("decl", Arrays.asList(";"));
        FOLLOW.put("decl_r", Arrays.asList(";"));
        FOLLOW.put("type", Arrays.asList("ID"));
		FOLLOW.put("varlist", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
		FOLLOW.put("varlist_r", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
		FOLLOW.put("statement_seq", Arrays.asList(".","fed","fi","od","else"));
        FOLLOW.put("statement", Arrays.asList(".",";","fed","fi","od","else"));
        FOLLOW.put("statement_seq_r", Arrays.asList(".",";","fed","fi","od","else"));
		FOLLOW.put("opt_else", Arrays.asList("fi"));
		FOLLOW.put("expr", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]"));
		FOLLOW.put("term", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
		FOLLOW.put("term_r", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
		FOLLOW.put("var_r", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
        FOLLOW.put("var", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
        FOLLOW.put("comp", Arrays.asList(""));
        FOLLOW.put("bfactor_r_p", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bfactor", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bfactor_r", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bterm", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bterm_r", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bexpr", Arrays.asList("then","do",")","or"));
        FOLLOW.put("exprseq_r", Arrays.asList(")"));
        FOLLOW.put("exprseq", Arrays.asList(")"));
        FOLLOW.put("factor", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
        FOLLOW.put("factor_r", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
        FOLLOW.put("factor_r_p", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
    }

    public static void main(String[] args) throws IOException {
        // System.out.println("Here");
		Parser parser = new Parser();
    }
    public Parser() throws IOException {
        // System.out.println("HERE");
        initializeFIRST();
        initializeFOLLOW();
        // lexer.readInput();
        // lexer.printTokens();
        // System.out.println("initialized FIRST");
        boolean validParse = true;
        consumeToken();
        // if(token.getName().equals("def") == false){
        //     System.out.println("error");
        // }
        consumeToken(); // Twice to initialize token & lookahead
		// System.out.println("consumed two tokens");
		
		
		program();
		
		// System.out.println("\nValid Parse: " + validParse);
		
		if (validParse)
		{
            // System.out.println("successful parse.");
            printSymbolTables();
			
		}else{
            System.out.println("error");
        }
    }
    

    public void createSymbolTable(String s){
        for (int i = 0; i < symboltables.size(); i++){
            if(symboltables.get(i).getName().equals(s)==true){
                return;
            }
            
        }  
        symboltables.add(new symbolTable(s)); 

    }

    public symbolTable getSymbolTable(String s){
        for(int i=0; i < symboltables.size(); i++){
            if(symboltables.get(i).getName().equals(s)){
                return symboltables.get(i);
            }
        }
        return null;
	}
	public boolean hasSymbolTable(String s){
		for(int i = 0; i < symboltables.size(); i++){
			if(symboltables.get(i).getName().equals(s)){
				return true;
			}
		}
		return false;
	}

    public void addSymbol(token tok){
        symbolTable sym = getSymbolTable(currentFuncName);
        if(sym.contains(tok.getName()) == false){
            sym.addSymbol(tok);
        }
    }

    public void printSymbolTables(){
        System.out.println("\n-------------------------");
        for(int i = 0; i< symboltables.size(); i++){
            System.out.println("SYMBOL TABLE: " + symboltables.get(i).getName());
            symboltables.get(i).printSymbolTable();
            System.out.println("\n-----------------------");
        }
    }
    public boolean checkTableName(String s){
        for(int i = 0; i < symboltables.size(); i++){
            if(symboltables.get(i).getName().equals(s)){
                return true;
            }
        }
        return false;
    }

    // public void addMainSymbolTable(){
    //     createSymbolTable("main");
    //     for(int i = 0; i < symboltables.size(); i++){
    //         ArrayList<symbol> symbol = symboltables.get(i).getSymbols();
    //         for(int j = 0; j < symbol.size(); j++){

    //         }
    //     }
    // }


    public void program() {
		createSymbolTable("global");
		node global = new node("global");
		currentNode = global;
		String first = checkFIRST("program");
		if(first != null){
			fdecls(); declarations(); statement_seq(); match('.');
		}
	}
	
	public void fdecls() {
        System.out.println("fdecls");
		String first = checkFIRST("fdecls");
		if(first != null)
			fdec();fdec_r();
	}
	
	public void fdec() {
        System.out.println("fdec");
		String first = checkFIRST("fdec");
		System.out.println("token is:"+ lookahead.getRepresentation().equals("def"));
		if(first != null && lookahead.getRepresentation().equals("def")){
			System.out.println("lookahead is def");
			node oldCurrent = currentNode;
			node temp = new node("function");

			match("def"); type(); temp = fname(); 
			currentNode = temp; 
			match("("); params(); match(")"); declarations(); statement_seq();
			functionNodes.add(temp);
			currentNode = oldCurrent; 
			System.out.println("MATCH FED PHASE"); match("fed"); match(';');
		}
	}
	
	public void fdec_r() {
        System.out.println("fdec_r");
		String first = checkFIRST("fdec_r");
		if(first != null && lookahead.getRepresentation().equals("def")){
			fdec(); fdec_r();
		}
	}

	public void params() {
        System.out.println("params");
		String first = checkFIRST("params");
		if (first != null){
			type();
			var();
			params_r();
		}

	}
	
	
	public void params_r() {
        System.out.println("params_r");
		String first = checkFIRST("params_r");
		if (first != null){
			match(",");
			params();
		}
	}
	
	public node fname() {
        System.out.println("fname");
		String first = checkFIRST("fname");
		if (first != null){
			System.out.println("FNAME");
			currentName = lookahead.getName();
            currentFuncName = currentName;
			createSymbolTable(currentFuncName);
			functionNodes.add(new node(currentFuncName));
			node temp = new node(currentFuncName);
				
			match("ID");
			return temp;
		}
		else
			return null;
	}
	
	public void declarations() {
		System.out.println("declarations");
		String first = checkFIRST("declarations");
		if(first != null){
			decl(); match(";"); declarations();
		}
	}
	
	public void decl() {
		System.out.println("decl");
		String first = checkFIRST("decl");
		if(first != null){
			type(); varlist();
		}
	}
	
	public void decl_r() {
		System.out.println("lookahead:" + lookahead.getName());
		String first = checkFIRST("decl_r");
		if(first != null){
			match(";"); declarations();
		}
		System.out.println("end of decl_r");
	}
	
	public void type() {
        System.out.println("type");
        String first = checkFIRST("type");
        
        if(lookahead.getRepresentation().equals("int")){
			getLastType = "int";
			match("int");
			return;
        }else if(lookahead.getRepresentation().equals("double")){
			getLastType = "double";
			match("double");
			return;
        }
	}
	
	public void statement_seq() {
		String first = checkFIRST("statement_seq");
		
		if(first != null){
			statement(); statement_seq_r();
		}
	}
	
	public void varlist() {
		System.out.println("varlist");
		String first = checkFIRST("varlist");
		if (first != null){
			var(); varlist_r();
		}

	}

	
	public void varlist_r() {
		System.out.println("varlist_r");
		String first = checkFIRST("varlist_r");
		if (first != null){
			match(","); varlist();
		}
		System.out.println("varlist_r first empty");
	}
	
	public node statement() {
        System.out.println("statement");
		String first = checkFIRST("statement");
		node temp = new node("statement");
		if(first != null){
			switch(first) {
				case "ID":
					System.out.println("ID");
					temp.setLeft(var());
					match("=");
					temp.setValue("=");
					temp.setRight(expr());
					// match();
					currentNode.nodes.add(temp);
					return temp;
				case "if":
					match("if"); 
					//set current global to temp;
					temp.setValue("IF");
					node lastCurrent = currentNode;
					currentNode = temp;
					temp.setLeft(bexpr()); match("then"); statement_seq(); opt_else(); match("fi"); 
					currentNode = lastCurrent;
					return temp;
				case "while":
					temp.setValue("WHILE");
					node lastCurrent2 = currentNode;
					currentNode = temp;
					
					match("while"); temp.setLeft(bexpr()); match("do"); statement_seq(); match("od");
					currentNode = lastCurrent2;
					return temp;
				case "print":
					temp.setValue("PRINT");
					match("print"); temp.setLeft(expr());
					return temp;
				case "return":
					temp.setValue("RETURN");
					match("return"); temp.setLeft(expr());
			}
		}
		return null;
	}
	
	public void statement_seq_r() {
        System.out.println("Statement_seq_r");
		String first = checkFIRST("statement_seq_r");

		if(first != null){
			match(";"); statement(); statement_seq_r();
		}
	}

	public void opt_else() {
        System.out.println("opt_else");
		String first = checkFIRST("opt_else");
		if (first != null){
			match("else"); statement_seq();
		}
	}
	
	public node expr(){
        System.out.println("expr");
		String first = checkFIRST("expr");
		node temp = new node("expr");
		if (first != null){

			temp.setLeft(term());
			temp.setRight(term_r());
			return temp;
		}else{
			return temp;
		}
	}
	
	public node term_r() {
		System.out.println("term_r");
		
		String first = checkFIRST("term_r");
		node temp = new node("term_r");
		if (first != null) {
			System.out.println("first not null");
			if (first.equals("+")){
				System.out.println("+");
				temp.setValue("+");
				match("+");
				temp.setLeft(term()); 
				temp.setRight(term_r());

				return temp;
			}else if (first.equals("-")){
				System.out.println("-");
				temp.setValue("-");
				match("-") ;
				temp.setLeft(term());
				temp.setRight(term_r());
				return temp;
			}else{
				System.out.println("NOT RECURSIVE CALL");
				temp.setValue(lookahead.getName());
				match();
				return temp; // need to fail here
			}
		} else { //Epsilon
			return temp;
		}
	}
	
	public node term() {
        System.out.println("term");
		String first = checkFIRST("term");
		node temp = new node("term");
		if (first != null){
			temp.setLeft(factor());
			temp.setRight(factor_r());
			return temp;
		}else{
			return null;
		}
	}
	
	public node factor_r() {
        System.out.println("factor_r");
		String first = checkFIRST("factor_r");
		node temp = new node("factor_r");
		if (first != null) {
			switch(first) {
				case "*":

					match("*"); temp.setLeft(factor()); temp.setRight(factor_r());
					return temp;
				case "/":
					match("/"); temp.setLeft(factor()); temp.setRight(factor_r());
					return temp;
				case "%":
					match("%"); temp.setLeft(factor()); temp.setRight(factor_r());
					return temp;
				default:
					return null;
			}
		}
		return null;
	}

	 public node factor() {
        System.out.println("factor");
		String first = checkFIRST("factor");
		node temp = new node("factor");
		if (first != null) {
			if (first.equals("ID")){
				System.out.println("factor ID " + lookahead.getName());
				temp.value = lookahead.value;
				
				if(hasSymbolTable(lookahead.getName())){
					match("ID"); 
					System.out.println("ID HAS SYMTABLE");
					temp = factor_r_p();
					return temp;
				}
				match("ID"); 
				

				return temp; //if is function call this factor_r_p();
			}else if (first.equals("NUMBER")){
				temp.value = lookahead.value;
				match("NUMBER");
				return temp;
			}else if (first.equals("(")){
				
				match("("); temp = expr(); match(")");
				return temp;
			}else if (first.equals("ID")){
				return var();
			}else{
				return temp;
			}
		} else {
			return null;
		}
		
	}
 
	public node factor_r_p() {
        System.out.println("factor_r_p");
		String first = checkFIRST("factor_r_p");
		node temp = new node("factor_r_p");
		if (first != null) {
			if (first.equals("(")){
				match("("); temp = exprseq(); match(")");
				return temp;
			}
		}
		System.out.println("factor_r_p first null");
		return null;
	}
	
	public node exprseq() {
        System.out.println("exprseq");
		String first = checkFIRST("exprseq");
		if (first != null){
			expr(); exprseq_r();
		}
		return null;
	}
	
	public void exprseq_r() {
        System.out.println("exprseq_r");
		String first = checkFIRST("exprseq_r");
		if (first != null)
			match(","); exprseq();
	}
	
	public node bexpr() {
        System.out.println("bexpr");
        String first = checkFIRST("bexpr");
		// System.out.print(first);
		node temp = new node("bexpr");
		if (first != null){
			temp.setLeft(bterm()); temp.setRight(bterm_r());
			return temp;
		}
		return null;
			
	}
	
	public node bterm_r() {
        System.out.println("bterm_r");
		String first = checkFIRST("bterm_r");
		node temp = new node("bterm_r");
		if (first != null){
			match("or"); temp.setLeft(bterm()); temp.setRight(bterm_r());
			return temp;
		}
		return null;
	}
	
	public node bterm() {
        System.out.println("bterm");
		String first = checkFIRST("bterm");
		node temp = new node("bterm");
		if (first != null){
			temp.setLeft(bfactor()); temp.setRight(bfactor_r());
			return temp;
		}
		return null;
	}
	
	public node bfactor_r() {
        System.out.println("bfactor_r");
		String first = checkFIRST("bfactor_r");
		node temp = new node("bfactor_r");
		if (first != null){
			match("and"); temp.setLeft(bfactor()); temp.setRight(bfactor_r());
			return temp;
		}
		return null;
	}
	
	public node bfactor() {
        System.out.println("bfactor");
		String first = checkFIRST("bfactor");
		node temp = new node("bfactor");
		switch (first) {
			case "(":
				match("("); temp.setLeft(bfactor_r_p());match(")");
				return temp;
			case "not":
				match("not"); temp.setLeft(bfactor());
				return temp;
		}
		return null;
	}
	
	// Careful
	public node bfactor_r_p() {
        System.out.println("bfactor_r_p");
		String first = checkFIRST("bfactor_r_p");
		node temp = new node("bfactor_r_p");
		if (FIRST.get("bfactor_r_p").contains(first) && token.getRepresentation().equals("COMPARATOR")){
			temp.setLeft(expr()); temp.setValue(comp()); temp.setRight(expr());
			return temp;
        }else if (FIRST.get("bfactor_r_p").contains(first)){
			temp.setLeft(bexpr());
			return temp;
		}
		return null;
	}
	
	public String comp() {
        System.out.println("comp");
        String first = checkFIRST("comp");
        // System.out.println(first);
        if (first != null){
            // System.out.println("return match");
			match("COMPARATOR");
			return lookahead.getName();
		}
		return "";
	}
	
	public node var() {
        System.out.println("var");
		String first = checkFIRST("var");
		if (first != null)
		{
			currentName = lookahead.getName();
			
			if (currentFuncName != null){
                System.out.println("HERE\n");
                // printSymbolTables();
                // System.out.println("token is:"+  lookahead.getName());
                if(checkTableName(lookahead.getName()) == false){
					if(getSymbolTable(currentFuncName).contains(currentName) == false){
						getSymbolTable(currentFuncName).addSymbol(lookahead);
						System.out.println("lookahead name:" + currentName);
						System.out.println("last type:" + getLastType);
						getSymbolTable(currentFuncName).getSymbol(lookahead.getName()).setType(getLastType);
					}
                }
            }else{
				if(getSymbolTable("global").contains(currentName) == false){
					getSymbolTable("global").addSymbol(lookahead);
					getSymbolTable("global").getSymbol(lookahead.getName()).setType(getLastType);
				}
			} 
			System.out.println("HERE AGAIN");
			System.out.println("1:"+lookahead.getName());
			match();
			System.out.println("2:"+lookahead.getName());
			// match();
			// var_r();
			return new node(lookahead.getName());
		}
		return null;
	}
	
	public void var_r() {
        System.out.println("var_r");
		String first = checkFIRST("var_r");
		if (first != null){
			
			 match("["); expr(); match("]");
		}
	}
	
	// UTILITY FUNCTIONS
	
	public void consumeToken() {
        lookahead = token;
        try{
            if (token == null || (token != null && (token.getRepresentation().equals("END") != true))) {
                token = lexer.nextToken();
            }
        }catch(Exception e){
            System.out.println(e);
        }
		
	}
	
	public String checkFIRST(String nonterminal) {
        List<String> first = FIRST.get(nonterminal);
        // System.out.println(first);
        // System.out.println("token:" +token.getRepresentation()+", lookahead:" + lookahead.getRepresentation());
		if (first != null) {
			if (lookahead.getRepresentation().equals("int") && first.contains("ID")) {
				return "ID";
			} else if (lookahead.getRepresentation().equals("int") || lookahead.getRepresentation().equals("double") && first.contains("NUMBER")) {
				return "NUMBER";
			} else if (first.contains(lookahead.getRepresentation())) {
                // System.out.println("first contains lookahead");
				return lookahead.getRepresentation();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public boolean match() {
        prettyPrint(lookahead.getRepresentation());
		System.out.println("the dumb function");
		consumeToken();
		return true;
	}
	
	public boolean match(char c) {
		System.out.println("character tomatch:" + c);
		System.out.println("character we are matching to:" + lookahead.getRepresentation());
		boolean isMatch = lookahead.getRepresentation().equals(String.valueOf(c));
        if (isMatch){ 
            prettyPrint(String.valueOf(c));
            consumeToken();
        }else{
			System.exit(0);
		}
		return isMatch;
	}

	public String fixComparator(String s){
		String[] comps = {"=","<",">", "<>", "<=", ">=", "=="};
		if(Arrays.asList(comps).contains(s)){
			return "COMPARATOR";
		}else{
			return s;
		}
		
	}
	
	public boolean match(String s) {
		s = fixComparator(s);
		System.out.println("string tomatch:" + s);
		System.out.println("debug:" + lookahead.getName());
		System.out.println("string we are matching to:" + lookahead.getRepresentation());
        boolean isMatch = lookahead.getRepresentation().equals(s);
        // System.out.println(token.getString());
        // System.out.println("MATCHING: " + s + " lookahead:"+lookahead.getRepresentation());
        // System.out.println(isMatch);
		if (isMatch) 
		{
            prettyPrint(s);
            
			if (s.equals("fed"))
				currentFuncName = null;
			
			consumeToken();
		}else{
			System.exit(0);
		}
		return isMatch;
    }
    
    public void prettyPrint(String s){

        
        // System.out.println("PRETTY PRINT");
        String addons[] = {"+", "-", "*", "/", "%"};
        
        if(lookahead.getRepresentation().equals("ID") || lookahead.getRepresentation().equals("COMPARATOR") || lookahead.getRepresentation().equals("NUMBER")){
            if(currentFuncName != null && lookahead.getRepresentation().equals("COMPARATOR") == false){
                if(checkTableName(lookahead.getName()) == false){
                    if(getSymbolTable(currentFuncName).contains(lookahead.getName()) == false){
                     getSymbolTable(currentFuncName).addSymbol(lookahead);
                    }
                }
            }else if(currentFuncName == null){
                //add to main
                // createSymbolTable("main");
                // if(getSymbolTable("main").contains(lookahead.getName())== false){
                //     getSymbolTable("main").addSymbol(lookahead);
                // }
            }
            s = lookahead.getName();
            addSpace = "";
        }
        if(token.getName().equals("fed")){
            tabCount = tabCount -1;
        }

        if(s.equals(")")){
            // System.out.println(token.getRepresentation());
            addSpace ="";
            if(token.getName().equals("then") || token.getName().equals(")")|| token.getName().equals(";") || Arrays.asList(addons).contains(token.getName())){
                // System.out.print("here");
                if(Arrays.asList(addons).contains(token.getName())){
                    addSpace = " ";
                }
                System.out.print(s + addSpace);
                // tabCount = tabCount +1;
                
            }else{
                addSpace = " ";
                System.out.print(s + '\n');
                // tabCount = tabCount + 1;
                for(int i = 0; i < tabCount; i++){
                    System.out.print("  ");
                }

            }
        }else if(s.equals("def") || s.equals("if") || s.equals("while")){
            tabCount = tabCount + 1;
            System.out.print(s+ addSpace);
        }else if(Arrays.asList(addons).contains(s)){
            System.out.print(s);
        }
        else if(s.equals(";")){
            System.out.print(s + '\n');
            for(int i = 0; i < tabCount; i++){
                System.out.print("  ");
            }
        }else if(s.equals("fi") || s.equals("fed")){
            System.out.print(s);
            // tabCount = tabCount - 1;
        }else if(s.equals("return")){
            tabCount = tabCount -1;
            System.out.print(s + addSpace);
        }else if(s.equals("(")){
            System.out.print("(");
        }else if(s.equals("then")){
            System.out.print(s + '\n');
            for(int i = 0; i < tabCount; i++){
                System.out.print("  ");
            }
        }
        else{
            System.out.print(s +addSpace);
        }
        addSpace = " ";

    }

}




class lex {
	private static final List<String> RESERVED = Arrays.asList("if", "fi", "then", "else", "do", "od", "def", "fed",
			"int", "double", "print", "return", "or", "and", "not", "while");
	private static final List<Character> TERMINALS = Arrays.asList(';', ',', '(', ')', '[', ']', '+', '-', '*', '/',
			'%', '.', '=', '>', '<');
	private static final List<Character> WHITESPACE = Arrays.asList(' ', '\n', '\t', '\f','\r');
    private static ArrayList<token> allTokens = new ArrayList<token>();
    private static int index = 0;

	public static ArrayList<token> getTokens(){
		return allTokens;
	}
    // read in info
    
    public lex()throws IOException{
        readInput();
    }
	public static void readInput() throws IOException {
        int ch;
        String s = "main";
		symbolTable symTable = new symbolTable(s);
		symTable.init(RESERVED);
		token temp_tk;
		// ArrayList<token> allTokens = new ArrayList<token>();
		// System.out.println("processing:");
		while ((ch = System.in.read()) != -1) {
				temp_tk = getNextToken((char) ch, symTable);
				// System.out.println(temp_tk.getString());
				allTokens.add(temp_tk);
				// reached EOF
		}
		// parse();
		//no longer need to parse into HTML
		// encodeHTML(allTokens, symTable);
    }
    public static token nextToken(){
        if(allTokens.size() > 0){
            return allTokens.remove(0);
        }else{
            return new token("","");
        }
        

    }
    public static void printTokens(){
        for(int i = 0; i < allTokens.size(); i++){
            System.out.print(allTokens.get(i).getString() +",");
        }
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

		while(WHITESPACE.contains(c)){
			// System.in.mark(10000);
			// System.out.println("found whitespace");
			// if(c == '\n' || c == '\r'){
			// 	return new token("\n", "NEWLINE");

			// }else if(c == '\t'){
			// 	return new token("\t", "TAB");
			// }else{
			// 	return new token(" ", "SPACE");
			// }
			c = readNextChar();
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
