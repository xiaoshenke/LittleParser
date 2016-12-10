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

    private boolean hasTypeArguments() {
        return getTypeArgumentsNode() != null;
    }

    public String getName() {
        String shortname = name;

        for (ClassOrInterfaceDotNode dotNode : getClassOrInterfaceDotNodes()) {
            String dot = dotNode.getName();
            if (dot != null) {
                shortname += dot;
            }
        }

        return shortname;
    }


    public String getNameLong() {
        String shortname = name;

        if (hasTypeArguments()) {
            shortname += getTypeArgumentsNode().printWholeNode();
        }

        for (ClassOrInterfaceDotNode dotNode : getClassOrInterfaceDotNodes()) {
            String dot = dotNode.getNameLong();
            if (dot != null) {
                shortname += dot;
            }
        }

        return shortname;
    }
}
