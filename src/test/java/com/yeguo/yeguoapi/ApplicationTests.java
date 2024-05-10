package com.yeguo.yeguoapi;

import com.yeguo.yeguoapi.constant.SecretConstant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

//@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {
        // 获取当前时间戳
        long timestamp = Instant.now().toEpochMilli();
        // 生成6位随机数
        SecureRandom random = new SecureRandom();
        int randomPart = random.nextInt(999999) + 1000000;
        // 拼接时间戳和随机数
        String accessKeyInput = String.valueOf(timestamp) + String.valueOf(randomPart);
        String secretKeyInput = accessKeyInput + SecretConstant.SECRET;  // 添加额外的字符串以增加复杂性

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

}
