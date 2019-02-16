package com.style.common.media;

import com.style.common.lang.RandomUtils;
import org.patchca.background.BackgroundFactory;
import org.patchca.color.ColorFactory;
import org.patchca.filter.FilterFactory;
import org.patchca.filter.predefined.*;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.text.renderer.BestFitTextRenderer;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.RandomWordFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 验证码工具
 */
public class CaptchaUtils {

    private static ConfigurableCaptchaService ccs;

    private static FilterFactory[] filterFactory;

    /**
     * 生成验证码
     */
    public static String getCaptcha(OutputStream outputStream) throws IOException {
        // 初始化设置
        initialize();
        // 设置随机样式
        ccs.setFilterFactory(filterFactory[RandomUtils.nextInt(5)]);
        return EncoderHelper.getChallangeAndWriteImage(ccs, "png", outputStream);

    }

    /**
     * 初始化
     */
    private static void initialize() {
        if (ccs == null) {
            synchronized (CaptchaUtils.class) {
                if (ccs == null) {
                    doInitialize();
                }
            }
        }
    }

    /**
     * 执行初始化工作
     */
    private static void doInitialize() {
        // 配置初始化
        ccs = new ConfigurableCaptchaService();

        // 设置图片大小
        ccs.setWidth(100);
        ccs.setHeight(28);

        // 设置文字数量
        RandomWordFactory wf = new RandomWordFactory();
        wf.setCharacters("ABDEFGHKMNRSWX2345689");
        wf.setMinLength(4);
        wf.setMaxLength(4);
        ccs.setWordFactory(wf);

        // 设置字体大小
        RandomFontFactory ff = new RandomFontFactory();
        ff.setMinSize(28);
        ff.setMaxSize(28);
        ccs.setFontFactory(ff);

        // 设置文字渲染边距
        BestFitTextRenderer tr = new BestFitTextRenderer();
        tr.setTopMargin(3);
        tr.setRightMargin(3);
        tr.setBottomMargin(3);
        tr.setLeftMargin(3);
        ccs.setTextRenderer(tr);

        // 设置字体颜色
        ccs.setColorFactory(new ColorFactory() {
            @Override
            public Color getColor(int x) {
                int r = RandomUtils.nextInt(90);
                int g = RandomUtils.nextInt(90);
                int b = RandomUtils.nextInt(90);
                return new Color(r, g, b);
            }
        });

        // 设置背景
        ccs.setBackgroundFactory(new BackgroundFactory() {
            @Override
            public void fillBackground(BufferedImage image) {
                Graphics graphics = image.getGraphics();
                // 验证码图片的宽高
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();
                // 填充为白色背景
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, imgWidth, imgHeight);
                // 画 50 个噪点(颜色及位置随机)
                for (int i = 0; i < 50; i++) {
                    // 随机颜色
                    int rInt = RandomUtils.nextInt(100) + 50;
                    int gInt = RandomUtils.nextInt(100) + 50;
                    int bInt = RandomUtils.nextInt(100) + 50;
                    graphics.setColor(new Color(rInt, gInt, bInt));
                    // 随机位置
                    int xInt = RandomUtils.nextInt(imgWidth - 3);
                    int yInt = RandomUtils.nextInt(imgHeight - 2);
                    // 随机旋转角度
                    int sAngleInt = RandomUtils.nextInt(360);
                    int eAngleInt = RandomUtils.nextInt(360);
                    // 随机大小
                    int wInt = RandomUtils.nextInt(6);
                    int hInt = RandomUtils.nextInt(6);
                    // 填充背景
                    graphics.fillArc(xInt, yInt, wInt, hInt, sAngleInt, eAngleInt);
                    // 画5条干扰线
                    if (i % 10 == 0) {
                        int xInt2 = RandomUtils.nextInt(imgWidth);
                        int yInt2 = RandomUtils.nextInt(imgHeight);
                        graphics.drawLine(xInt, yInt, xInt2, yInt2);
                    }
                }
            }
        });
        // 初始化工厂
        filterFactory = new FilterFactory[]{
                new WobbleRippleFilterFactory(),// 摆波纹
                new DoubleRippleFilterFactory(),// 双波纹
                new DiffuseRippleFilterFactory(),// 漫纹波
                new MarbleRippleFilterFactory(), // 大理石
                new CurvesRippleFilterFactory(ccs.getColorFactory())// 曲线波纹
        };
    }
}
