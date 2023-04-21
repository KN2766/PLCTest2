package PLCAssignment.Code;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Lexer {
    Map<String, TokenType> Tokens = new HashMap<>();
    List<Token> output = new LinkedList<>();
    private char ch;
    private String str;
    int pos;
    int position;


    Lexer(String source){
        this.str = source;
        this.pos = 0;
        this.position = 0;
        this.ch = this.str.charAt(0);
        this.Tokens.put("if", TokenType.IF);
        this.Tokens.put("else", TokenType.ELSE);
        this.Tokens.put("while", TokenType.WHILE);
        this.Tokens.put("EOF", TokenType.EOF);
        
    }
    List<Token> printTokens() {
        Token t;
        while ((t = getToken()).type != TokenType.EOI) {
            output.add(t);
        }
        return output;
    }
    char getNextChar(){
        this.position++;

        if(this.position >= this.str.length()){
            this.ch = '\u0000';
            return this.ch;
        }
        this.ch = this.str.charAt(this.position);
        return this.ch;
    }

    Token getToken() {
        while (Character.isWhitespace(this.ch)) {
            getNextChar();
        }
        char c = this.ch;
        switch (this.ch) {
            
            case '\u0000': return new Token(TokenType.EOI, "");
            case '/': return div_or_cmt();
            case '\'': return character_lit();
            case '<': return follow('=', TokenType.GREATEROREQUAL, TokenType.GREATER);
            case '>': return follow('=', TokenType.LESSOREQUAL, TokenType.LESS);
            case '=': return follow('=', TokenType.ASSIGN, TokenType.EQUAL);
            case '!': return follow('=', TokenType.BNOT, TokenType.EXCLAMATION);
            case '&': return follow('&', TokenType.BAND, TokenType.EOI);
            case '|': return follow('|', TokenType.BOR, TokenType.EOI);
            case '"': return string_lit();
            case '+': getNextChar(); return new Token(TokenType.ADD, Character.toString(c));
            case '-': getNextChar(); return new Token(TokenType.SUB, Character.toString(c));
            case '*': getNextChar(); return new Token(TokenType.MULTI, Character.toString(c));
            case '%': getNextChar(); return new Token(TokenType.MOD, Character.toString(c));
            case ';': getNextChar(); return new Token(TokenType.SEMI, Character.toString(c));
            case ',': getNextChar(); return new Token(TokenType.COMMA, Character.toString(c));
            case '{': getNextChar(); return new Token(TokenType.LEFT_BRAC, Character.toString(c));
            case '}': getNextChar(); return new Token(TokenType.RIGHT_BRAC, Character.toString(c));
            case '(': getNextChar(); return new Token(TokenType.LEFT_PAR, Character.toString(c));
            case ')': getNextChar(); return new Token(TokenType.RIGHT_PAR, Character.toString(c));
            default: return ident_or_num();
        }
    }

    Token follow(char expect, TokenType ifyes, TokenType ifno){
        String symbol = Character.toString(this.ch);
        if(getNextChar() == expect){
            symbol += Character.toString(this.ch);
            getNextChar();
            return new Token(ifyes, symbol);
        }
        if(ifno == TokenType.EOI){
            error(String.format("follow: unrecognized character: %c", symbol));
        }

        return new Token(ifno, symbol);
    }

    Token character_lit(){
        char c = getNextChar();
        String lexemes = "";
        if(c == '\''){
            error("empty character constant");
        }
        else if( c == '\\'){
            c = getNextChar();
            if(c == 'n' || c == '\\'){
                lexemes += c;
            }
            else{
                error(String.format("unknown escapse sequence \\%c", c));
            }
        }
        if(getNextChar() != '\''){
            error("multi-character literal");
        }
        getNextChar();

    return new Token(TokenType.CHAR, lexemes);
    }

    Token string_lit(){
        String output = "";
        while(getNextChar() != '\"'){
            if(this.ch == '\u0000'){
                error("EOF while scanning for string");

            }
            if(this.ch == '\n'){
                error("EOL while scanning for string");
            }

            output += this.ch;
        } 
        getNextChar();
        return new Token(TokenType.STRING, output);       
    }

    Token div_or_cmt() {
        if (getNextChar() != '*') {
            return new Token(TokenType.DIV, Character.toString(ch));
        }
        getNextChar();
        while (true) { 
            if (this.ch == '\u0000') {
                error("Error");
            } else if (this.ch == '*') {
                if (getNextChar() == '/') {
                    getNextChar();
                    return getToken();
                }
            } else {
                getNextChar();
            }
        }
    }

    Token ident_or_num() {
        boolean is_number = true;
        boolean is_float = false;
        String output = "";
            
        while (Character.isAlphabetic(this.ch) || Character.isDigit(this.ch) || this.ch == '_') {
            output += this.ch;
            if (!Character.isDigit(this.ch)) {
                is_number = true;
            }
            if (this.ch == '.') {
                is_float = true;
            }
            getNextChar();
        }
            
        if (output.equals("")) {
            error(String.format("Identifer or number unrecognized character:", this.ch));
        }
            
        if (Character.isDigit(output.charAt(0))) {
            if (is_float) {
                return new Token(TokenType.FLOAT_LIT, output);
            } else {
                if (is_number == true) {
                    return new Token(TokenType.INT_LIT, output);
                } else {
                    error(String.format("Invalid integer or float literal: %s", output));
                }
            }
        }
        
        if (this.Tokens.containsKey(output)) {
            return new Token(this.Tokens.get(output), output);
        }
        return new Token(TokenType.ID, output);
}

    void error(String e){
        System.out.println(e);
        System.exit(0);
    }
    
}
class Token{
   
    public String lexemes;
    public TokenType type;
    
    Token(TokenType type, String lexemes){
        this.lexemes = lexemes;
        this.type = type;

    }

    @Override
    public String toString(){
        return lexemes + " : " + type;
    }
}

enum TokenType{
    EOI,
    ADD, //+
    SUB, //-
    MULTI, //* 
    DIV, // /
    MOD, //%
    LEFT_PAR, // (
    RIGHT_PAR, // )
    LEFT_BRAC, // {
    RIGHT_BRAC, //}
    EQUAL, // =
    EXCLAMATION, //!
    BAND,//&&
    BOR,//||
    ASSIGN, //==
    BNOT, //!= 
    GREATER, // >
    LESS, // < 
    GREATEROREQUAL, // >=
    LESSOREQUAL, //<=
    DOT, //.
    COMMA, //,
    SEMI, //;
    ID, //identifier
    STRING, 
    IF, //if
    ELSE, //else
    WHILE, //while
    EOF,
    datatype,
    natural_num, 
    INT_LIT,
    FLOAT_LIT,
    CHAR,
}
