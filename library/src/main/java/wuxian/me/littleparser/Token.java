package wuxian.me.littleparser;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuxian on 5/12/2016.
 */

public class Token {
    public static Set<Character> javaLetter;
    public static Set<Character> javaLetterOrDigit;
    public static Set<Character> bracket;
    public static Set<Character> terminal;
    public static Set<String> keywords;
    public static Set<String> primitives;

    static int TOKEN_NONE_TYPE = -1;
    static int TOKEN_JAVALETTER = 0;
    static int TOKEN_JAVALETTERORDIGIT = 1;
    static int TOKEN_BRACKET = 2;
    static int TOKEN_TERMINAL = 3;
    static int TOKEN_KEYWORDS = 4;
    static int TOKEN_VARIABLE = 5;
    static int TOKEN_PRIMITIVE = 6;

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
        terminal.add('[');
        terminal.add(']');

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



    public int type = TOKEN_NONE_TYPE;
    public Object obj;

    public Token(int type, Object content) {
        this.type = type;
        this.obj = content;
    }
}
