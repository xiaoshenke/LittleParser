
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
class LittleParser {

    static Set<Character> javaLetter;
    static Set<Character> javaLetterOrDigit;
    static Set<Character> bracket;
    static Set<Character> terminal;
    static Set<String> keywords;
    static Set<String> primitives;

    static int TOKEN_NONE_TYPE = -1;
    static int TOKEN_JAVALETTER = 0;
    static int TOKEN_JAVALETTERORDIGIT = 1;
    static int TOKEN_BRACKET = 2;
    static int TOKEN_TERMINAL = 3;
    static int TOKEN_KEYWORDS = 4;
    static int TOKEN_VARIABLE = 5;
    static int TOKEN_PRIMITIVE = 6;

    private boolean checkIndex(int index) {
        return index < tokens.size();
    }

    //classDeclaration:'class' Identifier typeParameters? ('extends' typeType)? ('implements' typeList)? classBody
    int matchClassDeclaration(int current) {
        System.out.println("matchClassDeclaration index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }

        if (tokens.get(current).type != TOKEN_KEYWORDS || !((String) (tokens.get(current)).obj).equals("class")) {
            return -1;
        }

        current++;

        int ret = matchIdentifier(current);
        if (ret == -1) {
            return -1;
        }

        current = ret;

        ret = matchTypeParameters(current + 1);
        current = ret == -1 ? current : ret;

        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            ret = matchTypeType(tmp + 1);
            if (ret != -1) {
                current = ret;
            }
        }

        tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("implements")) {
            tmp++;
            ret = matchTypeList(tmp + 1);
            if (ret != -1) {
                current = ret;
            }
        }

        if (!checkIndex(current + 1)) {
            return -1;
        }

        if (tokens.get(current + 1).type == TOKEN_TERMINAL && (char) (tokens.get(current + 1).obj) == '{') {
            current++;
        }
        return current;
    }

    //typeParameters:   '<' typeParameter (',' typeParameter)* '>'
    int matchTypeParameters(int current) {
        System.out.println("matchTypeParameters index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type != TOKEN_TERMINAL || (char) (tokens.get(current).obj) != '<') {
            return -1;
        }

        current++;
        int ret = matchTypeParameter(current);
        if (ret == -1) {
            return -1;
        }
        current = ret;
        int tmp = current;
        while (true) {
            if (!checkIndex(tmp + 1)) {
                break;
            }

            if (tokens.get(tmp + 1).type != TOKEN_TERMINAL || (char) (tokens.get(tmp + 1).obj) != ',') {
                break;
            }
            tmp++;
            ret = matchTypeParameter(tmp + 1);
            if (ret == -1) { //Todo:throw an exception
                ;
            } else {
                tmp = ret;
                current = tmp;
            }
        }

        if (!checkIndex(current + 1)) {
            return -1;
        }

        if (tokens.get(current + 1).type != TOKEN_TERMINAL || (char) (tokens.get(current + 1).obj) != '>') {
            return -1;
        }
        current++;
        return current;
    }

    //typeParameter:   Identifier ('extends' typeBound)?
    int matchTypeParameter(int current) {
        System.out.println("matchTypeParameter index:" + current);
        current = matchIdentifier(current);
        if (current == -1) {
            return -1;
        }

        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return current;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            int ret = matchTypeBound(tmp + 1);
            if (ret != -1) {
                current = ret;
            }
        }

        return current;
    }

    //目前的策略：匹配到最长字符串
    //typeList:   typeType (',' typeType)*
    int matchTypeList(int current) {
        System.out.println("matchTypeList index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }

        current = matchTypeType(current);
        if (current == -1) {
            return -1;
        }
        int tmp = current;
        while (true) {
            if (!checkIndex(tmp + 1)) {
                break;
            }
            if (tokens.get(tmp + 1).type == TOKEN_TERMINAL && (char) (tokens.get(tmp + 1).obj) == ',') {
                tmp++;
                int ret = matchTypeType(tmp + 1);
                if (ret == -1) { //TODO:throw an exception
                    ;
                } else {
                    tmp = ret;
                    current = tmp;
                }
            }
        }
        return current;
    }

    //primitiveType:   'boolean'|'char'|'byte'|'short'|'int'|'long'|'float'|'double'
    int matchPrimitiveType(int current) {
        System.out.println("matchPrimitiveType index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type == TOKEN_PRIMITIVE) {
            return current;
        } else {
            return -1;
        }
    }

    //typeType:   classOrInterfaceType ('[' ']')* | primitiveType ('[' ']')*
    int matchTypeType(int current) {
        System.out.println("matchTypeType index:" + current);
        int next = matchClassOrInterfaceType(current);
        if (next == -1) { //匹配失败
            next = matchPrimitiveType(current);
        }
        return next;
    }

    //classOrInterfaceType:Identifier typeArguments? ('.' Identifier typeArguments? )*
    int matchClassOrInterfaceType(int current) {
        System.out.println("matchClassOrInterfaceType index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        Token token = tokens.get(current);
        if (token.type == TOKEN_VARIABLE) {

            int next = matchTypeArguments(current + 1);
            if (next != -1) {
                current = next;
            }

            int tmp = current;
            while (true) {
                if (!checkIndex(tmp + 1)) {
                    break;
                }
                if (tokens.get(tmp + 1).type != TOKEN_TERMINAL || (char) (tokens.get(tmp + 1).obj) != '.') {
                    break;
                }
                tmp++;
                int ret = matchIdentifier(tmp + 1);
                if (ret == -1) {  //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                    break;
                }
                tmp = ret;
                ret = matchTypeArguments(tmp + 1);
                if (ret != -1) {
                    tmp = ret;
                }
                current = tmp; //继续下一个loop匹配

            }

            return current;
        } else {
            return -1;
        }
    }

    //typeArguments:   '<' typeArgument (',' typeArgument)* '>' //java范型
    int matchTypeArguments(int current) {
        System.out.println("matchTypeArguments index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        Token token = tokens.get(current);
        if (token.type == TOKEN_TERMINAL && (char) (token.obj) == '<') {
            int ret = matchTypeArgument(current + 1);
            if (ret == -1) { //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                return -1;
            }
            current = ret;
            int tmp = current;
            while (true) {
                if (!checkIndex(tmp + 1)) {
                    break;
                }
                if (tokens.get(tmp + 1).type != TOKEN_TERMINAL || (char) (tokens.get(tmp + 1).obj) != ',') {
                    break;
                }
                tmp++;
                ret = matchTypeArgument(tmp + 1);
                if (ret == -1) { //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                    break;
                }
                tmp = ret;
                current = tmp;
            }
            if (!checkIndex(current + 1)) {
                return -1;
            }
            if (tokens.get(current + 1).type != TOKEN_TERMINAL || (char) (tokens.get(current + 1).obj) != '>') { //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                return -1;
            }
            current++;

            return current;

        } else {
            return -1;
        }
    }

    //typeParameter:   Identifier ('extends' typeBound)?
    int matchTypeArgument(int current) {
        current = matchIdentifier(current);
        if (current == -1) { //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
            return -1;
        }
        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }
        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            int ret = matchTypeBound(tmp + 1);
            if (ret == -1) { //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                ;
            } else {
                tmp = ret;
                current = tmp;
            }
        }

        return current;
    }

    //typeBound:   typeType ('&' typeType)*
    int matchTypeBound(int current) {
        System.out.println("matchTypeBound index:" + current);
        current = matchTypeType(current);
        if (current != -1) {
            int tmp = current;
            while (true) {
                if (!checkIndex(tmp + 1)) {
                    break;
                }
                if (tokens.get(tmp + 1).type == TOKEN_TERMINAL && (char) (tokens.get(tmp + 1).obj) == '&') {
                    tmp++;
                    int ret = matchTypeType(tmp + 1);
                    if (ret == -1) { //事实上这里是出错了,这里先挑出处理...Todo:throw an exception
                        break;
                    } else {
                        tmp = ret;
                        current = tmp;
                    }
                } else {
                    break;
                }
            }
            return current;

        } else {
            return -1;
        }

    }

    //Identifier:   JavaLetter JavaLetterOrDigit*
    int matchIdentifier(int current) {
        System.out.println("matchIdentifier index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type == TOKEN_VARIABLE) {
            return current;
        } else {
            return -1;
        }
    }

    static {
        javaLetter = new HashSet<Character>();
        javaLetter.add('a');
        javaLetter.add('b');
        javaLetter.add('c');
        javaLetter.add('d');
        javaLetter.add('e');
        javaLetter.add('f');
        javaLetter.add('g');
        javaLetter.add('h');
        javaLetter.add('i');
        javaLetter.add('j');
        javaLetter.add('k');
        javaLetter.add('l');
        javaLetter.add('m');
        javaLetter.add('n');
        javaLetter.add('o');
        javaLetter.add('p');
        javaLetter.add('q');
        javaLetter.add('r');
        javaLetter.add('s');
        javaLetter.add('t');
        javaLetter.add('u');
        javaLetter.add('v');
        javaLetter.add('w');
        javaLetter.add('x');
        javaLetter.add('y');
        javaLetter.add('z');
        javaLetter.add('A');
        javaLetter.add('B');
        javaLetter.add('C');
        javaLetter.add('D');
        javaLetter.add('E');
        javaLetter.add('F');
        javaLetter.add('G');
        javaLetter.add('H');
        javaLetter.add('I');
        javaLetter.add('J');
        javaLetter.add('K');
        javaLetter.add('L');
        javaLetter.add('M');
        javaLetter.add('N');
        javaLetter.add('O');
        javaLetter.add('P');
        javaLetter.add('Q');
        javaLetter.add('R');
        javaLetter.add('S');
        javaLetter.add('T');
        javaLetter.add('U');
        javaLetter.add('V');
        javaLetter.add('W');
        javaLetter.add('X');
        javaLetter.add('Y');
        javaLetter.add('Z');
        javaLetter.add('$');
        javaLetter.add('_');

        javaLetterOrDigit = new HashSet<Character>(javaLetter);
        javaLetterOrDigit.add('0');
        javaLetterOrDigit.add('1');
        javaLetterOrDigit.add('2');
        javaLetterOrDigit.add('3');
        javaLetterOrDigit.add('4');
        javaLetterOrDigit.add('5');
        javaLetterOrDigit.add('6');
        javaLetterOrDigit.add('7');
        javaLetterOrDigit.add('8');
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
        terminal.add('{');

        keywords = new HashSet<String>();
        keywords.add("class");
        keywords.add("extends");
        keywords.add("implements");
        //keywords.add("int");

        //primitiveType:'boolean'|'char'|'byte'|'short'|'int'|'long'|'float'|'double'
        primitives = new HashSet<String>();
        primitives.add("boolean");
        primitives.add("char");
        primitives.add("byte");
        primitives.add("short");
        primitives.add("int");
        primitives.add("long");
        primitives.add("float");
        primitives.add("double");

    }

    List<Token> tokens = new ArrayList<Token>();

    public LittleParser() {
        ;
    }

    public List<Token> getLexicalTokens() {
        return tokens;
    }

    public void lexical(String content) {
        tokens.clear();
        char lookahead;
        for (int i = 0; i < content.length(); ) {
            char now = content.charAt(i);
            if (terminal.contains(now)) {
                tokens.add(new Token(TOKEN_TERMINAL, now));
                i++;
                continue;
            } else if (bracket.contains(now)) {
                i++;
                continue;
            } else {
                int j = i;
                StringBuilder builder = new StringBuilder();
                builder.append(now);
                if (j + 1 == content.length()) {
                    tokens.add(new Token(TOKEN_VARIABLE, builder.toString()));
                    break;
                }
                lookahead = content.charAt(j + 1);
                while (!bracket.contains(lookahead) && !terminal.contains(lookahead)) {
                    j++;
                    builder.append(lookahead);

                    if (j + 1 == content.length()) {
                        break;
                    }
                    lookahead = content.charAt(j + 1);
                }
                if (keywords.contains(builder.toString())) {
                    tokens.add(new Token(TOKEN_KEYWORDS, builder.toString()));
                } else if (primitives.contains(builder.toString())) {
                    tokens.add(new Token(TOKEN_PRIMITIVE, builder.toString()));
                } else {
                    tokens.add(new Token(TOKEN_VARIABLE, builder.toString()));
                }
                i = j + 1;
            }
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            System.out.println(String.format("token type: %d,index: %d,content: %s ", token.type, i, token.obj.toString()));
        }
    }

    //goal is to implement TypeList --> (int,Some-Class<T extends Other-class>)
    public static void main(String[] args) {
        String content = "int,Some_Class<T extends Other_class>";
        content = "A<D,B extends C<D>>";
        content = "class A<Integer,D extends E> extends B<Integer,D> {";
        LittleParser parser = new LittleParser();
        parser.lexical(content);

        System.out.println("begin match: " + content);
        int ret = parser.matchClassDeclaration(0);
        if (ret == -1) {
            System.out.println("match fail");
        } else if (ret == parser.getLexicalTokens().size() - 1) {
            System.out.println("match success");
        }

        System.out.println("match finish");
    }

    private static class Token {
        int type = TOKEN_NONE_TYPE;
        Object obj;

        Token(int type, Object content) {
            this.type = type;
            this.obj = content;
        }
    }
}