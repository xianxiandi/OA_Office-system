package com.web.oa.myexception;

public class MyException extends Exception {
    private String Msg;

    public MyException(String msg) {
       this.Msg = msg;
    }
    public MyException() {
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        this.Msg = msg;
    }
}
