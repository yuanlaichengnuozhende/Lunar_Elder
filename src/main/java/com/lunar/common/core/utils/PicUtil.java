package com.lunar.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片处理
 *
 * @author szx
 * @date 2020/03/09 15:01
 */
@Slf4j
public class PicUtil {

    /**
     * 远程图片转BufferedImage
     *
     * @param destUrl 远程图片地址
     * @return
     */
    public static BufferedImage getBufferedImageFromUrl(String destUrl) {
        HttpURLConnection conn = null;
        BufferedImage buffImg = null;
        try {
            URL url = new URL(destUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                buffImg = ImageIO.read(conn.getInputStream());
                return buffImg;
            }
        } catch (Exception e) {
            log.error("获取图片失败. msg={}", e.getMessage());
        } finally {
            conn.disconnect();
        }
        return buffImg;
    }

    /**
     * 本地图片转BufferedImage
     *
     * @param filePath
     * @return
     */
    public static BufferedImage getBufferedImageFromFile(String filePath) {
        BufferedImage buffImg = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                buffImg = ImageIO.read(file);
            }
        } catch (Exception e) {
            log.error("获取图片失败. msg={}", e.getMessage());
        }

        return buffImg;
    }

    /**
     * 将BufferedImage转换为InputStream
     *
     * @param image
     * @return
     */
    public static InputStream bufferedImageToInputStream(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return is;
        } catch (IOException e) {
            log.error("转换失败. msg={}", e.getMessage());
        }
        return null;
    }

    /**
     * buff转byte[]
     *
     * @param bImage
     * @return
     */
    public static byte[] bufferedImageToBytes(BufferedImage bImage) {
        if (bImage == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", os);
        } catch (IOException e) {
            log.error("转换失败. msg={}", e.getMessage());
            return null;
        }
        return os.toByteArray();
    }

    /**
     * 图片叠加
     *
     * @param buffImg 源文件
     * @param overImg 覆盖文件
     * @param x       距离左上角的X偏移量
     * @param y       距离左上角的Y偏移量
     * @param alpha   透明度, 选择值从0.0~1.0: 完全透明~完全不透明
     * @return BufferedImage
     */
    public static BufferedImage overlyingImage(BufferedImage buffImg,
                                               BufferedImage overImg,
                                               int x,
                                               int y,
                                               float alpha) {
        if (buffImg == null || overImg == null) {
            log.error("图片不存在");
            return null;
        }
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g = buffImg.createGraphics();
        // 获取层图的宽度
        int waterImgWidth = overImg.getWidth();
        int waterImgHeight = overImg.getHeight();
        // 在图形和图像中实现混合和透明效果
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 绘制
        g.drawImage(overImg, x, y, waterImgWidth, waterImgHeight, null);
        // 释放图形上下文使用的系统资源
        g.dispose();
        return buffImg;
    }

    /**
     * 保存图片
     *
     * @param buffImg
     * @param filePath
     * @return
     */
    public static File saveImg(BufferedImage buffImg, String filePath) {
        if (buffImg == null || StringUtils.isBlank(filePath)) {
            return null;
        }

        // 图片格式
        int temp = filePath.lastIndexOf(".") + 1;
        String formatName = filePath.substring(temp);

        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            ImageIO.write(buffImg, formatName, file);
            log.info("保存图片成功. path={}", filePath);
        } catch (IOException e) {
            log.error("保存图片失败. msg={}", e.getMessage());
        }

        return file;
    }

    /**
     * 改变图片大小
     *
     * @param source
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static BufferedImage resize(BufferedImage source, int newWidth, int newHeight) {
        int type = source.getType();
        BufferedImage target = new BufferedImage(newWidth, newHeight, type);
        double sx = (double) newWidth / source.getWidth();
        double sy = (double) newHeight / source.getHeight();
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));

        g.dispose();
        return target;
    }

    /**
     * 原型图片
     *
     * @param image
     * @return
     */
    public static BufferedImage circle(BufferedImage image) {
        return corner(image, image.getWidth());
    }

    /**
     * 圆角图片
     *
     * @param image
     * @return
     */
    public static BufferedImage corner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }

    /**
     * 添加文字
     *
     * @param buffImg
     * @param pressText 水印文字
     * @param fontName  字体名称，    如：宋体
     * @param fontStyle 字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
     * @param fontSize  字体大小，单位为像素
     * @param color     字体颜色
     * @param x         水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
     * @param y         水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha     透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     * @return
     */
    public static BufferedImage pressText(BufferedImage buffImg,
                                          String pressText,
                                          String fontName,
                                          int fontStyle,
                                          int fontSize,
                                          Color color,
                                          int x,
                                          int y,
                                          float alpha) {
        try {
            fontName = StringUtils.isBlank(fontName) ? "宋体" : fontName;
            fontSize = fontSize == 0 ? 32 : fontSize;
            color = color == null ? Color.BLACK : color;

            int width = buffImg.getWidth();
            int height = buffImg.getHeight();
            Graphics2D g = buffImg.createGraphics();
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setColor(color);
            // 消除文字锯齿
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            int widthAdapt = fontSize * getLength(pressText);
            int heightAdapt = fontSize;
            int widthDiff = width - widthAdapt;
            int heightDiff = height - heightAdapt;
            if (x < 0) {
                x = widthDiff / 2;
            } else if (x > widthDiff) {
                x = widthDiff;
            }
            if (y < 0) {
                y = heightDiff / 2;
            } else if (y > heightDiff) {
                y = heightDiff;
            }

            g.drawString(pressText, x, y + heightAdapt);
            g.dispose();
            return buffImg;
        } catch (Exception e) {
            log.error("绘制文字失败. msg={}", e.getMessage());
        }

        return null;
    }

    /**
     * 添加文字（x居中）
     *
     * @param buffImg
     * @param pressText 水印文字
     * @param fontName  字体名称，    如：宋体
     * @param fontStyle 字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
     * @param fontSize  字体大小，单位为像素
     * @param color     字体颜色
     * @param y         水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha     透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     * @return
     */
    public static BufferedImage pressTextXCenter(BufferedImage buffImg,
                                                 String pressText,
                                                 String fontName,
                                                 int fontStyle,
                                                 int fontSize,
                                                 Color color,
                                                 int y,
                                                 float alpha) {
        return pressText(buffImg, pressText, fontName, fontStyle, fontSize, color, -1, y, alpha);
    }

    /**
     * 获取字符长度，一个汉字作为 1 个字符, 一个英文字母作为 0.5 个字符
     *
     * @param text
     * @return 字符长度，如：text="中国",返回 2；text="test",返回 2；text="中国ABC",返回 4.
     */
    public static int getLength(String text) {
        int textLength = text.length();
        int length = textLength;
        for (int i = 0; i < textLength; i++) {
            if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
                length++;
            }
        }
        return (length % 2 == 0) ? length / 2 : length / 2 + 1;
    }

    public static void main(String[] args) throws Exception {
        //// 合成头像
        String postUrl = "https://ins-pro-luban.miao.cn/online/source/image/b17f7ab0-fc01-11ea-a72b-592b37123625.jpg";
        BufferedImage baseImg = PicUtil.getBufferedImageFromUrl(postUrl);
        baseImg = PicUtil.resize(baseImg, 572, 728);
        // 保存到本地
        saveImg(baseImg, "/Users/szx/Desktop/a.png");

        // 头像
        BufferedImage headImg = PicUtil.getBufferedImageFromFile("/Users/szx/Desktop/poster_head.png");
//        PicUtil.corner(headImg, 32);
        PicUtil.circle(headImg);

        // 合成图片
        BufferedImage bufferedImage = PicUtil.overlyingImage(baseImg, headImg, 24, 24, 1.0f);
        saveImg(bufferedImage, "/Users/szx/Desktop/b.png");

        //// 合成文字
//        BufferedImage fontImg = pressText(bufferedImage, "尊敬的测试用户xxx", "阿里巴巴普惠体-R",
//            Font.PLAIN, 50, Color.RED, 120, 602, 1.0f);
//        BufferedImage fontImg = pressText(bufferedImage, "尊敬的测试用户xxx", "Kaiti SC",
//            Font.PLAIN, 50, Color.RED, 1, 602, 1.0f);
        BufferedImage fontImg = pressText(bufferedImage, "尊敬的测试用户xxx", "Kaiti SC",
                                          Font.PLAIN, 50, Color.RED, -1, -1, 1.0f);
//        BufferedImage fontImg = pressTextXCenter(bufferedImage, "尊敬的测试用户xxx", "Kaiti SC",
//            Font.PLAIN, 50, Color.RED, 602, 1.0f);
        saveImg(fontImg, "/Users/szx/Desktop/c.png");

    }

}
