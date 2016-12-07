package wuxian.me.littleparser;

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
    public static final int NODE_TYPE_ARGUMENTS = 11;
    public static final int NODE_TYPE_CLASSORINTERFACE_DOT = 12;

    public static final String NAME_TYPE_PARAMETERS = "NAME_TYPE_PARAMETERS";


    public int type;
    public String name = "";
    public List<ASTNode> subNodes = new ArrayList<>();

    public ASTNode() {
        ;
    }
}
