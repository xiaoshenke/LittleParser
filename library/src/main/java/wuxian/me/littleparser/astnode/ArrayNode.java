package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 *
 * grammer --> '['']'
 */

public class ArrayNode extends ASTNode {
    public ArrayNode() {
        type = NODE_TYPE_ARRAY;
    }
}
