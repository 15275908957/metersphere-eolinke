package io.metersphere.platform.domain;

import im.metersphere.plugin.utils.JSON;

import java.io.InputStream;

public class LarkResponseBase {
    private LarResponseError error;
    private Object data;
    private int err_code;
    private String err_msg;

    public LarResponseError getError() {
        return error;
    }

    public void setError(LarResponseError error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }
}
class LarResponseError {
    private int code;
    private String msg;
    private DisplayMsg display_msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DisplayMsg getDisplay_msg() {
        return display_msg;
    }

    public void setDisplay_msg(DisplayMsg display_msg) {
        this.display_msg = display_msg;
    }
}
class DisplayMsg {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
