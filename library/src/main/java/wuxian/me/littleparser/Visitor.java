package wuxian.me.littleparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 8/12/2016.
 */

public class Visitor {
    public Visitor() {
        ;
    }

    //visitFirstNode
    public ASTNode visitFirstNode(ASTNode root, int nodeType) {
        List<ASTNode> nodes = visitAllNode(root, nodeType);
        if (nodes == null || nodes.size() == 0) {
            return null;
        }

        return nodes.get(0);
    }

    //Todo:
    public List<ASTNode> visitAllNode(ASTNode root, int nodeType) {
        if (root == null) {
            return new ArrayList<>();
        }
        return null;
    }
}
