package com.zhantu.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CHARACTERS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static CaptchaResult generateCaptcha() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawLine(x, y, x + xl, y + yl);
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            String ch = String.valueOf(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            code.append(ch);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            g.translate(random.nextInt(3), random.nextInt(3));
            g.drawString(ch, 22 * i + 10, 28);
        }

        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int xl = random.nextInt(WIDTH);
            int yl = random.nextInt(HEIGHT);
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(x, y, xl, yl);
        }

        g.dispose();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "JPEG", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            return new CaptchaResult(base64Image, code.toString().toLowerCase());
        } catch (IOException e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

    public static class CaptchaResult {
        private final String image;
        private final String code;

        public CaptchaResult(String image, String code) {
            this.image = image;
            this.code = code;
        }

        public String getImage() {
            return image;
        }

        public String getCode() {
            return code;
        }
    }
}
