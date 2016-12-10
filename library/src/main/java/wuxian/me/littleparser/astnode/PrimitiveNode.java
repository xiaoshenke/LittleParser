package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 *
 * grammer --> primitiveType:'boolean'|'char'|'byte'|'short'|'int'|'long'|'float'|'double'
 */

public class PrimitiveNode extends ASTNode {
    public PrimitiveNode() {
        type = NODE_TYPE_PRIMITIVE;
    }
}
