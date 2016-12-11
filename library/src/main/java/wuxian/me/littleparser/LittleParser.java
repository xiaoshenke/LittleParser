package wuxian.me.littleparser;
import java.util.List;
import java.util.ArrayList;

import wuxian.me.littleparser.astnode.ASTNode;
import wuxian.me.littleparser.astnode.ArrayNode;
import wuxian.me.littleparser.astnode.ClassDeclareNode;
import wuxian.me.littleparser.astnode.ClassExtendsNode;
import wuxian.me.littleparser.astnode.ClassImplementsNode;
import wuxian.me.littleparser.astnode.ClassOrInterfaceDotNode;
import wuxian.me.littleparser.astnode.ClassOrInterfaceNode;
import wuxian.me.littleparser.astnode.PrimitiveNode;
import wuxian.me.littleparser.astnode.TypeArgumentsNode;
import wuxian.me.littleparser.astnode.TypeListCommaNode;
import wuxian.me.littleparser.astnode.TypeListNode;
import wuxian.me.littleparser.astnode.TypeNode;
import wuxian.me.littleparser.astnode.TypeParametersNode;

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
        root = new ClassDeclareNode();

        lexical(content);
        return matchClassDeclaration(0, root) != -1;
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

    }

    private boolean checkIndex(int index) {
        return index < tokens.size();
    }

    //classDeclaration:'class' Identifier typeParameters? ('extends' typeType)? ('implements' typeList)? #classBody#这里的classbody就不要了
    private int matchClassDeclaration(int current, ASTNode node) {
        if (!checkIndex(current)) {
            return -1;
        }
        if (tokens.get(current).type != TOKEN_KEYWORDS || !((String) (tokens.get(current)).obj).equals("class")) {
            return -1;
        }
        current++;

        int ret = matchIdentifier(current, node);
        if (ret == -1) {
            return -1;
        }

        current = ret;

        ASTNode typeParametersNode;
        typeParametersNode = new TypeParametersNode();
        ret = matchTypeParameters(current + 1, typeParametersNode);
        if (ret != -1) {
            current = ret;
            node.subNodes.add(typeParametersNode);
        }

        int tmp = current;
        if (!checkIndex(tmp + 1)) {
            return current;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("extends")) {
            tmp++;
            ASTNode extendsNode;
            extendsNode = new ClassExtendsNode();
            ASTNode type;
            type = new TypeNode();
            ret = matchTypeType(tmp + 1, type);
            if (ret != -1) {
                current = ret;
                extendsNode.subNodes.add(type);
                node.subNodes.add(extendsNode);
            }
        }

        tmp = current;
        if (!checkIndex(tmp + 1)) {
            return current;
        }

        if (tokens.get(tmp + 1).type == TOKEN_KEYWORDS && ((String) (tokens.get(tmp + 1).obj)).equals("implements")) {
            tmp++;
            ASTNode implement;
            implement = new ClassImplementsNode();

            ASTNode list;
            list = new TypeListNode();
            ret = matchTypeList(tmp + 1, list);
            if (ret != -1) {
                current = ret;
                implement.subNodes.add(list);
                node.subNodes.add(implement);
            }
        }


        if (!checkIndex(current + 1)) {
            return current;
        }
        if (tokens.get(current + 1).type == TOKEN_TERMINAL && (char) (tokens.get(current + 1).obj) == '{') {
            current++;
        } else {  //一个class declare string一定是以'{'结尾,要么就是后面没有字符串了
            return -1;
        }
        return current;
    }

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
        if (!checkIndex(current)) {
            return -1;
        }

        ASTNode typeNode;
        typeNode = new TypeNode();
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
            ASTNode commaNode;
            commaNode = new TypeListCommaNode();

            if (tokens.get(tmp + 1).type == TOKEN_TERMINAL && (char) (tokens.get(tmp + 1).obj) == ',') {
                tmp++;
                ASTNode typenode;
                typenode = new TypeNode();
                int ret = matchTypeType(tmp + 1, typenode);
                if (ret == -1) {
                    return -1;
                } else {
                    tmp = ret;
                    current = tmp;
                    commaNode.subNodes.add(typenode);
                    node.subNodes.add(commaNode);
                }
            } else {
                break;
            }
        }
        return current;
    }

    //primitiveType:   'boolean'|'char'|'byte'|'short'|'int'|'long'|'float'|'double'
    private int matchPrimitiveType(int current, ASTNode node) {
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
        ASTNode subNode;
        subNode = new ClassOrInterfaceNode();
        int next = matchClassOrInterfaceType(current, subNode);
        if (next != -1) {
            node.subNodes.add(subNode);
        } else {
            subNode = new PrimitiveNode();
            next = matchPrimitiveType(current, subNode);
            if (next != -1) {
                node.subNodes.add(subNode);
            } else {
                return -1;
            }
        }
        int tmp = next;
        while (true) {
            ASTNode array;
            array = new ArrayNode();
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

    //A<T>.B<V> --> inner class...
    //classOrInterfaceType:Identifier typeArguments? ('.' Identifier typeArguments? )*
    private int matchClassOrInterfaceType(int current, ASTNode node) {
        if (!checkIndex(current)) {
            return -1;
        }
        int ret = matchIdentifier(current, node);
        if (ret != -1) {
            ASTNode typeArgumentsNode;
            typeArgumentsNode = new TypeArgumentsNode();
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
                ASTNode dotNode;
                dotNode = new ClassOrInterfaceDotNode();//.Identifier typeArguments
                if (tokens.get(tmp + 1).type != TOKEN_TERMINAL || (char) (tokens.get(tmp + 1).obj) != '.') {
                    break;
                }
                tmp++;
                ret = matchIdentifier(tmp + 1, dotNode);
                if (ret == -1) {
                    return -1;
                }
                tmp = ret;
                ASTNode argumentNode;
                argumentNode = new TypeArgumentsNode();
                ret = matchTypeArguments(tmp + 1, argumentNode);
                if (ret != -1) {
                    tmp = ret;
                    dotNode.subNodes.add(argumentNode);
                    node.subNodes.add(dotNode);
                } else {
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
        if (!checkIndex(current)) {
            return -1;
        }
        Token token = tokens.get(current);
        if (token.type == TOKEN_TERMINAL && (char) (token.obj) == '<') {
            ASTNode argNode = new ASTNode();
            int ret = matchTypeArgument(current + 1, argNode);
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
        ASTNode typetypeNode;
        typetypeNode = new TypeNode();
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
                    ASTNode typeNode;
                    typeNode = new TypeNode();
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

        }
        return -1;
    }

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