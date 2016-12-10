package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 9/12/2016.
 */

public class ClassDeclareNode extends ASTNode {

    public ClassDeclareNode() {
        type = NODE_CLASS_DECLARATION;
    }

    public boolean isTemplate() {
        for (ASTNode node : subNodes) {
            if (node instanceof TypeParametersNode) {
                return true;
            }
        }
        return false;
    }

    public ClassExtendsNode getExtendsNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof ClassExtendsNode) {
                return (ClassExtendsNode) node;
            }
        }
        return null;
    }

    public ClassImplementsNode getImplementsNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof ClassImplementsNode) {
                return (ClassImplementsNode) node;
            }
        }
        return null;
    }
}
