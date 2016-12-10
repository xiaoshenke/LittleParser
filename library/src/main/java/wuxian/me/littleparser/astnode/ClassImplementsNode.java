package wuxian.me.littleparser.astnode;

import java.util.List;

/**
 * Created by wuxian on 9/12/2016.
 */

public class ClassImplementsNode extends ASTNode {

    public ClassImplementsNode() {
        type = NODE_IMPLEMENTS_STATEMENT;
    }

    public TypeListNode getTypelistNode() {
        if (subNodes.size() == 0) {
            return null;
        }
        return (TypeListNode) subNodes.get(0);
    }

    private boolean hasTypeListNode() {
        return getTypelistNode() != null;
    }

    public List<String> getInterfacesNameLong() {
        if (!hasTypeListNode()) {
            return null;
        }
        return getTypelistNode().getNames();
    }


    public List<String> getInterfacesName() {
        if (!hasTypeListNode()) {
            return null;
        }
        return getTypelistNode().getNamesLong();
    }
}
