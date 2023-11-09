import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class SyntaxAnalyzer {

    private static Stack<Integer> stack = new Stack<>();
    private static boolean expectDataTypeOrIdent = true;
    //private static boolean expectOperandOrDataType = true;
    private static boolean expectOperandOrDataType = false;
    private static boolean expectOperator = false;
    //private static int lineNumber = 1;
    private static boolean expectsemiColum = false;
    private static boolean alreadyincrement = false;
    private static boolean   inparent=false ;
     private static boolean inforCond=false  ;
     public static boolean stackempty=true;
     public static boolean openparen=true;
    public static void main(String[] args) {
        try {
            LexicalAnalyzer.in_fp = new BufferedReader(new FileReader("TestCases\\input7.txt"));
            LexicalAnalyzer.getChar();

            do {
                LexicalAnalyzer.lex();
                int token = LexicalAnalyzer.nextToken;
                //System.out.println("string "+token+'\n');
                //System.out.println("new line"+LexicalAnalyzer.lineNum);
                  

                if (token == LexicalAnalyzer.EOF) {
                    break;
                }
                
                //System.out.println('x');
                

                switch (token) {

                    case LexicalAnalyzer.LEFT_BRACE:
                    case LexicalAnalyzer.LEFT_BRACKET:
                    case LexicalAnalyzer.LEFT_PAREN:inparent=true ;openparen=false;
                    
                    
                        stack.push(token);
                        //System.out.println(stack.lastElement());
                        expectDataTypeOrIdent = true;
                        expectOperandOrDataType = false;
                        expectOperator = false;
                        expectsemiColum = false;
                        break;
                    case LexicalAnalyzer.RIGHT_PAREN:
                    if (stack.isEmpty() || stack.pop() != LexicalAnalyzer.LEFT_PAREN) {
                        System.out.println("Syntax analysis failed.");
                            System.err.println("syntax_analyzer_error - Unmatched closing ) at line " + LexicalAnalyzer.lineNum);
                            
                            return;
                        }
                        expectDataTypeOrIdent = true;
                        expectOperandOrDataType = false;
                        expectOperator = false;
                        expectsemiColum = false;
                        inparent =false ;
                        inforCond=false ;
                        
                        break;
                    case LexicalAnalyzer.RIGHT_BRACE:
                    
                    if (stack.isEmpty() || stack.pop() != LexicalAnalyzer.LEFT_BRACE) {
                        System.out.println("Syntax analysis failed.");
                            System.err.println("syntax_analyzer_error - Unmatched closing } at line " + LexicalAnalyzer.lineNum);
                            //System.out.println("here 2");
                            return;
                        }
                       //System.out.println("here");
                        expectDataTypeOrIdent = true;
                        expectOperandOrDataType = false;
                        expectOperator = false;
                        expectsemiColum = false;
                        //stack.pop();

                        break;
                    case LexicalAnalyzer.RIGHT_BRACKET:
                        if (stack.isEmpty() || stack.pop() != LexicalAnalyzer.LEFT_BRACKET) {
                            System.out.println("Syntax analysis failed");
                            System.err.println("Error - Unmatched ]" + token + " at line " + LexicalAnalyzer.lineNum);
                          
                            return;
                        }
                        expectDataTypeOrIdent = true;
                        expectOperandOrDataType = false;
                        expectOperator = false;
                        expectsemiColum = false;
                        //stack.pop();
                        break;
                    case LexicalAnalyzer.ASSIGN_OP:
                        if (!expectDataTypeOrIdent) { // Assign op should not come when a data type or identifier is expected.
                            expectOperator = false;
                            expectOperandOrDataType = true; // After an assign op, we expect an operand (literal or identifier).
                            expectDataTypeOrIdent=false;
                           expectsemiColum = false;
                        } else {
                            System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - Unexpected assignment operator at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        break;
                    case LexicalAnalyzer.SEMICOLON:
                        // After a semicolon, we expect a new statement, so we should expect a data type or identifier again.
                        if(expectOperandOrDataType ){
                            System.out.println("Syntax analysis failed");
                             System.err.println("Syntax error - Unexpected semi colon operator at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        expectDataTypeOrIdent = true;
                        expectOperandOrDataType = false;
                        expectOperator = false;
                    
                        expectsemiColum = false;
                        alreadyincrement = true;
                        break;
                    case LexicalAnalyzer.INT_LIT:
                    case LexicalAnalyzer.FLOAT_LIT:
                    case LexicalAnalyzer.STR_LIT:
                        if (expectDataTypeOrIdent) {
                             expectOperator = true;
                             expectDataTypeOrIdent = false;
                             expectOperandOrDataType = false;
                             expectsemiColum = true;
                        } else if(expectOperator && !expectOperandOrDataType && expectsemiColum ){
                            System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - String assignment error at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        
                        else if(expectsemiColum && !expectOperandOrDataType){
                            System.out.println("Syntax analysis failed.");
                            System.err.println("syntax_analyzer_error - Missing semi colon at line " + LexicalAnalyzer.lineNum);
                            return;

                        }

                        
                        
                        else if (!expectOperandOrDataType) {
                            System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - Unexpected datatype declaration at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        expectOperandOrDataType = false;
                        expectOperator = true;
                        expectsemiColum = true;
                        break;
                    case LexicalAnalyzer.IDENT:
                         //System.out.println(openparen);
                         if(openparen){
                            System.out.println("Syntax analysis failed.");
                            System.err.println("syntax_analyzer_error - Missing '(' at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        if (!expectDataTypeOrIdent ) {
                            System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - Unexpected identifier at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        expectOperator = true;
                        expectOperandOrDataType = false;
                        if(inparent  && inforCond){
                           expectDataTypeOrIdent=true ;
                        }else {
                            expectDataTypeOrIdent=false ;
                        }
                        
                        expectsemiColum = true;
                        
                        break;
                    case LexicalAnalyzer.ADD_OP:
                    case LexicalAnalyzer.SUB_OP:
                    case LexicalAnalyzer.MULT_OP:
                    case LexicalAnalyzer.DIV_OP:
                    if(openparen==true){
                        System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - UMissing open ) " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        if (!expectOperator) {
                            System.out.println("Syntax analysis failed");
                            System.err.println("syntax_analyzer_error - Missing operand before operator at line " + LexicalAnalyzer.lineNum);
                            return;
                        } 
                        else {
                            expectOperator = false;
                            expectOperandOrDataType = true;
                        }
                        break;
                    case LexicalAnalyzer.EQUALS:
                    if(openparen==true){
                        System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - UMissing open ) " + LexicalAnalyzer.lineNum);
                            return;
                        }
                    if (!expectDataTypeOrIdent) { // equal op should not come when a data type or identifier is expected.
                        expectDataTypeOrIdent=false;
                        expectOperator = false;
                            expectOperandOrDataType = true; // After an assign op, we expect an operand (literal or identifier).
                        } 
                        
                        else {
                            System.err.println("Syntax error - Unexpected equals operator at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        break;

                    case LexicalAnalyzer.GREATER_THAN:
                    //System.out.println(expectDataTypeOrIdent);
                    if(openparen==true){
                        System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - UMissing open ) " + LexicalAnalyzer.lineNum);
                            return;
                        }
                    if (!expectDataTypeOrIdent) { // equal op should not come when a data type or identifier is expected.
                        expectDataTypeOrIdent=false;
                        expectOperator = false;
                            expectOperandOrDataType = true; // After an assign op, we expect an operand (literal or identifier).
                        } 
                        else {
                            System.err.println("Syntax error - Unexpected Greater than operator at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        break;

                   

                    case LexicalAnalyzer.LESS_THAN:
                    if(openparen==true){
                        System.out.println("Syntax analysis failed");
                            System.err.println("Syntax error - UMissing open ) " + LexicalAnalyzer.lineNum);
                            return;
                        }
                    if (!expectDataTypeOrIdent) { // equal op should not come when a data type or identifier is expected.
                        expectDataTypeOrIdent=false;
                        expectOperator = false;
                            expectOperandOrDataType = true; // After an assign op, we expect an operand (literal or identifier).
                        } 
                        else {
                            System.err.println("Syntax error - Unexpected less than operator at line " + LexicalAnalyzer.lineNum);
                            return;
                        }
                        break;



                   
                    case  LexicalAnalyzer.FOR :
                       if ( expectsemiColum ){
                        System.out.println("Syntax analysis failed");
                        System.err.println("syntax_analyzer_error - Missing semi colon at line " + LexicalAnalyzer.lineNum);
                        return ;
                       }
                       openparen=true;
                       expectOperator = false;
                        expectOperandOrDataType = false;
                        expectDataTypeOrIdent=false;
                        inforCond=true ;
                        //System.out.println("je suis venu  ici line " );
                       break ;

                       case  LexicalAnalyzer.WHILE :
                       if ( expectsemiColum ){
                        System.out.println("Syntax analysis failed");
                        System.err.println("syntax_analyzer_error - Missing semi colon at line " + LexicalAnalyzer.lineNum);
                        return ;
                       }
                       openparen=true;
                       expectOperator = false;
                        expectOperandOrDataType = false;
                        expectDataTypeOrIdent=false;
                        inforCond=false ;
                        //System.out.println("je suis venu  ici line " );
                       break ;

                       case  LexicalAnalyzer.IF :
                       if ( expectsemiColum ){
                        System.out.println("Syntax analysis failed");
                        System.err.println("syntax_analyzer_error - Missing semi colon at line " + LexicalAnalyzer.lineNum);
                        return ;
                       }
                       openparen=true;
                       expectOperator = false;
                        expectOperandOrDataType = false;
                        expectDataTypeOrIdent=false;
                        inforCond=false ;
                        //System.out.println("je suis venu  ici line " );
                       break ;

                    //case 
                    case LexicalAnalyzer.COMMENT:
                       if (!alreadyincrement && !expectsemiColum){
                          
                             //System.out.println("comment line "+lineNumber);
                       }
                       
                        
                        break;

                    
                }
                // for (int i=0;i<stack.size();i++){
                //     System.out.println(stack.indexOf(i));
                // }
                //System.out.println("here3");
                
            } while (LexicalAnalyzer.nextToken != LexicalAnalyzer.EOF);
            //while(true);
        for (int i=0;i<stack.size();i++){
                  //System.out.println(stack.indexOf(i));
                 }

            // if (!stack.isEmpty()) {
            //     System.err.println("Error - Unmatched symbol " + stack.pop() + "' at the end of code");
            // } else {
            //     System.out.println("Syntax analysis completed successfully!");
            // }
            
            while (!stack.isEmpty()) {
                int unmatchedToken = stack.pop();
                System.out.println("Syntax analysis failed.");
                //System.err.println("Error - Unmatched symbol " + unmatchedToken+" at the end of code  failed");
                stackempty=false;
            }

            if (stackempty ) {
                System.out.println("Syntax analysis succeed");
            }
        } catch (IOException e) {
            System.err.println("ERROR - cannot open input11.txt");
        }
    }
}
