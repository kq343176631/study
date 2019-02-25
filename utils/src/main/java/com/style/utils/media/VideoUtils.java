package com.style.utils.media;

import com.style.utils.collect.ListUtils;
import com.style.utils.io.FileUtils;
import com.style.utils.io.PropertyUtils;
import com.style.utils.lang.StringUtils;
import com.style.utils.lang.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频工具类
 */
public class VideoUtils {

    private static final Logger log = LoggerFactory.getLogger(VideoUtils.class);
    /**
     * ffmpeg.exe所放的路径
     */
    private static String ffmPegFile;
    /**
     * mencoder.exe所放的路径
     */
    private static String menCoderFile;
    /**
     * qt-faststart.exe所放的路径
     */
    private static String qtFastStartFile;

    /**
     * 需转换的原始文件名称
     */
    private String inputFile = "";
    /**
     * 当前文件的文件后缀
     */
    private String inputFileExtension = "";

    /**
     * 输出文件名称
     */
    private String outputFile = "";
    /**
     * 最终转换的文件格式 mp4 flv
     */
    private String outputFileExtension = "mp4";
    /**
     * 生成缩略图文件名
     */
    private String imgFile = "";
    /**
     * 生成视频图片截图后缀 jpg gif
     */
    private String imgFileExtension = "jpg";
    /**
     * 视频默认宽高 800
     */
    private String width = null;

    /**
     * 视频默认宽高 600
     */
    private String height = null;

    /**
     * 是否正常状态
     */
    private boolean status = false;

    /**
     * 构造函数
     *
     * @param inputFile 需要转换视频文件的绝对路径和名称
     */
    public VideoUtils(String inputFile) {
        this(inputFile, null, null);
    }

    /**
     * 构造函数
     *
     * @param inputFile  需要转换视频文件的绝对路径和名称
     * @param outputFile 视频文件转换后的输出文件路径和名称
     * @param imgFile    视频文件截图的图片路径和名称
     */
    public VideoUtils(String inputFile, String outputFile, String imgFile) {
        this.inputFile = FileUtils.path(inputFile);
        this.inputFileExtension = FileUtils.getFileExtension(inputFile);
        this.outputFile = outputFile != null ? FileUtils.path(outputFile) : inputFile + "." + outputFileExtension;
        this.imgFile = imgFile != null ? imgFile : inputFile + "." + imgFileExtension;
        this.status = checkfile(inputFile);
    }

    /**
     * 构造函数
     *
     * @param inputFile  需要转换视频文件的绝对路径和名称
     * @param outputFile 视频文件转换后的输出文件路径和名称
     * @param imgFile    视频文件截图的图片路径和名称
     * @param width      转换后视频和图片的宽度
     * @param height     转换后视频和图片的高度
     */
    public VideoUtils(String inputFile, String outputFile, String imgFile, String width, String height) {
        this(inputFile, outputFile, imgFile);
        this.width = width;
        this.height = height;
    }

    /**
     * 检查文件格式。根据文件格式 分类解析
     *
     * @return int 0：ffmpag；1：mencoder；0：不支持的格式
     */
    private int checkContentType() {
        // ffmpeg 能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv，rm，rmvb等）
        if (StringUtils.inString(inputFileExtension, "avi", "mpg", "wmv", "3gp", "mov",
                "mp4", "asf", "asx", "flv", "rm", "rmvb")) {
            return 0;
        }
        // 对ffmpeg无法解析的文件格式(wmv9，等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (StringUtils.inString(inputFileExtension, "wmv9")) {
            return 1;
        }
        return 9;
    }

    /**
     * 截取图片
     *
     * @return boolean
     */
    public boolean cutPic() {
        long startTime = System.currentTimeMillis(); // 获取开始时间
        boolean statusTemp = status;
        if (statusTemp) {
            statusTemp = processFfmpegCutpic(inputFile, outputFile);
            try {
                File imgfile = new File(imgFile);
                if (imgfile.exists()) {
                    ImageUtils.thumbnails(imgfile, 800, 600, null);
                } else {
                    statusTemp = false;
                }
            } catch (Exception e) {
                statusTemp = false;
                log.error("视频剪切图片失败", e);
            }
        }
        log.debug("视频剪切图片" + (statusTemp ? "成功" : "失败") + "，用时：" + TimeUtils.formatDateAgo(System.currentTimeMillis() - startTime));
        return statusTemp;
    }

    /**
     * 转换视频
     *
     * @return boolean
     */
    public boolean convert() {
        long startTime = System.currentTimeMillis(); // 获取开始时间
        boolean statusTemp = status;
        int type = checkContentType();
        String tempFile = outputFile + ".tmp";
        if (statusTemp && type == 0) {
            log.debug("使用ffmpage进行视频转换");
            statusTemp = processFfmpeg(inputFile, tempFile);
        } else if (statusTemp && type == 1) {
            log.debug("使用mencoder进行视频转换");
            statusTemp = processMencoder(inputFile, tempFile);
        }
        if (statusTemp) {
            log.debug("将mp4视频的元数据信息转到视频第一帧");
            statusTemp = processQtFastStart(tempFile, outputFile);
        }
        log.debug("删除临时文件");
        FileUtils.deleteFile(tempFile);
        log.debug("视频转换" + (statusTemp ? "成功" : "失败") + "，用时：" + TimeUtils.formatDateAgo(System.currentTimeMillis() - startTime));
        return statusTemp;
    }

    /**
     * 检查文件是否存在
     */
    public boolean checkfile(String inputFile) {
        File file = new File(inputFile);
        if (!file.isFile() || !file.exists()) {
            log.warn("文件不存在！");
            return false;
        }
        return true;
    }

    /**
     * ffmpeg 截取缩略图
     */
    public boolean processFfmpegCutpic(String inputFile, String outputFile) {
        List<String> command = new java.util.ArrayList<>();
        command.add(getFfmPegFile());
        command.add("-i");
        command.add(inputFile);
        if ((imgFileExtension.toLowerCase()).equals("gif")) {
            command.add("-vframes");
            command.add("30");
            command.add("-f");
            command.add("gif");
        } else {
            command.add("-ss");
            command.add("4");
            command.add("-t");
            command.add("0.001");
            command.add("-f");
            command.add("image2");
        }
        command.add("-y");
        command.add(imgFile);
        return process(command);
    }

    /**
     * ffmpeg能解析转换视频
     *
     * @param inputFile （asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
     * @return boolean
     */
    private boolean processFfmpeg(String inputFile, String outputFile) {
        List<String> command = new java.util.ArrayList<>();
        command.add(getFfmPegFile());
        command.add("-i");
        command.add(inputFile);
        command.add("-f");
        command.add(outputFileExtension);
        command.add("-c:v");
        command.add("libx264");
        command.add("-b:v");
        command.add("600k");
        command.add("-g");
        command.add("300");
        command.add("-bf");
        command.add("2");
        command.add("-c:a");
        command.add("aac");
        command.add("-strict");
        command.add("experimental");
        command.add("-ac");
        command.add("1");
        command.add("-ar");
        command.add("44100"); // 11025 22050 32000 44100
        command.add("-r");
        command.add("29.97");
        command.add("-qscale");
        command.add("6");
        if (StringUtils.isNotBlank(width) && StringUtils.isNotBlank(height)) {
            command.add("-s");
            command.add((width + "x" + height));
        }
        command.add("-y");
        command.add(outputFile);
        return process(command);
    }

    /**
     * 直接转换不需要转成avi在转换
     *
     * @return boolean
     */
    private boolean processMencoder(String inputFile, String outputFile) {
        List<String> command = new ArrayList<>();
        command.add(getMenCoderFile());
        command.add(inputFile);
        command.add("-oac");
        command.add("mp3lame");
        command.add("-lameopts");
        command.add("aq=7:vbr=2:q=6");
        command.add("-srate");
        command.add("44100");
        if (StringUtils.isNotBlank(width) && StringUtils.isNotBlank(height)) {
            command.add("-vf");
            command.add(("scale=" + width + ":" + height + ",harddup"));
        }
        command.add("-ovc");
        command.add("xvid");
        command.add("-xvidencopts");
        command.add("fixed_quant=8");
        command.add("-of");
        command.add("lavf");
        command.add("-o");
        command.add(outputFile);
        return process(command);
    }

    /**
     * 将mp4视频的元数据信息转到视频第一帧
     */
    private boolean processQtFastStart(String inputFile, String outputFile) {
        List<String> command = new ArrayList<>();
        command.add(getQtFastStartFile());
        command.add(inputFile);
        command.add(outputFile);
        return process(command);
    }

    /**
     * 执行命令
     */
    private boolean process(List<String> command) {
        try {
            log.debug(ListUtils.convertToString(command, " "));
            Process process = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
            new PrintErrorReader(process.getErrorStream()).start();
            new PrintInputStream(process.getInputStream()).start();
            process.waitFor();
            return true;
        } catch (Exception e) {
            if (StringUtils.contains(e.getMessage(), "CreateProcess error=2")) {
                log.error("缺少视频转换工具，请配置video.ffmpegFile相关参数。" + e.getMessage());
            } else {
                log.error(e.getMessage(), e);
            }
            return false;
        }
    }

    public static String getFfmPegFile() {
        if (ffmPegFile == null) {
            ffmPegFile = PropertyUtils.getInstance().getProperty("video.ffmpegFile");
        }
        return ffmPegFile;
    }

    public static void setFfmPegFile(String ffmPegFile) {
        VideoUtils.ffmPegFile = ffmPegFile;
    }

    public static String getMenCoderFile() {
        if (menCoderFile == null) {
            menCoderFile = PropertyUtils.getInstance().getProperty("video.menCoderFile");
        }
        return menCoderFile;
    }

    public static void setMenCoderFile(String menCoderFile) {
        VideoUtils.menCoderFile = menCoderFile;
    }

    public static String getQtFastStartFile() {
        if (qtFastStartFile == null) {
            qtFastStartFile = PropertyUtils.getInstance().getProperty("video.qtFaststartFile");
        }
        return qtFastStartFile;
    }

    public static void setQtFastStartFile(String qtFastStartFile) {
        VideoUtils.qtFastStartFile = qtFastStartFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = FileUtils.path(inputFile);
    }

    public String getInputFileExtension() {
        return inputFileExtension;
    }

    public void setInputFileExtension(String inputFileExtension) {
        this.inputFileExtension = inputFileExtension;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = FileUtils.path(outputFile);
    }

    public String getOutputFileExtension() {
        return outputFileExtension;
    }

    public void setOutputFileExtension(String outputFileExtension) {
        this.outputFileExtension = outputFileExtension;
    }

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }

    public String getImgFileExtension() {
        return imgFileExtension;
    }

    public void setImgFileExtension(String imgFileExtension) {
        this.imgFileExtension = imgFileExtension;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    class PrintInputStream extends Thread {

        java.io.InputStream __is;

        public PrintInputStream(java.io.InputStream is) {
            __is = is;
        }

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(__is));
                String line;
                while ((line = br.readLine()) != null) {
                    log.debug(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PrintErrorReader extends Thread {

        java.io.InputStream __is;

        public PrintErrorReader(java.io.InputStream is) {
            __is = is;
        }

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(__is));
                String line;
                while ((line = br.readLine()) != null) {
                    log.error(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
