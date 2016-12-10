package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 *
 * grammer --> typeArguments:'<' typeArgument (',' typeArgument)* '>'
 */

public class TypeArgumentsNode extends ASTNode {
    public TypeArgumentsNode() {
        type = NODE_TYPE_TYPEARGUMENTS;
    }

    @Override
    public String printWholeNode() {
        return super.printWholeNode();
    }
}
