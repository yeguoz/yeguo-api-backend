package icu.yeguo.yeguoapi.utils;

import com.sun.mail.util.MailSSLSocketFactory;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {
    public static String verifyCode = "";
    /**
     * 发送验证码
     *
     * @param receiveMail 邮件接收者
     *  Exception
     */
    public static Integer sendMail(String receiveMail) {
        Properties props = new Properties();
        // 开启debug调试，以便在控制台查看
        props.setProperty("mail.debug", "false");
        // 设置邮件服务器主机名
        props.setProperty("mail.host", "smtp.163.com");
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        // 开启SSL加密，否则会失败
        Message message;
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", sf);
            // 创建session
            Session session = Session.getInstance(props);
            // 通过session得到transport对象
            Transport ts = session.getTransport();
            // 连接邮件服务器：邮箱类型，帐号，POP3/SMTP协议授权码 163使用：smtp.163.com，qq使用：smtp.qq.com
            ts.connect("smtp.163.com", "aidjajd@163.com", "WJUBMWYGVQTOZCQX");
            // 创建邮件
            message = createSimpleMail(session, receiveMail);
            // 发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * @Method: createSimpleMail
     * @Description: 创建一封只包含文本的邮件
     */
    public static MimeMessage createSimpleMail(Session session, String receiveMail) throws MessagingException, UnsupportedEncodingException {
        String sender = "aidjajd@163.com";
        String senderName = "yg-API 接口开放平台";
        Random random = new Random();

        // 生成6位随机验证码
        verifyCode = ""+(random.nextInt(899999) + 100000);
        System.out.println("verifyCode:"+verifyCode);
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        message.setFrom(new InternetAddress(sender,senderName));
        // 指明邮件的收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail));
        // 邮件的标题
        message.setSubject("验证码");

        // 邮件的文本内容（包含随机字母和验证码）
        String htmlContent = "<html>" +
                "<body style=\"margin: 0;\">" +
                "<header style=\"display: block; width: 100%; height: 20px; background-color: #a6559d; padding: 10px;line-height:20px;color:#fff\">" +
                "野果API" +
                "</header>" +
                "<div style=\"padding: 30px;\">"+
                "<h1>验证码通知</h1>" +
                "<p>您的验证码是：" + verifyCode + "。</p>" +
                "<p>如非本人操作，请忽略此邮件！请勿回复此邮箱。</p>" +
                "</div>" +
                "</body>" +
                "</html>";
        message.setContent(htmlContent, "text/html;charset=UTF-8");
        // 返回创建好的邮件对象
        return message;
    }

    public static void main(String[] args) {
        sendMail("1419593965@qq.com");
    }

}