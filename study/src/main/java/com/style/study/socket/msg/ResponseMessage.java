package com.style.study.socket.msg;

public class ResponseMessage extends CommonMessage{

    public ResponseMessage(){
        super();
    }

    public ResponseMessage(int header,int length,byte[] body){
        super(header,length,body);
    }
}
