package com.yang.springbootbackend.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class ImageCodeUtil {

    /**
     * 生成图片验证码
     * @param code 验证码字符串
     * @return 图片
     */
    public static BufferedImage generateImageCode(String code) {
        // 图片宽高
        int width = 120;
        int height = 40;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置渲染提示
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 绘制干扰线
        drawInterferenceLines(g, width, height);

        // 绘制干扰点
        drawInterferencePoints(g, width, height);

        // 绘制验证码
        drawVerificationCode(g, code);

        g.dispose();
        return image;
    }

    /**
     * 绘制干扰线
     */
    private static void drawInterferenceLines(Graphics2D g, int width, int height) {
        g.setColor(Color.LIGHT_GRAY);
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制干扰点
     */
    private static void drawInterferencePoints(Graphics2D g, int width, int height) {
        g.setColor(Color.LIGHT_GRAY);
        Random random = new Random();
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.drawRect(x, y, 1, 1);
        }
    }

    /**
     * 绘制验证码
     */
    private static void drawVerificationCode(Graphics2D g, String code) {
        // 随机颜色生成器
        Random random = new Random();

        // 不同字体
        String[] fontNames = {"Arial", "Verdana", "Times New Roman", "Courier New", "Impact"};

        for (int i = 0; i < code.length(); i++) {
            // 随机字体、大小、颜色
            String fontName = fontNames[random.nextInt(fontNames.length)];
            int fontSize = 24 + random.nextInt(12); // 24-36大小
            Font font = new Font(fontName, Font.BOLD, fontSize);
            g.setFont(font);

            // 随机颜色
            int red = 20 + random.nextInt(160);
            int green = 20 + random.nextInt(160);
            int blue = 20 + random.nextInt(160);
            g.setColor(new Color(red, green, blue));

            // 随机旋转
            double degree = (random.nextInt(60) - 30) * Math.PI / 180;

            // 计算位置
            int x = 25 + i * 20 + random.nextInt(5);
            int y = 25 + random.nextInt(10);

            // 应用旋转
            AffineTransform original = g.getTransform();
            g.rotate(degree, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.setTransform(original);
        }
    }

    /**
     * 将图片转换为Base64编码
     */
    public static String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("图片转Base64失败", e);
        }
    }
}