package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 * ',' typeType
 */

public class TypeListCommaNode extends ASTNode {
    public TypeListCommaNode() {
        type = ASTNode.NODE_TYPE_TYPELIST_COMMA;
    }

    public TypeNode getTypeNode() {
        if (subNodes.size() == 0) {
            return null;
        }
        return (TypeNode) subNodes.get(0);
    }

    public String getName() {
        TypeNode node = getTypeNode();
        if (node == null) {
            return null;
        }
        return node.getName();
    }

    public String getNameLong() {
        TypeNode node = getTypeNode();
        if (node == null) {
            return null;
        }
        return node.getNameLong();
    }
}
