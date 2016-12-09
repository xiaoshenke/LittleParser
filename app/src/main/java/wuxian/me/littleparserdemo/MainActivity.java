package wuxian.me.littleparserdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import wuxian.me.littleparser.astnode.ASTNode;
import wuxian.me.littleparser.LittleParser;
import wuxian.me.littleparser.Visitor;

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
                    ASTNode classNode = visitor.visitFirstNode(parser.getParsedASTNode(), ASTNode.NODE_EXTENDS_STATEMENT);
                    if (classNode != null) {
                        ((TextView) findViewById(R.id.tv_answer)).setText("success: " + classNode.toString());
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
