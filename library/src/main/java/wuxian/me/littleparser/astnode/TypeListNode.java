package wuxian.me.littleparser.astnode;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 10/12/2016.
 */

public class TypeListNode extends ASTNode {
    public TypeListNode() {
        type = NODE_TYPE_TYPELIST;
    }

    public TypeNode getTypeNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof TypeNode) {
                return (TypeNode) node;
            }
        }
        return null;
    }

    public List<TypeListCommaNode> getTypelistcommaNodes() {
        List<TypeListCommaNode> nodes = new ArrayList<>();
        for (ASTNode node : subNodes) {
            if (node instanceof TypeListCommaNode) {
                nodes.add((TypeListCommaNode) node);
            }
        }
        return nodes;
    }
}
