package wuxian.me.littleparser.astnode;

/**
 * Created by wuxian on 10/12/2016.
 *
 * grammer --> typeParameters: '<' typeParameter (',' typeParameter)* '>'
 */

public class TypeParametersNode extends ASTNode {
    public TypeParametersNode() {
        type = NODE_TYPE_PARAMETERS;
    }
}
