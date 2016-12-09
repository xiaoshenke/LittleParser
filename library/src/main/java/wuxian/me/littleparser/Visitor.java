package wuxian.me.littleparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import wuxian.me.littleparser.astnode.ASTNode;

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

    //广度遍历impl
    public List<ASTNode> visitAllNode(ASTNode root, int nodeType) {
        if (root == null) {
            return new ArrayList<>();
        }

        List<ASTNode> ret = new ArrayList<>();

        Queue<ASTNode> nodes = new LinkedBlockingQueue();
        nodes.add(root);

        while (nodes.size() != 0) {
            ASTNode currentNode = nodes.poll();
            for (ASTNode node : currentNode.subNodes) {
                nodes.add(node);
            }
            if (currentNode.type == nodeType) {
                ret.add(currentNode);
            }
        }

        return ret;
    }
}
