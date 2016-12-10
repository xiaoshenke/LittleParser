package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 */

public class ClassOrInterfaceDotNode extends ASTNode {
    public ClassOrInterfaceDotNode() {
        type = NODE_TYPE_CLASSORINTERFACE_DOT;
    }

    public TypeArgumentsNode getTypeArgumentsNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof TypeArgumentsNode) {
                return (TypeArgumentsNode) node;
            }
        }
        return null;
    }
}
