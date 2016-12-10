package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 *
 * grammer --> '.' Identifier typeArguments?
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

    private boolean hasTypeArguments() {
        return getTypeArgumentsNode() != null;
    }

    public String getName() {
        return "." + name;
    }


    public String getNameLong() {
        if (hasTypeArguments()) {
            return getName() + getTypeArgumentsNode().printWholeNode();
        }
        return getName();
    }
}
