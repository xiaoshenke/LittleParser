package wuxian.me.littleparserdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import wuxian.me.littleparser.astnode.ASTNode;
import wuxian.me.littleparser.LittleParser;
import wuxian.me.littleparser.Visitor;
import wuxian.me.littleparser.astnode.ClassDeclareNode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_parse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = ((EditText) findViewById(R.id.et_string)).getText().toString();
                LittleParser parser = new LittleParser();
                boolean success = parser.matchClassString(s);

                if (success) {
                    Visitor visitor = new Visitor();
                    ASTNode classNode = visitor.visitFirstNode(parser.getParsedASTNode(), ASTNode.NODE_CLASS_DECLARATION);
                    if (classNode != null) {
                        String text = "success,whole node: " + classNode.printWholeNode();

                        if (classNode instanceof ClassDeclareNode) {
                            ClassDeclareNode declareNode = (ClassDeclareNode) classNode;
                            text += "\n and it is a class declare node: " + declareNode.getClassNameLong();

                            if (declareNode.hasSuperClass()) {
                                text += "\n super class is " + declareNode.getSuperClassNameLong();
                            }
                            if (declareNode.hasInterfaces()) {
                                List<String> interfaces = declareNode.getInterfacesNameLong();
                                text += "\n interfaces are:";
                                for (int i = 0; i < interfaces.size(); i++) {
                                    if (i != 0) {
                                        text += ", ";
                                    }
                                    text += interfaces.get(i);
                                }
                            }
                        }

                        ((TextView) findViewById(R.id.tv_answer)).setText(text);
                    } else {
                        ((TextView) findViewById(R.id.tv_answer)).setText("parse success,but print string error");
                    }

                } else {
                    ((TextView) findViewById(R.id.tv_answer)).setText("fail");
                }
            }
        });
    }
}
