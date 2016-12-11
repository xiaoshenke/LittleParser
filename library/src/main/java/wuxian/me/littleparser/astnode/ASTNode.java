package wuxian.me.littleparser.astnode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 6/12/2016.
 */

public class ASTNode {
    public static final int NODE_CLASS_DECLARATION = 1;
    public static final int NODE_EXTENDS_STATEMENT = 2;
    public static final int NODE_IMPLEMENTS_STATEMENT = 3;
    public static final int NODE_TYPE_PARAMETERS = 4; //<T,V>
    public static final int NODE_TYPE_PARAMETER = 5;
    public static final int NODE_TYPE_TYPEBOUND = 6;
    public static final int NODE_TYPE_TYPEBOUND_AND = 7;  //T & V --> interface
    public static final int NODE_TYPE_PARAMETERS_EXTENDS = 8;
    public static final int NODE_TYPE_TYPE = 9;
    public static final int NODE_TYPE_CLASSORINTERFACE = 10;
    public static final int NODE_TYPE_CLASSORINTERFACE_DOT = 12;
    public static final int NODE_TYPE_PRIMITIVE = 13;
    public static final int NODE_TYPE_TYPELIST = 14;
    public static final int NODE_TYPE_TYPELIST_COMMA = 15;
    public static final int NODE_TYPE_TYPEARGUMENTS = 16;
    public static final int NODE_TYPE_TYPEARGUMENTS_COMMA = 17;
    public static final int NODE_TYPE_TYPEARGUMENT = 18;
    public static final int NODE_TYPE_TYPEBOUND_EXTENDS = 19;
    public static final int NODE_TYPE_ARRAY = 20;

    public int type;
    public String name = "";
    public List<ASTNode> subNodes = new ArrayList<>();

    public ASTNode() {
        ;
    }

    protected String post() {
        String post = "";
        switch (type) {
            case NODE_TYPE_PARAMETERS:
            case NODE_TYPE_TYPEARGUMENTS:
                post = ">";
                break;
        }
        return post;
    }

    protected String pre() {
        String pre = "";
        switch (type) {
            case NODE_CLASS_DECLARATION:
                pre = "class " + name;
                break;
            case NODE_TYPE_PARAMETERS_EXTENDS:
            case NODE_TYPE_TYPEBOUND_EXTENDS:
            case NODE_EXTENDS_STATEMENT:
                pre = " extends ";
                break;
            case NODE_TYPE_ARRAY:
            case NODE_TYPE_PRIMITIVE:
            case NODE_TYPE_TYPEARGUMENT:
            case NODE_TYPE_PARAMETER:
            case NODE_TYPE_CLASSORINTERFACE:
                pre = name;
                break;
            case NODE_TYPE_CLASSORINTERFACE_DOT:
                pre = "." + name;
                break;
            case NODE_TYPE_PARAMETERS:
            case NODE_TYPE_TYPEARGUMENTS:
                pre = "<";
                break;
            case NODE_IMPLEMENTS_STATEMENT:
                pre = " implements ";
                break;
            case NODE_TYPE_TYPEARGUMENTS_COMMA:
            case NODE_TYPE_TYPELIST_COMMA:
                pre = " , ";
                break;
            case NODE_TYPE_TYPEBOUND_AND:
                pre = " & ";
                break;
        }
        return pre;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(pre());
        for (ASTNode node : subNodes) {
            b.append(node.toString());
        }
        b.append(post());
        return b.toString();
    }


    public String printWholeNode() {
        return toString();
    }
}
