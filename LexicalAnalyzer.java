import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LexicalAnalyzer {
    /* Global declarations */
    /* Variables */
    public static int charClass;
    public static StringBuilder lexeme = new StringBuilder(100);
    public static StringBuilder error = new StringBuilder(100);
    public static char nextChar;
    public static int lexLen;
    public static int token;
    public static int nextToken;
    public static BufferedReader in_fp;

    /* Character classes */
    public static final int EOF = -1;
    public static final int LETTER = 0;
    public static final int DIGIT = 1;
    public static final int UNDERSCORE = 2;
    public static final int UNKNOWN = 99;

    /* Token codes */
    public static final int FLOAT_LIT = 12;
    public static final int IDENT = 11;
    public static final int INT_LIT = 10;
    public static final int STR_LIT = 13;
    public static final int ASSIGN_OP = 20;
    public static final int ADD_OP = 21;
    public static final int SUB_OP = 22;
    public static final int MULT_OP = 23;
    public static final int DIV_OP = 24;
    public static final int LEFT_PAREN = 25;
    public static final int RIGHT_PAREN = 26;
    public static final int LEFT_BRACE = 27;
    public static final int RIGHT_BRACE = 28;
    public static final int SEMICOLON = 29;
    public static final int LESS_THAN = 30;
    public static final int GREATER_THAN = 31;
    public static final int EQUALS = 32;
    public static final int NOT_EQUALS = 33;
    public static final int AND_OP = 34;
    public static final int OR_OP = 35;
    public static final int IF = 36;
    public static final int ELSE = 37;
    public static final int FOR = 38;
    public static final int WHILE = 39;
    public static final int COMMENT = 40;
    public static final int QUESTION_MARK = 41;
    public static final int COLON = 42;
    public static final int LEFT_BRACKET = 43; 
    public static final int RIGHT_BRACKET = 44; 
   
    public static int lineNum = 1;  // start from 1 since we start at the first line



    public static void main(String[] args) {
        /* Open the input data file and process its contents */
        try {
            in_fp = new BufferedReader(new FileReader("input20.txt"));
            getChar();
            do {
                lex();
                
            } while (nextToken != EOF);
        } catch (IOException e) {
            System.err.println("ERROR - cannot open front.in");
        }
    }

    /* lookup - a function to lookup operators and keywords and return the token */
    public static void lookup(char ch) throws IOException {
        switch (ch) {
            case '(':
                addChar();
                nextToken = LEFT_PAREN;
                break;
            case ')':
                addChar();
                nextToken = RIGHT_PAREN;
                break;
            case '{':
                addChar();
                nextToken = LEFT_BRACE;
                break;
            case '}':
                addChar();
                nextToken = RIGHT_BRACE;
                break;
            case '+':
                addChar();
                nextToken = ADD_OP;
                break;
            case '-':
                addChar();
                nextToken = SUB_OP;
                break;
            case '*':
                addChar();
                nextToken = MULT_OP;
                break;
            case '/':
                addChar();
                getChar();
                nextToken = isComment() ? COMMENT : DIV_OP;
                break;
            case '=':
                addChar();
                getChar();
                if (nextChar == '=') {
                    addChar();
                    nextToken = EQUALS;
                } else {
                    nextToken = ASSIGN_OP;
                }
                break;
            case ';':
                addChar();
                nextToken = SEMICOLON;
                break;
            case '<':
                addChar();
                getChar();
                if (nextChar == '=') {
                    addChar();
                    nextToken = LESS_THAN;
                } else {
                    nextToken = LESS_THAN;
                }
                break;
            case '>':
                addChar();
                getChar();
                if (nextChar == '=') {
                    addChar();
                    nextToken = GREATER_THAN;
                } else {
                    nextToken = GREATER_THAN;
                }
                break;
            case '!':
                addChar();
                getChar();
                if (nextChar == '=') {
                    addChar();
                    nextToken = NOT_EQUALS;
                } else {
                    nextToken = UNKNOWN;
                }
                break;
            case '&':
                addChar();
                getChar();
                if (nextChar == '&') {
                    addChar();
                    nextToken = AND_OP;
                } else {
                    nextToken = UNKNOWN;
                }
                break;
            case '|':
                addChar();
                getChar();
                if (nextChar == '|') {
                    addChar();
                    nextToken = OR_OP;
                } else {
                    nextToken = UNKNOWN;
                }
                break;
            case '?':
                addChar();
                nextToken = QUESTION_MARK;
                break;
            case ':':
                addChar();
                nextToken = COLON;
                break;
            case '\"':
                addChar();
                getChar();
                while (nextChar != '\"') {
                    addChar();
                    getChar();
                }
                addChar(); // Include the closing double quote
                
                nextToken = STR_LIT;
                break;
            default:
                addChar();
                nextToken = EOF;
                break;
        }
    }

    public static boolean isComment() throws IOException {
        if (nextChar == '/') {
            // This is the beginning of a single-line comment
            while (nextChar != '\n')
                getChar(); // Ignore characters in the comment
            nextToken = COMMENT;
            lexeme = new StringBuilder("a single line comment");
        } else if (nextChar == '*') {
            // This is the beginning of a block comment
            addChar();
            getChar();
            while (!(nextChar == '*' && in_fp.read() == '/')) {
                getChar();
            }
            getChar(); // Consume the '/'
            nextToken = COMMENT;
            lexeme = new StringBuilder("a block comment");
        } else {
            return false;
        }
        return true;
    }

    /* addChar - a function to add nextChar to lexeme */
    public static void addChar() {
        if (lexLen <= 98) {
            lexeme.append(nextChar);
        } else {
            System.err.println("Error - lexeme is too long");
        }
    }

    /* getChar - a function to get the next character of input and determine its character class */
    public static void getChar() throws IOException {
        int nextCharInt = in_fp.read();
        if (nextCharInt != -1) {
            nextChar = (char) nextCharInt;
            if (Character.isLetter(nextChar))
                charClass = LETTER;
            else if (nextChar == '_')
                charClass = UNDERSCORE;
            else if (Character.isDigit(nextChar))
                charClass = DIGIT;
            else
                charClass = UNKNOWN;
        } else {
            charClass = EOF;
            //System.out.println('k');
        }
        if (nextChar == '\n') {
            lineNum++;
        }
    }

    /* getNonBlank - a function to call getChar until it returns a non-whitespace character */
    public static void getNonBlank() throws IOException {
        
        while (Character.isWhitespace(nextChar) && charClass != EOF)
        
            getChar();
            System.out.println(nextChar);
    }

    /* lex - a simple lexical analyzer for arithmetic expressions */
    public static void lex() throws IOException {
        lexLen = 0;
        lexeme = new StringBuilder();
        getNonBlank();
        String specialChar = "(+-/*<>)";
        System.out.println("charclass "+charClass);

        switch (charClass) {
            /* Parse identifiers or keywords */
            case LETTER:
            case UNDERSCORE:
                addChar();
                getChar();
                while (charClass == LETTER || charClass == DIGIT || charClass == UNDERSCORE) {
                    addChar();
                    getChar();
                }
                // Check if the lexeme is a keyword
                String lexemeStr = lexeme.toString();
                if (lexemeStr.equals("if"))
                    nextToken = IF;
                else if (lexemeStr.equals("else"))
                    nextToken = ELSE;
                else if (lexemeStr.equals("for"))
                    nextToken = FOR;
                else if (lexemeStr.equals("while"))
                    nextToken = WHILE;
                else if (charClass == UNKNOWN && !Character.isWhitespace(nextChar) && !specialChar.contains(String.valueOf(nextChar))) {
                    addChar();
                    error = new StringBuilder("Error - illegal identifier");
                    nextToken = EOF;
                } else
                    nextToken = IDENT;
                break;
            // Parse integer literals
            case DIGIT:
                addChar();
                getChar();
                while (charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                if (nextChar == '.') {
                    addChar(); // Include the decimal point
                    getChar();
                    while (charClass == DIGIT) {
                        addChar();
                        getChar();
                    }
                    nextToken = FLOAT_LIT;
                } else if (Character.isLetter(nextChar) || nextChar == '_') {
                    while (Character.isLetter(nextChar) || charClass == DIGIT || nextChar == '_') {
                        addChar();
                        getChar();
                    }
                    error = new StringBuilder("Error - illegal identifier");
                    nextToken = EOF;
                } else {
                    nextToken = INT_LIT;
                }
                break;
            case '\"':
                lookup(nextChar);
                getChar();
                break;
            /* Operators and punctuation */
            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;
            /* EOF */
            case EOF:
                nextToken = EOF;
                lexeme = new StringBuilder("EOF");
                System.out.println("here");
                break;
        }
        /* End of switch */
        System.out.printf("Next token is: %d, Next lexeme is %s", nextToken, lexeme);
        System.out.printf("\t%s\n", error);
    }
}
