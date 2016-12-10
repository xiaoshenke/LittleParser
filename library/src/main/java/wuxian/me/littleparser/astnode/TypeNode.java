package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 */

public class TypeNode extends ASTNode {
    private PrimitiveNode primitiveNode;

    public TypeNode() {
        type = NODE_TYPE_TYPE;
    }

    public boolean isPrimitive() {
        for (ASTNode node : subNodes) {
            if (node instanceof PrimitiveNode) {
                primitiveNode = (PrimitiveNode) node;
                return true;
            }
        }
        return false;
    }

    public PrimitiveNode getPrimitiveNode() {
        if (isPrimitive()) {
            return primitiveNode;
        } else {
            return null;
        }
    }

    public ClassOrInterfaceNode getClassOrInterfaceNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof ClassOrInterfaceNode) {
                return (ClassOrInterfaceNode) node;
            }
        }
        return null;
    }
}
