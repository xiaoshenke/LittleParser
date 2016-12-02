
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by wuxian on 1/12/2016.
 * <p>
 * parse java class
 * <p>
 * classDeclaration:   'class' Identifier typeParameters? ('extends' typeType)? ('implements' typeList)? classBody
 * typeParameters:   '<' typeParameter (',' typeParameter)* '>'
 * typeParameter:   Identifier ('extends' typeBound)?
 * typeBound:   typeType ('&' typeType)*
 * typeType:   classOrInterfaceType ('[' ']')*|   primitiveType ('[' ']')*
 * classOrInterfaceType:   Identifier typeArguments? ('.' Identifier typeArguments? )*
 * primitiveType:   'boolean'|'char'|'byte'|'short'|'int'|'long'|'float'|'double'
 * Identifier:   JavaLetter JavaLetterOrDigit*
 * typeArguments:   '<' typeArgument (',' typeArgument)* '>'
 * typeArgument:   typeType|'?' (('extends' | 'super') typeType)?
 * <p>
 * typeList:   typeType (',' typeType)*
 * <p>
 * JavaLetterOrDigit:   [a-zA-Z0-9$_]|~[\u0000-\u007F\uD800-\uDBFF]{Character.isJavaIdentifierPart(_input.LA(-1))}?
 * |[\uD800-\uDBFF] [\uDC00-\uDFFF]{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
 * <p>
 * JavaLetter:   [a-zA-Z$_]|~[\u0000-\u007F\uD800-\uDBFF]{Character.isJavaIdentifierStart(_input.LA(-1))}?
 * ＊   |[\uD800-\uDBFF] [\uDC00-\uDFFF]{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
 */

class LittleParser{

    static Set<Character> javaLetter;// = new HashSet<char>();
    static Set<Character> javaLetterOrDigit;// = new HashSet
    static Set<Character> bracket;
    static Set<Character> terminal;
    static Set<String> keywords;

    static int TOKEN_NONE_TYPE = -1;
    static int TOKEN_JAVALETTER = 0;
    static int TOKEN_JAVALETTERORDIGIT = 1;
    static int TOKEN_BRACKET = 2;
    static int TOKEN_TERMINAL = 3;
    static int TOKEN_KEYWORDS = 4;
    static int TOKEN_VARIABLE = 5;

    static{
    	javaLetter = new HashSet<Character>();
    	javaLetter.add('a');javaLetter.add('b');javaLetter.add('c');javaLetter.add('d');
    	javaLetter.add('e');javaLetter.add('f');javaLetter.add('g');javaLetter.add('h');
    	javaLetter.add('i');javaLetter.add('j');javaLetter.add('k');javaLetter.add('l');
    	javaLetter.add('m');javaLetter.add('n');javaLetter.add('o');javaLetter.add('p');
    	javaLetter.add('q');javaLetter.add('r');javaLetter.add('s');javaLetter.add('t');
    	javaLetter.add('u');javaLetter.add('v');javaLetter.add('w');javaLetter.add('x');
    	javaLetter.add('y');javaLetter.add('z');javaLetter.add('A');javaLetter.add('B');
    	javaLetter.add('C');javaLetter.add('D');javaLetter.add('E');javaLetter.add('F');
    	javaLetter.add('G');javaLetter.add('H');javaLetter.add('I');javaLetter.add('J');
    	javaLetter.add('K');javaLetter.add('L');javaLetter.add('M');javaLetter.add('N');
    	javaLetter.add('O');javaLetter.add('P');javaLetter.add('Q');javaLetter.add('R');
    	javaLetter.add('S');javaLetter.add('T');javaLetter.add('U');javaLetter.add('V');
    	javaLetter.add('W');javaLetter.add('X');javaLetter.add('Y');javaLetter.add('Z');
    	javaLetter.add('$');javaLetter.add('_');

    	javaLetterOrDigit = new HashSet<Character>(javaLetter);
    	javaLetterOrDigit.add('0');javaLetterOrDigit.add('1');javaLetterOrDigit.add('2');
    	javaLetterOrDigit.add('3');javaLetterOrDigit.add('4');javaLetterOrDigit.add('5');
    	javaLetterOrDigit.add('6');javaLetterOrDigit.add('7');javaLetterOrDigit.add('8');
    	javaLetterOrDigit.add('9');

    	bracket = new HashSet<Character>();
    	bracket.add('\n');
    	bracket.add('\t');
    	bracket.add('\b');
    	bracket.add('\r');
        bracket.add(' ');

    	terminal = new HashSet<Character>();
    	terminal.add('<');
    	terminal.add('>');
    	terminal.add(',');

    	keywords = new HashSet<String>();
    	keywords.add("class");
    	keywords.add("extends");
    	keywords.add("implements");
    	keywords.add("int");

    }

    List<Token> tokens = new ArrayList<Token>();
 	public LittleParser(){
 		;
 	}

 	public void lexical(String content){
 		tokens.clear();
 		char lookahead;
 		for(int i=0;i<content.length();){
 			char now = content.charAt(i);
 			if(terminal.contains(now)){
 				tokens.add(new Token(TOKEN_TERMINAL,now));
 				i++;
 				continue;
 			}else if(bracket.contains(now)){
 				i++;
 				continue;
 			}else{
 				int j=i;
 				StringBuilder builder = new StringBuilder();
 				builder.append(now);
 				if(j+1 == content.length()){
 					tokens.add(new Token(TOKEN_VARIABLE,builder.toString()));
 					break;
 				}
 				lookahead = content.charAt(j+1);
 				while(!bracket.contains(lookahead) && !terminal.contains(lookahead)){				
 					j++;
 					builder.append(lookahead);
 					
 					if(j+1 == content.length()){
 						break;
 					}
 					lookahead = content.charAt(j+1);
 				}
 				if(keywords.contains(builder.toString())){
 					tokens.add(new Token(TOKEN_KEYWORDS,builder.toString()));
 				}else{
 					tokens.add(new Token(TOKEN_VARIABLE,builder.toString()));
 				}
 				i = j+1;
 			}
 		}
 		for(Token token:tokens){
 			System.out.println(String.format("token type: %d,content: %s ",token.type,token.obj.toString()));
 		}
 	}



 	//goal is to implement TypeList --> (int,Some-Class<T extends Other-class>)
	public static void main(String[] args){
		String content = "int,Some_Class<T extends Other_class>";
		LittleParser parser = new LittleParser();
		parser.lexical(content);
	}

	private static class Token{
		int type = TOKEN_NONE_TYPE;
		Object obj;

		Token(int type,Object content){
			this.type = type;
			this.obj = content;
		} 
	}
}