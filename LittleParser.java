
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

/**
* 目前的匹配策略都是最长匹配 且没有回溯策略 没有error handling...
**/
class LittleParser{

    static Set<Character> javaLetter;
    static Set<Character> javaLetterOrDigit;
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

    //typeList:   typeType (',' typeType)*
    //Todo: 这里返回的是一个list 还是只是一个int???? 还是说这里不会有模糊点？直接拿到最长匹配就可以了？
    //目前的理解是：只要拿到最长匹配字符串就可以了.....先这么做 不行就换方法
    int matchTypeList(int current){

        int pos = matchTypeType(current);  

        List<Integer> poslist = new ArrayList<Integer>();
        poslist.add(pos+1);

        int next = pos+1;
        while(true){
            if(tokens.get(next).type == ','){
                next++;
                int result = matchTypeType(next);
                if(result == -1){
                    ;//Todo error??????
                }
                //ret.add(result);
            } else{
                break;
            }

        }

        return pos;
    }

    //todo
    int matchPrimitiveType(int current){
        return -1;
    }

    //typeType:   classOrInterfaceType ('[' ']')* | primitiveType ('[' ']')*
    int matchTypeType(int current){
        int next = matchClassOrInterfaceType(current);
        if(next == -1){ //匹配失败
            ;
        } else {
            next = matchPrimitiveType(current);
        }
        return next;
    }

    //classOrInterfaceType:Identifier typeArguments? ('.' Identifier typeArguments? )*
    int matchClassOrInterfaceType(int current){
        Token token = tokens.get(current);
        if(token.type == TOKEN_VARIABLE){
        
            int next = matchTypeArguments(current+1);
            if(next != -1){
                current = next;
            }

            int tmp = current;
            while(true){
                if(tokens.get(tmp+1).type != TOKEN_TERMINAL || (char)(tokens.get(tmp+1).obj) != '.'){
                    break;
                }
                tmp++;
                int ret = matchIdentifier(tmp+1);
                if(ret == -1){  //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                    break;
                }
                tmp = ret;
                ret = matchTypeArguments(tmp+1);
                if(ret != -1){
                    tmp = ret;
                }
                current = tmp; //继续下一个loop匹配

            }

            return current;
        } else{
            return -1;
        }
    }

    //typeArguments:   '<' typeArgument (',' typeArgument)* '>' //java范型
    int matchTypeArguments(int current){
        Token token = tokens.get(current);
        if(token.type == TOKEN_TERMINAL && (char)(token.obj) == '<'){
            current++;
            int ret = matchTypeArgument(current+1);
            if(ret == -1){ //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                return -1;
            }
            current = ret;
            int tmp = current;
            while(true){
                if(tokens.get(tmp+1).type != TOKEN_TERMINAL || (char)(tokens.get(tmp+1).obj) != ','){
                    break;
                }
                tmp++;
                ret = matchTypeArgument(tmp+1);
                if(ret == -1){ //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                    break;
                }
                tmp = ret;
                current = tmp;
            }

            if(tokens.get(current+1).type != TOKEN_TERMINAL || (char)(tokens.get(current+1).obj) != '>'){ //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                return -1;
            }
            current++;

            return current;

        } else{
            return -1;
        }
    }

    //typeParameter:   Identifier ('extends' typeBound)?
    int matchTypeArgument(int current){
        current = matchIdentifier(current);
        if(current == -1){ //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
            return -1;
        }
        int tmp = current;
        if(tokens.get(tmp+1).type == TOKEN_KEYWORDS && tokens.get(tmp+1).obj.equals("extends")){
            tmp++;
            int ret = matchTypeBound(tmp+1);
            if(ret == -1){ //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                ;
            }else{
                tmp = ret;
                current = tmp;
            }
        }

        return current;
    }

    //typeBound:   typeType ('&' typeType)*
    int matchTypeBound(int current){
        current = matchTypeType(current);
        if(current != -1){
            int tmp = current;
            while(true){
                if(tokens.get(tmp+1).type == TOKEN_TERMINAL && (char)(tokens.get(tmp+1).obj) == '&'){
                    tmp++;
                    int ret = matchTypeType(tmp+1);
                    if(ret == -1){ //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                        break;
                    }else{
                        tmp = ret;
                        current = tmp;
                    }
                }else{
                    break;
                }
            }
            return current;
            
        } else{
            return -1;
        }
    
    }

    //Identifier:   JavaLetter JavaLetterOrDigit*
    int matchIdentifier(int current){
        if(tokens.get(current).type == TOKEN_TERMINAL){
            return current;
        } else {
            return -1;
        }
    }

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
        terminal.add('.');

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

    public List<Token> getLexicalTokens(){
        return tokens;
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
        content="A<D,B extends C<int>>";
		LittleParser parser = new LittleParser();
		parser.lexical(content);
        int ret = parser.matchTypeType(0);
        if(ret == -1){
            System.out.println("match fail");
        } else if(ret == parser.getLexicalTokens().size()-1){
            System.out.println("match success");
        }

        System.out.println("match finish");
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