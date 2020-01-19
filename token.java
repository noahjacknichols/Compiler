public class token {
	public tokenType type;
	public String name;
	public token(String n, TokenType t) {name = n; type = t;}
	public token(char n, TokenType t) { name = String.valueOf(n); type = t;}

	public getString() {
		return "<"+ name + ">";
	}

	public getType() {
		return type;
	}
	public getName() {
		return name;
	}
}
