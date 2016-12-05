package wuxian.me.littleparser;

import static wuxian.me.littleparser.LittleParser.TOKEN_NONE_TYPE;

/**
 * Created by wuxian on 5/12/2016.
 */

public class Token {
    public int type = TOKEN_NONE_TYPE;
    public Object obj;

    public Token(int type, Object content) {
        this.type = type;
        this.obj = content;
    }
}
