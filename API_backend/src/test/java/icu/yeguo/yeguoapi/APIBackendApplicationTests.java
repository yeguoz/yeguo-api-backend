package icu.yeguo.yeguoapi;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import icu.yeguo.yeguoapi.constant.SecretConstant;
import org.junit.jupiter.api.Test;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import javax.mail.*;
import java.util.Properties;

//@SpringBootTest
class APIBackendApplicationTests {

    @Test
    void crypto() {
        String content = "test中文";
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4","野果_API平台".getBytes());

        String encryptHex = sm4.encryptHex(content);
        String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);//test中文
        System.out.println(encryptHex);
        System.out.println(decryptStr);
    }

    @Test
    void contextLoads() {
        // 获取当前时间戳
        long timestamp = Instant.now().toEpochMilli();
        // 生成6位随机数
        SecureRandom random = new SecureRandom();
        int randomPart = random.nextInt(999999) + 1000000;
        // 拼接时间戳和随机数
        String accessKeyInput = String.valueOf(timestamp) + randomPart;
        String secretKeyInput = accessKeyInput + SecretConstant.PASSWORD_SECRET_KEY;  // 添加额外的字符串以增加复杂性

        // 使用SHA-256哈希函数生成密钥
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] accessKeyBytes = md.digest(accessKeyInput.getBytes());
            byte[] secretKeyBytes = md.digest(secretKeyInput.getBytes());

            // Base64编码密钥
            String accessKey = Base64.getEncoder().encodeToString(accessKeyBytes);
            String secretKey = Base64.getEncoder().encodeToString(secretKeyBytes);

            System.out.println("Access Key: " + accessKey);
            System.out.println("SecretConstant Key: " + secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test() {
                // 发件人邮箱和授权码
                final String username = "1419593965@qq.com";
                final String password = "ormjptjzlqmjhihj";

                // 收件人邮箱
                String to = "1419593965@qq.com";

                // 设置邮件服务器的属性
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.qq.com"); // 指定SMTP服务器
                props.put("mail.smtp.port", "465"); // QQ邮箱的SMTP端口
                props.put("mail.smtp.auth", "true"); // 启用认证
                props.put("mail.smtp.ssl.enable", "true"); // 使用SSL加密连接
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.socketFactory.port", "465");

                // 创建会话对象
                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                try {
                    // 创建邮件消息
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username)); // 设置发件人
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // 设置收件人
                    message.setSubject("23点52分通知邮件"); // 设置邮件主题
                    message.setText("这是一份来自Java程序的通知邮件。"); // 设置邮件正文

                    // 发送邮件
                    Transport.send(message);
                    System.out.println("邮件已成功发送！");
                } catch (MessagingException mex) {
                    mex.printStackTrace();
                }
            }

    @Test
    public void test1() {
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println("当前时间（毫秒时间戳）: " + currentTimeMillis);
    }
}
