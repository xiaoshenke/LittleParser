package wuxian.me.littleparser;

import java.util.List;
import java.util.ArrayList;

import static wuxian.me.littleparser.Token.*;

/**
 * Created by wuxian on 1/12/2016.
 * 目前的匹配策略都是最长匹配 且没有回溯策略 没有error handling
 * 参考文章: https://zhuanlan.zhihu.com/p/21830284
 **/
class LittleParser {

    List<Token> tokens = new ArrayList<Token>();
    ASTNode root;//= new ASTNode();
    public LittleParser() {

    }

    public boolean matchClassString(String content) {
        root = new ASTNode();
        //root.type = ASTNode.NODE_CLASS_DECLARATION;

        lexical(content);
        return matchClassDeclaration(0, root) != -1;
    }

    //TODO: 能够表达这个内容的语法树？
    //class A<> extends B<> implements C,D
    public String getClassString() {
        return null;
    }

    //TODO:
    //A<>
    public String getClassName() {
        return null;
    }

    //TODO:
    //A
    public String getSimpleClassName() {
        return null;
    }

    //TODO:
    //B
    public String getSimpleSuperClassName() {
        return null;
    }

    //TODO:
    //B<>
    public String getSuperClassName() {
        return null;
    }

    //TODO:
    //C,D
    public String getInterfacesName() {
        return null;
    }

    private List<Token> getLexicalTokens() {
        return tokens;
    }

    private void lexical(String content) {
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
            //System.out.println(String.format("token type: %d,index: %d,content: %s ", token.type, i, token.obj.toString()));
        }
    }

    private boolean checkIndex(int index) {
        return index < tokens.size();
    }

    //class A extends B implements C,D
    //AST: type:'ClassDeclaration' name:'A' params:[{type:'extends' name:'B',params:?},{type:'implements',name:'C,D',params:?}]
    //classDeclaration:'class' Identifier typeParameters? ('extends' typeType)? ('implements' typeList)? classBody
    private int matchClassDeclaration(int current, ASTNode node) {
        //System.out.println("matchClassDeclaration index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }

        if (tokens.get(current).type != TOKEN_KEYWORDS || !((String) (tokens.get(current)).obj).equals("class")) {
            return -1;
        }
        node.type = ASTNode.NODE_CLASS_DECLARATION;  //设置一下type
        current++;

        int ret = matchIdentifier(current, node);
        if (ret == -1) {
            return -1;
        }

        current = ret;

        ASTNode typeParametersNode = new ASTNode();
        ret = matchTypeParameters(current + 1, typeParametersNode);
        if (ret != -1) {
            current = ret;
            node.subNodes.add(typeParametersNode); //添加子node if match success
        }
        current = ret == -1 ? current : ret;

        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            ret = matchTypeType(tmp + 1, node);
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
            ret = matchTypeList(tmp + 1, node);
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
    private int matchTypeParameters(int current, ASTNode node) {
        //System.out.println("matchTypeParameters index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type != TOKEN_TERMINAL || (char) (tokens.get(current).obj) != '<') {
            return -1;
        }

        current++;
        int ret = matchTypeParameter(current, node);
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
            ret = matchTypeParameter(tmp + 1, node);
            if (ret == -1) {
                return -1;
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
    private int matchTypeParameter(int current, ASTNode node) {
        //System.out.println("matchTypeParameter index:" + current);
        current = matchIdentifier(current, node);
        if (current == -1) {
            return -1;
        }

        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return current;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            int ret = matchTypeBound(tmp + 1, node);
            if (ret != -1) {
                current = ret;
            }
        }

        return current;
    }

    //typeList:   typeType (',' typeType)*
    private int matchTypeList(int current, ASTNode node) {
        //System.out.println("matchTypeList index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }

        current = matchTypeType(current, node);
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
                int ret = matchTypeType(tmp + 1, node);
                if (ret == -1) {
                    return -1;
                } else {
                    tmp = ret;
                    current = tmp;
                }
            }
        }
        return current;
    }

    //primitiveType:   'boolean'|'char'|'byte'|'short'|'int'|'long'|'float'|'double'
    private int matchPrimitiveType(int current) {
        //System.out.println("matchPrimitiveType index:" + current);
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
    private int matchTypeType(int current, ASTNode node) {
        //System.out.println("matchTypeType index:" + current);
        int next = matchClassOrInterfaceType(current, node);
        if (next == -1) { //匹配失败
            next = matchPrimitiveType(current);
        }
        return next;
    }

    //classOrInterfaceType:Identifier typeArguments? ('.' Identifier typeArguments? )*
    private int matchClassOrInterfaceType(int current, ASTNode node) {
        //System.out.println("matchClassOrInterfaceType index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        Token token = tokens.get(current);
        if (token.type == TOKEN_VARIABLE) {

            int next = matchTypeArguments(current + 1, node);
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
                int ret = matchIdentifier(tmp + 1, node);
                if (ret == -1) {
                    return -1;
                }
                tmp = ret;
                ret = matchTypeArguments(tmp + 1, node);
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
    private int matchTypeArguments(int current, ASTNode node) {
        //System.out.println("matchTypeArguments index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        Token token = tokens.get(current);
        if (token.type == TOKEN_TERMINAL && (char) (token.obj) == '<') {
            int ret = matchTypeArgument(current + 1, node);
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
                ret = matchTypeArgument(tmp + 1, node);
                if (ret == -1) {
                    return -1;
                }
                tmp = ret;
                current = tmp;
            }
            if (!checkIndex(current + 1)) {
                return -1;
            }
            if (tokens.get(current + 1).type != TOKEN_TERMINAL || (char) (tokens.get(current + 1).obj) != '>') {
                return -1;
            }
            current++;

            return current;

        } else {
            return -1;
        }
    }

    //typeParameter:   Identifier ('extends' typeBound)?
    private int matchTypeArgument(int current, ASTNode node) {
        current = matchIdentifier(current, node);
        if (current == -1) {
            return -1;
        }
        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }
        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            int ret = matchTypeBound(tmp + 1, node);
            if (ret == -1) {
                return -1;
            } else {
                tmp = ret;
                current = tmp;
            }
        }

        return current;
    }

    //typeBound:   typeType ('&' typeType)*
    private int matchTypeBound(int current, ASTNode node) {
        //System.out.println("matchTypeBound index:" + current);
        current = matchTypeType(current, node);
        if (current != -1) {
            int tmp = current;
            while (true) {
                if (!checkIndex(tmp + 1)) {
                    break;
                }
                if (tokens.get(tmp + 1).type == TOKEN_TERMINAL && (char) (tokens.get(tmp + 1).obj) == '&') {
                    tmp++;
                    int ret = matchTypeType(tmp + 1, node);
                    if (ret == -1) {
                        return -1;
                        //break;
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

    //AST: name:''
    //Identifier:   JavaLetter JavaLetterOrDigit*
    private int matchIdentifier(int current, ASTNode node) {
        //System.out.println("matchIdentifier index:" + current);
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type == TOKEN_VARIABLE) {
            node.name = (String) tokens.get(current).obj;  //设置一下name
            return current;
        } else {
            return -1;
        }
    }


    /*
    public static void main(String[] args) {
        String content = "int,Some_Class<T extends Other_class>";
        content = "A<D,B extends C<D>>";
        content = "class A<Integer,D extends E> extends B<Integer,D> {";
        LittleParser parser = new LittleParser();
        parser.lexical(content);

        //System.out.println("begin match: " + content);
        int ret = parser.matchClassDeclaration(0);
        if (ret == -1) {
            //System.out.println("match fail");
        } else if (ret == parser.getLexicalTokens().size() - 1) {
            //System.out.println("match success");
        }

        //System.out.println("match finish");
    }
    */

}