package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 9/12/2016.
 *
 * grammer --> 'extends' typeType
 */

public class ClassExtendsNode extends ASTNode {

    public ClassExtendsNode() {
        type = NODE_EXTENDS_STATEMENT;
    }

    public TypeNode getTypeNode() {
        if (subNodes.size() == 0) {
            return null;
        }
        return (TypeNode) subNodes.get(0);
    }

    private boolean hasTypeNode() {
        return getTypeNode() != null;
    }

    public String getClassName() {
        return hasTypeNode() ? getTypeNode().getName() : null;
    }

    public String getClassNameLong() {
        return hasTypeNode() ? getTypeNode().getNameLong() : null;
    }
}
