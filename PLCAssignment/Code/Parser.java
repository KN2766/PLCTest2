package PLCAssignment.Code;
import java.util.List;

public class Parser {
    private List<Token> source;
    private Token token;
	private int position;
    
    //constructor
    Parser(List<Token> source) {
		this.source = source;
        this.position = 0;
		this.token = getNextToken();

	}
    void parse(){
            System.out.println("Enter program <stmt>");
            while(getNextToken().type != TokenType.EOF){
                stmt();
            }
            if(this.token.type == TokenType.EOF){
                System.out.println("Parse successfull");
            }
            else{
                error(String.format("Expected ending: %s", TokenType.EOF));
            }
        }
    Token getNextToken() {
		this.token = this.source.get(this.position++);
		return this.token;
	}

    // <stmt> --> <ifstmt> | <while_loop> | <assign> | <declaration>
    void stmt(){
        if(this.token.type == TokenType.IF){
            System.out.println("Enter <ifstmt>");
            ifstmt();
        }
        else if(this.token.type == TokenType.WHILE){
            System.out.println("Enter <while_loop>");
            while_loop();
        }
        else if(this.token.type == TokenType.ID){
            System.out.println("Enter <assign>");
            assign();
        }
        else{
            System.out.println("Enter <declaration>");
            declaration();
        }
    }

    //<IF_STMT> --> `if` `(` <BOOL_EXPR> `)` <BLOCK>  [ `else`  <BLOCK> ]
    void ifstmt(){
        if(getNextToken().type  == TokenType.LEFT_PAR){//(
            bool_exp();
            if(getNextToken().type  == TokenType.RIGHT_PAR){
                block();
                if(getNextToken().type  == TokenType.ELSE){
                    block();
                    System.out.println("Parse <ifstmt> done");
                }
                else{
                    error(String.format("Expected %s", TokenType.ELSE));//else
                }
            }
            else{
                error("Expected ')'");
            }
        }
        else{
            error("Expected '('");
        } 
    }

    //<WHILE_LOOP> --> `while` `(` <BOOL_EXPR> `)` <BLOCK> 
    void while_loop(){
        
        if(getNextToken().type == TokenType.LEFT_PAR){
            bool_exp();
            if(getNextToken().type == TokenType.RIGHT_PAR){
                block();
                System.out.println("Parse <while_loop> done");
            }
            else{
                error("Expected ')");
            }
        }
        else{
            error("Expected '(");
        }
    }

    //<ASSIGN> --> ID `=` <EXPR>
    void assign(){
        if(getNextToken().type == TokenType.ASSIGN){//=
            System.out.println("Enter <expr>");
            expr();
            if(this.token.type == TokenType.SEMI){//;
                System.out.println("Parse <assign> done");
            }
            else{
                error("Expected ';'");
            }
        }
        else{
            error("Expected '='");
        }
    }

    //<EXPR> --> <TERM> {(`+`|`-`) <TERM>}
    void expr(){
        term();
        if(this.token.type == TokenType.ADD || this.token.type == TokenType.SUB){
            term();
        }
        System.out.println("<expr> parse done");
    }

    //<term> --> <factor> {(*|/|%) <factor>} 
    void term(){
        factor();
        getNextToken();
        if(token.type == TokenType.MULTI || token.type == TokenType.MOD || token.type == TokenType.DIV){ // token == + | -
            factor();
        }
        System.out.println("<term> parse done");

    }

    //<FACT> --> {(`*`|`/`|`%`) <EXPR>}
    void factor(){
        getNextToken();
        if(token.type == TokenType.ID){
            System.out.println("done <factor>");
        }
        else if(token.type == TokenType.LEFT_PAR){
            expr();
            if(getNextToken().type == TokenType.RIGHT_PAR){
                System.out.println("done <factor>");
            }
            else{
                error("Expected ')");
            }
        }
        else{
            datatype();
            System.out.println("done <factor>");
        }
    }

    //|<DECLARE> --> `DataType` ID {`,` ID }
    void datatype(){
       if(this.token.type == TokenType.INT_LIT || this.token.type == TokenType.FLOAT_LIT || this.token.type == TokenType.CHAR){
        System.out.println("<datatype>");
        
       }
       else{
        error(String.format("Expected %s", TokenType.datatype));
       }
        
    }

    //<declaration> --> <datatype> ID;
    void declaration(){
        if(getNextToken().type == TokenType.ID){
            if(getNextToken().type == TokenType.SEMI){
                System.out.println("Parse <declaration> done");
            }
            else{
                error("Expected ';'");
            }  
        }
        else{
            error(String.format("Expected id %s", TokenType.ID));
        }
    }

    //<block> --> '{' <stmt>; '}'
    void block(){
        if(getNextToken().type == TokenType.LEFT_BRAC){
            stmt();
            if(getNextToken().type == TokenType.SEMI){
                if(getNextToken().type == TokenType.RIGHT_BRAC){
                    System.out.println("Parse <block> done");
                }
                else{
                    error("Expected '}'");
                }
            }
            else{
                error("Expected ';'");
            }
        }
        else{
            error("Expected '{'");
        }

    }

    //<BOOL_EXPR> --> <BTERM> {(`>`|`<`|`>=`|`<=`) <BTERM>}
    void bool_exp(){
        System.out.println("Enter <bool_exp>");
        bool_term();
        getNextToken();
        if( token.type == TokenType.GREATER|| token.type == TokenType.LESS 
        || token.type == TokenType.GREATEROREQUAL || token.type == TokenType.LESSOREQUAL){
            bool_term();
            System.out.println("done <bool_exp>");
        }
        
    }
    //<BTERM> --> <BAND> {(`==`|`!=`) }
    void bool_term(){
        bool_and();
        getNextToken();
        System.out.println("Enter <bool_term>");
        if(token.type == TokenType.ASSIGN || token.type == TokenType.BOR){
            bool_and();
        }

    }
    
    //<BAND> --> <BOR> {`&&` <BOR>}
    void bool_and(){
        bool_op();
        getNextToken();
        System.out.println("Enter <bool_and>");
        if(token.type == TokenType.BAND){
            bool_op();
        }

    }
    //<BOR> --> <EXPR> {`&&` <EXPR>}
    void bool_op(){
        expr();
        getNextToken();
        System.out.println("Enter <bool_op>");
        if(token.type == TokenType.GREATER){
            expr();
        }

    }

    void error(String msg){
        System.out.println(msg);
        System.exit(-1);
    }
}
