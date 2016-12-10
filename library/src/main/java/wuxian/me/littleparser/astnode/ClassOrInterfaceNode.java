package wuxian.me.littleparser.astnode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 10/12/2016.
 */

public class ClassOrInterfaceNode extends ASTNode {
    public ClassOrInterfaceNode() {
        type = NODE_TYPE_CLASSORINTERFACE;
    }

    public List<ClassOrInterfaceDotNode> getClassOrInterfaceDotNodes() {
        List<ClassOrInterfaceDotNode> nodes = new ArrayList<>();
        for (ASTNode node : subNodes) {
            if (node instanceof ClassOrInterfaceDotNode) {
                nodes.add((ClassOrInterfaceDotNode) node);
            }
        }
        return nodes;
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
