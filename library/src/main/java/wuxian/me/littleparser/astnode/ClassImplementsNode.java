package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 9/12/2016.
 */

public class ClassImplementsNode extends ASTNode {

    public ClassImplementsNode() {
        type = NODE_IMPLEMENTS_STATEMENT;
    }

    public TypeListNode getTypelistNode() {
        if (subNodes.size() == 0) {
            return null;
        }

        return (TypeListNode) subNodes.get(0);
    }
}
