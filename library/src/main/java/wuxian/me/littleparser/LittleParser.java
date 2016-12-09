package wuxian.me.littleparser;
import java.util.List;
import java.util.ArrayList;

import wuxian.me.littleparser.astnode.ASTNode;

import static wuxian.me.littleparser.Token.*;

/**
 * Created by wuxian on 1/12/2016.
 * 目前的匹配策略都是最长匹配 且没有回溯策略 没有error handling
 * 参考文章: https://zhuanlan.zhihu.com/p/21830284
 **/
public class LittleParser {

    List<Token> tokens = new ArrayList<Token>();
    ASTNode root;

    public LittleParser() {

    }

    public ASTNode getParsedASTNode() {
        return root;
    }

    public boolean matchClassString(String content) {
        root = new ASTNode();

        lexical(content);
        return matchClassDeclaration(0, root) != -1;
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
        }
    }

    private boolean checkIndex(int index) {
        return index < tokens.size();
    }

    //class A extends B implements C,D
    //AST: type:'ClassDeclaration' name:'A' params:[{type:'extends' name:'B',params:?},{type:'implements',name:'C,D',params:?}]
    //classDeclaration:'class' Identifier typeParameters? ('extends' typeType)? ('implements' typeList)? classBody
    private int matchClassDeclaration(int current, ASTNode node) {
        if (!checkIndex(current)) {
            return -1;
        }

        if (tokens.get(current).type != TOKEN_KEYWORDS || !((String) (tokens.get(current)).obj).equals("class")) {
            return -1;
        }
        node.type = ASTNode.NODE_CLASS_DECLARATION;
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
            node.subNodes.add(typeParametersNode);
        }

        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            ASTNode extendsNode = new ASTNode();
            extendsNode.type = ASTNode.NODE_EXTENDS_STATEMENT;
            ASTNode type = new ASTNode();
            ret = matchTypeType(tmp + 1, type);
            if (ret != -1) {
                current = ret;
                extendsNode.subNodes.add(type);
                node.subNodes.add(extendsNode);
            }
        }

        tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("implements")) {
            tmp++;
            ASTNode implement = new ASTNode();
            implement.type = ASTNode.NODE_IMPLEMENTS_STATEMENT;
            ASTNode list = new ASTNode();
            ret = matchTypeList(tmp + 1, list);
            if (ret != -1) {
                current = ret;
                implement.subNodes.add(list);
                node.subNodes.add(implement);
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

    //AST: type:'match type parameters'
    //typeParameters:   '<' typeParameter (',' typeParameter)* '>'
    private int matchTypeParameters(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_PARAMETERS;
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type != TOKEN_TERMINAL || (char) (tokens.get(current).obj) != '<') {
            return -1;
        }

        ASTNode typeParameterNode = new ASTNode();
        current++;
        int ret = matchTypeParameter(current, typeParameterNode);
        if (ret == -1) {
            return -1;
        }
        node.subNodes.add(typeParameterNode);
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
            ASTNode typeNode = new ASTNode();
            ret = matchTypeParameter(tmp + 1, typeNode);
            if (ret == -1) {
                return -1;
            } else {
                tmp = ret;
                current = tmp;
                typeParameterNode.subNodes.add(typeNode);
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
        node.type = ASTNode.NODE_TYPE_PARAMETER;
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
            ASTNode typeParameterExtendsNode = new ASTNode();
            typeParameterExtendsNode.type = ASTNode.NODE_TYPE_PARAMETERS_EXTENDS;
            ASTNode typeBoundNode = new ASTNode();
            int ret = matchTypeBound(tmp + 1, typeBoundNode);
            if (ret != -1) {
                current = ret;

                typeParameterExtendsNode.subNodes.add(typeBoundNode);
                node.subNodes.add(typeParameterExtendsNode);
            }
        }

        return current;
    }

    //typeList:   typeType (',' typeType)*
    private int matchTypeList(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_TYPELIST;
        if (!checkIndex(current)) {
            return -1;
        }

        ASTNode typeNode = new ASTNode();
        current = matchTypeType(current, typeNode);
        if (current == -1) {
            return -1;
        }
        node.subNodes.add(typeNode);
        int tmp = current;
        while (true) {
            if (!checkIndex(tmp + 1)) {
                break;
            }
            ASTNode commaNode = new ASTNode();
            commaNode.type = ASTNode.NODE_TYPE_TYPELIST_COMMA;
            if (tokens.get(tmp + 1).type == TOKEN_TERMINAL && (char) (tokens.get(tmp + 1).obj) == ',') {
                tmp++;
                ASTNode typenode = new ASTNode();
                int ret = matchTypeType(tmp + 1, typenode);
                if (ret == -1) {
                    return -1;
                } else {
                    tmp = ret;
                    current = tmp;
                    commaNode.subNodes.add(typenode);
                    node.subNodes.add(commaNode);
                }
            }
        }
        return current;
    }

    //primitiveType:   'boolean'|'char'|'byte'|'short'|'int'|'long'|'float'|'double'
    private int matchPrimitiveType(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_PRIMITIVE;
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type == TOKEN_PRIMITIVE) {
            node.name = (String) tokens.get(current).obj;
            return current;
        } else {
            return -1;
        }
    }

    //[]
    private int matchArrayType(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_ARRAY;
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type != Token.TOKEN_TERMINAL || (char) tokens.get(current).obj != '[') {
            return -1;
        }

        current++;

        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type != Token.TOKEN_TERMINAL || (char) tokens.get(current).obj != ']') {
            return -1;
        }
        node.name = "[]";

        return current;
    }


    //typeType:   classOrInterfaceType ('[' ']')* | primitiveType ('[' ']')*
    private int matchTypeType(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_TYPE;
        ASTNode subNode = new ASTNode();
        int next = matchClassOrInterfaceType(current, node);
        if (next != -1) {
            node.subNodes.add(subNode);
        } else {
            next = matchPrimitiveType(current, node);
            if (next != -1) {
                node.subNodes.add(subNode);
            } else {
                return -1;
            }
        }
        int tmp = next;
        while (true) {
            ASTNode array = new ASTNode();
            int ret = matchArrayType(tmp + 1, array);
            if (ret != -1) {
                tmp = ret;
                node.subNodes.add(array);
            } else {
                break;
            }
        }

        return next;
    }

    //classOrInterfaceType:Identifier typeArguments? ('.' Identifier typeArguments? )*
    private int matchClassOrInterfaceType(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_CLASSORINTERFACE;
        if (!checkIndex(current)) {
            return -1;
        }
        int ret = matchIdentifier(current, node);
        if (ret != -1) {
            ASTNode typeArgumentsNode = new ASTNode();
            int next = matchTypeArguments(current + 1, typeArgumentsNode);
            if (next != -1) {
                current = next;
                node.subNodes.add(typeArgumentsNode);
            }

            int tmp = current;
            while (true) {
                if (!checkIndex(tmp + 1)) {
                    break;
                }
                ASTNode dotNode = new ASTNode();
                dotNode.type = ASTNode.NODE_TYPE_CLASSORINTERFACE_DOT; //.Identifier typeArguments
                if (tokens.get(tmp + 1).type != TOKEN_TERMINAL || (char) (tokens.get(tmp + 1).obj) != '.') {
                    break;
                }
                tmp++;
                ret = matchIdentifier(tmp + 1, dotNode);
                if (ret == -1) {
                    return -1;
                }
                tmp = ret;
                ASTNode argumentNode = new ASTNode();
                ret = matchTypeArguments(tmp + 1, argumentNode);
                if (ret != -1) {
                    tmp = ret;
                    dotNode.subNodes.add(argumentNode);
                    node.subNodes.add(dotNode);
                }
                current = tmp;

            }

            return current;
        } else {
            return -1;
        }
    }

    //typeArguments:   '<' typeArgument (',' typeArgument)* '>' //java范型
    private int matchTypeArguments(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_TYPEARGUMENTS;
        if (!checkIndex(current)) {
            return -1;
        }
        Token token = tokens.get(current);
        if (token.type == TOKEN_TERMINAL && (char) (token.obj) == '<') {
            ASTNode argNode = new ASTNode();
            int ret = matchTypeArgument(current + 1, node);
            if (ret == -1) {
                return -1;
            }
            node.subNodes.add(argNode);
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
                ASTNode argmentComma = new ASTNode();
                argmentComma.type = ASTNode.NODE_TYPE_TYPEARGUMENTS_COMMA;
                ASTNode arg = new ASTNode();
                ret = matchTypeArgument(tmp + 1, node);
                if (ret == -1) {
                    return -1;
                }
                argmentComma.subNodes.add(arg);
                node.subNodes.add(argmentComma);
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
        node.type = ASTNode.NODE_TYPE_TYPEARGUMENT;

        current = matchIdentifier(current, node);

        ASTNode extend = new ASTNode();
        extend.type = ASTNode.NODE_TYPE_TYPEBOUND_EXTENDS;

        if (current == -1) {
            return -1;
        }
        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return -1;
        }
        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            ASTNode bound = new ASTNode();
            int ret = matchTypeBound(tmp + 1, node);
            if (ret == -1) {
                return -1;
            } else {
                tmp = ret;
                current = tmp;
                extend.subNodes.add(bound);
                node.subNodes.add(extend);
            }
        }

        return current;
    }

    //typeBound:   typeType ('&' typeType)*
    private int matchTypeBound(int current, ASTNode node) {
        node.type = ASTNode.NODE_TYPE_TYPEBOUND;
        ASTNode typetypeNode = new ASTNode();
        current = matchTypeType(current, typetypeNode);
        if (current != -1) {
            node.subNodes.add(typetypeNode);
            int tmp = current;
            while (true) {
                if (!checkIndex(tmp + 1)) {
                    break;
                }
                if (tokens.get(tmp + 1).type == TOKEN_TERMINAL && (char) (tokens.get(tmp + 1).obj) == '&') {
                    tmp++;
                    ASTNode boundAndNode = new ASTNode();
                    boundAndNode.type = ASTNode.NODE_TYPE_TYPEBOUND_AND;
                    ASTNode typeNode = new ASTNode();
                    int ret = matchTypeType(tmp + 1, typeNode);
                    if (ret == -1) {
                        return -1;
                    } else {
                        tmp = ret;
                        current = tmp;
                        boundAndNode.subNodes.add(typeNode);
                        node.subNodes.add(boundAndNode);
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
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type == TOKEN_VARIABLE) {
            node.name = (String) tokens.get(current).obj;
            return current;
        } else {
            return -1;
        }
    }

}