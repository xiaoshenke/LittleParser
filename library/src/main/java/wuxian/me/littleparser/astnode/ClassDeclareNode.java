package wuxian.me.littleparser.astnode;

import java.util.List;

/**
 * Created by wuxian on 9/12/2016.
 * grammer --> classDeclaration:'class' Identifier typeParameters? ('extends' typeType)? ('implements' typeList)? classBody
 *
 * class A extends B implements C,D
 */

public class ClassDeclareNode extends ASTNode {

    public ClassDeclareNode() {
        type = NODE_CLASS_DECLARATION;
    }

    public TypeParametersNode getTypeparametersNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof TypeParametersNode) {
                return (TypeParametersNode) node;
            }
        }
        return null;
    }

    public boolean hasTypeParametersNode() {
        return getTypeparametersNode() != null;
    }

    public ClassExtendsNode getExtendsNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof ClassExtendsNode) {
                return (ClassExtendsNode) node;
            }
        }
        return null;
    }

    public boolean hasSuperClass() {
        return getExtendsNode() != null;
    }

    //不含模版参数
    public String getClassName() {
        return name;
    }

    //含模版参数
    public String getClassNameLong() {
        String ret = name;
        TypeParametersNode node = getTypeparametersNode();
        if (node != null) {
            name += node.printWholeNode();
        }

        return name;
    }

    public String getSuperClassName() {
        if (!hasSuperClass()) {
            return null;
        }

        ClassExtendsNode extendsNode = getExtendsNode();
        return extendsNode.getClassName();
    }

    public String getSuperClassNameLong() {
        if (!hasSuperClass()) {
            return null;
        }

        ClassExtendsNode extendsNode = getExtendsNode();
        return extendsNode.getClassNameLong();
    }

    public List<String> getInterfacesName() {
        if (!hasInterfaces()) {
            return null;
        }
        ClassImplementsNode implementsNode = getImplementsNode();
        return implementsNode.getInterfacesName();
    }

    public List<String> getInterfacesNameLong() {
        if (!hasInterfaces()) {
            return null;
        }
        ClassImplementsNode implementsNode = getImplementsNode();
        return implementsNode.getInterfacesNameLong();
    }

    public boolean hasInterfaces() {
        return getImplementsNode() != null;
    }

    public ClassImplementsNode getImplementsNode() {
        for (ASTNode node : subNodes) {
            if (node instanceof ClassImplementsNode) {
                return (ClassImplementsNode) node;
            }
        }
        return null;
    }
}
