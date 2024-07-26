package icu.yeguo.yeguoapi.utils;

import com.sun.mail.util.MailSSLSocketFactory;
import icu.yeguo.yeguoapi.model.dto.orderInfo.OrderInfoNotificationRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
public class EmailUtil {
    public static String verifyCode;
    private static final String EMAIL_SENDER = "aidjajd@163.com";
    private static final String EMAIL_SENDER_NAME = "野果API接口开放平台";
    private static final String SMTP_HOST = "smtp.163.com";
    private static final String SMTP_AUTH_USER = "aidjajd@163.com";
    private static final String SMTP_AUTH_PWD = "WJUBMWYGVQTOZCQX";

    private static Session createSession() throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.debug", "false");
        props.setProperty("mail.host", SMTP_HOST);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);

        return Session.getInstance(props);
    }

    private static void sendEmail(Session session, Message message) throws MessagingException {
        try (Transport ts = session.getTransport()) {
            ts.connect(SMTP_HOST, SMTP_AUTH_USER, SMTP_AUTH_PWD);
            ts.sendMessage(message, message.getAllRecipients());
        }
    }

    public static Integer sendMail(String receiveMail) {
        try {
            Session session = createSession();
            Message message = createValidationMail(session, receiveMail);
            sendEmail(session, message);
            return 1;
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            return 0;
        }
    }

    public static Integer sendMail(String receiveMail, OrderInfoNotificationRequest orderInfoNotificationRequest) {
        try {
            Session session = createSession();
            Message message = createNotificationMail(session, receiveMail, orderInfoNotificationRequest);
            sendEmail(session, message);
            return 1;
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            return 0;
        }
    }

    private static Message createNotificationMail(Session session, String receiveMail, OrderInfoNotificationRequest orderInfoNotificationRequest)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_SENDER, EMAIL_SENDER_NAME));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail));
        message.setSubject("野果API积分购买审核通知");

        String htmlContent = "<html><body style=\"margin: 0;\">"
                + "<header style=\"display: block; width: 100%; height: 20px; background-color: #a6559d; padding: 10px;line-height:20px;color:#fff\">"
                + "野果API</header><div style=\"padding: 30px;\">"
                + "<h1>积分购买审核通知</h1>"
                + "<p>订单号：" + orderInfoNotificationRequest.getOrderId() + "</p>"
                + "<p>用户ID：" + orderInfoNotificationRequest.getUserId() + "</p>"
                + "<p>支付金额：" + orderInfoNotificationRequest.getMoney() + "</p>"
                + "<p>支付方式：" + orderInfoNotificationRequest.getPayType() + "</p>"
                + "<p>商品内容：" + orderInfoNotificationRequest.getCommodityContent() + "</p>"
                + "<p>请尽快审核！</p></div></body></html>";

        message.setContent(htmlContent, "text/html;charset=UTF-8");
        return message;
    }

    private static MimeMessage createValidationMail(Session session, String receiveMail) throws MessagingException, UnsupportedEncodingException {
        Random random = new Random();
        verifyCode = "" + (random.nextInt(899999) + 100000);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_SENDER, EMAIL_SENDER_NAME));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail));
        message.setSubject("验证码");

        String htmlContent = "<html><body style=\"margin: 0;\">"
                + "<header style=\"display: block; width: 100%; height: 20px; background-color: #a6559d; padding: 10px;line-height:20px;color:#fff\">"
                + "野果API</header><div style=\"padding: 30px;\">"
                + "<h1>验证码通知</h1>"
                + "<p>您的验证码是：" + verifyCode + "。</p>"
                + "<p>如非本人操作，请忽略此邮件！请勿回复此邮箱。</p></div></body></html>";

        message.setContent(htmlContent, "text/html;charset=UTF-8");
        return message;
    }
}
