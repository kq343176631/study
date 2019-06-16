package com.style.study.socket.msg;

public class RequestMessage extends CommonMessage {

    public RequestMessage(){
        super();
    }

    public RequestMessage(int header,int length,byte[] body){
        super(header,length,body);
    }
}
