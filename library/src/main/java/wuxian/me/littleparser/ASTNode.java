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


    public int type;
    public String name;
    public List<ASTNode> subNodes = new ArrayList<>();

    public ASTNode() {
        ;
    }
}
