package wuxian.me.littleparser.astnode;

import java.util.ArrayList;
import java.util.List;

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

    public List<ArrayNode> getArraynodes() {
        List<ArrayNode> nodes = new ArrayList<>();
        for (ASTNode node : subNodes) {
            if (node instanceof ArrayNode) {
                nodes.add((ArrayNode) node);
            }
        }
        return nodes;
    }

    //这里的name不管后面的[][]
    public String getName() {
        if (isPrimitive()) {
            return getPrimitiveNode().printWholeNode();
        }
        ClassOrInterfaceNode node = getClassOrInterfaceNode();
        return node == null ? null : node.getName();
    }

    //这里的name不管后面的[][]
    public String getNameLong() {
        if (isPrimitive()) {
            return getPrimitiveNode().printWholeNode();
        }
        ClassOrInterfaceNode node = getClassOrInterfaceNode();
        return node == null ? null : node.getNameLong();
    }
}
