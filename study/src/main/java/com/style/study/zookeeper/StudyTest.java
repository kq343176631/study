package com.style.study.zookeeper;

import java.util.Arrays;

public class StudyTest {

    public static void main(String[] args) {

        byte[] test=new byte[5];
        test[0]=1;
        test[1]=1;
        test[2]=1;
        test[3]=1;

        byte[] newByte=new byte[10];

        System.arraycopy(test,0,newByte,0,3);

        for(int i=0;i<10;i++){
            System.out.println(newByte[i]);
        }


    }
}
