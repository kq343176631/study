package com.style.common.file;

import java.io.*;

/**
 * 数据流工具类
 */
public class IOUtils extends org.apache.commons.io.IOUtils {

    private final static int BUFFER_SIZE = 4096;

    /**
     * 根据文件路径创建文件输入流处理 以字节为单位（非 unicode ）
     *
     * @param filePath filePath
     * @return FileInputStream
     */
    public static FileInputStream getFileInputStream(String filePath) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("错误信息:文件不存在");
            e.printStackTrace();
        }
        return fileInputStream;
    }

    /**
     * 根据文件对象创建文件输入流处理 以字节为单位（非 unicode ）
     *
     * @param file file
     * @return FileInputStream
     */
    public static FileInputStream getFileInputStream(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("错误信息:文件不存在");
            e.printStackTrace();
        }
        return fileInputStream;
    }

    /**
     * 根据文件对象创建文件输出流处理 以字节为单位（非 unicode ）
     *
     * @param file   file
     * @param append true:文件以追加方式打开,false:则覆盖原文件的内容
     * @return FileOutputStream
     */
    public static FileOutputStream getFileOutputStream(File file, boolean append) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, append);
        } catch (FileNotFoundException e) {
            System.out.println("错误信息:文件不存在");
            e.printStackTrace();
        }
        return fileOutputStream;
    }

    /**
     * 根据文件路径创建文件输出流处理 以字节为单位（非 unicode ）
     *
     * @param filePath filePath
     * @param append   true:文件以追加方式打开,false:则覆盖原文件的内容
     * @return FileOutputStream
     */
    public static FileOutputStream getFileOutputStream(String filePath, boolean append) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath, append);
        } catch (FileNotFoundException e) {
            System.out.println("错误信息:文件不存在");
            e.printStackTrace();
        }
        return fileOutputStream;
    }

    /**
     * 将InputStream转换成String
     *
     * @param in InputStream
     * @return String
     */
    public static String InputStreamToString(InputStream in) {
        ByteArrayOutputStream outStream = getByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        String string = null;
        int count;
        try {
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
                outStream.write(data, 0, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // data = null;
        try {
            string = new String(outStream.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * @param in       InputStream
     * @param encoding encoding
     * @return String
     */
    public static String InputStreamToString(InputStream in, String encoding) {
        String string = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        try {
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
                outStream.write(data, 0, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            string = new String(outStream.toByteArray(), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 将String转换成InputStream
     *
     * @param string string
     * @return InputStream
     * @throws Exception Exception
     */
    public static InputStream StringToInputStream(String string) throws Exception {
        return new ByteArrayInputStream(string.getBytes("UTF-8"));
    }

    /**
     * 将String转换成byte[]
     *
     * @param string string
     * @return byte[]
     */
    public static byte[] StringToByte(String string) {
        byte[] bytes = null;
        try {
            bytes = InputStreamToByte(StringToInputStream(string));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将InputStream转换成byte[]
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] InputStreamToByte(InputStream in) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }
        return outStream.toByteArray();
    }

    /**
     * 将byte[]转换成InputStream
     *
     * @param bytes bytes
     * @return InputStream
     */
    public static InputStream byteToInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 将byte[]转换成String
     *
     * @param bytes bytes
     * @return String
     */
    public static String byteToString(byte[] bytes) {
        String result = null;
        try {
            result = InputStreamToString(byteToInputStream(bytes), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * InputStream 转换成 byte[]
     *
     * @param in in
     * @return byte[]
     * @throws IOException IOException
     */
    public byte[] getBytes(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = new byte[BUFFER_SIZE];
        int len;
        while ((len = in.read(b, 0, BUFFER_SIZE)) != -1) {
            os.write(b, 0, len);
        }
        os.flush();
        byte[] bytes = os.toByteArray();
        System.out.println(new String(bytes));
        return bytes;
    }

    /**
     * 获取字节数组流
     *
     * @return ByteArrayOutputStream
     */
    public static ByteArrayOutputStream getByteArrayOutputStream() {
        return new ByteArrayOutputStream();
    }
}