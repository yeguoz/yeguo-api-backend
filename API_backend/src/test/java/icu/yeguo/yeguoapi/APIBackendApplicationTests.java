package icu.yeguo.yeguoapi;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.http.HttpRequest;
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
import java.text.SimpleDateFormat;
import java.time.Instant;
import javax.mail.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import static icu.yeguo.apicommon.constant.common.*;
class APIBackendApplicationTests {

    @Test
    void testRequest() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        System.out.println("timestamp::" + timestamp);
        String nonce = String.valueOf(UUID.randomUUID());
        System.out.println("nonce::" + nonce);
        String message = "GET\n/api/app/weibo/hot\n" + X_ACCESS_KEY + ":" + "290F6AA4F0BFE8F584DE60D25E56706F\n" +
                X_TIMESTAMP + ":" + timestamp + "\n" + X_NONCE + ":" + nonce;
        System.out.println("message::" + message);

        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, "3F661CAD463534FC5634D095F81AC605".getBytes());
        String signature = mac.digestHex(message);
        System.out.println("signature::" + signature);
        String result = HttpRequest.get("http://localhost:8081/api/app/weibo/hot")
                .header(X_ACCESS_KEY, "290F6AA4F0BFE8F584DE60D25E56706F")
                .header(X_TIMESTAMP, timestamp)
                .header(X_NONCE, nonce)
                .header(X_SIGNATURE, signature)
                .timeout(6 * 60 * 60 * 1000)//超时，毫秒
                .execute()
                .body();
        System.out.println(result);
    }

    @Test
    void timeTest() {
        // 获取当前时间的毫秒时间戳
        long timestamp = System.currentTimeMillis();

        // 创建SimpleDateFormat对象，并设置日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        // 将毫秒时间戳转换为Date对象
        Date date = new Date(timestamp);

        // 使用SimpleDateFormat将Date对象格式化为字符串
        String formattedDateTime = sdf.format(date);
        System.out.println(timestamp);
        // 输出格式化后的日期时间字符串
        System.out.println(formattedDateTime);
    }

    @Test
    void generateSignature() {
        // 生成签名
        String testStr = "7F48461FA9DB04287F8DF2C21CE39BB77DFF6FD641F8434ECCCEC5DA3BB05637";
        // 此处密钥如果有非ASCII字符
        byte[] key = "sdfsdsfds".getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);

        String macHex = mac.digestHex(testStr);
        System.out.println(macHex);
    }

    @Test
    void httpReqTest() {
        // http客户端测试
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("irp", "1419593965");
        paramMap.put("test", "这是个测试");
        HttpRequest form = HttpRequest.post("http://localhost:8082/api/qq/info")
                .form(paramMap);//表单内容
        System.out.println(form);
    }

    @Test
    void crypto() {
        // 加密
        String content = "123456789";
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4", "野果_API平台".getBytes());

        String encryptHex = sm4.encryptHex(content);
        String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);//test中文
        System.out.println(encryptHex);
        System.out.println(decryptStr);
    }

    @Test
    public void generateKeys() {
        // 生成ak sk
        // 获取当前时间戳
        long timestamp = Instant.now().toEpochMilli();
        // 生成6位随机数
        SecureRandom random = new SecureRandom();
        int randomPart = random.nextInt(899999) + 100000; // 生成100000到999999之间的随机数
        // 拼接时间戳和随机数
        String accessKeyInput = String.valueOf(timestamp) + randomPart;
        String secretKeyInput = accessKeyInput + SecretConstant.PASSWORD_SECRET_KEY;// 添加额外的字符串以增加复杂性

        // 使用SHA-256哈希函数生成密钥
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] accessKeyBytes = md.digest(accessKeyInput.getBytes());
            byte[] secretKeyBytes = md.digest(secretKeyInput.getBytes());

            // 将字节数组转换为十六进制字符串
            String md5AccessKey = toMD5(accessKeyBytes);
            String md5SecretKey = toMD5(secretKeyBytes);

            System.out.println("Access Key: " + md5AccessKey);
            System.out.println("Secret Key: " + md5SecretKey);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    private String toMD5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(bytes);

            // 转换字节数组为十六进制大写字符串表示
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
            System.out.println(mex.getMessage());
        }
    }

    @Test
    public void test1() {
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println("当前时间（毫秒时间戳）: " + currentTimeMillis);
    }
}
