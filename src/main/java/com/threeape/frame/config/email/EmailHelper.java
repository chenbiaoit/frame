package com.threeape.frame.config.email;

import com.threeape.frame.bean.MailBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 22:53 2019-07-09
 * @Modified by:
 */
@Component
@Slf4j
public class EmailHelper {

    @Resource
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    @Resource
    private JavaMailSender mailSender;

    /**
     * 模板枚举定义,方便统一更改
     */
    public enum MAIL_TEMPLATE {
        USER_CREATE("mail/createSubAgent");

        private String templateName;

        MAIL_TEMPLATE(String templateName){
            this.templateName = templateName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }

    /**
     * 发送普通邮件
     * @param mailBean
     */
    public void sendMail(MailBean mailBean) {
        this.sendMail(mailBean,false);
    }

    /**
     * 发送静态html页面
     * @param mailBean
     */
    public void sendHtmlMail(MailBean mailBean) {
        String content = templateEngine.process(
                mailBean.getTemplateName(),
                mailBean.getParams() == null ? new Context() : this.getContext(mailBean.getParams()));

        mailBean.setContent(content);

        this.sendMail(mailBean,true);
    }

    /**
     * 发送带附件邮件
     * @param mailBean
     */
    public void sendMailAttachment(MailBean mailBean) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(mailBean.getTos());
            helper.setSubject(mailBean.getSubject());
            String content = templateEngine.process(
                    mailBean.getTemplateName(),
                    mailBean.getParams() == null ? new Context() : this.getContext(mailBean.getParams()));

            helper.setText(content);
            helper.setText(mailBean.getContent(), true);
            helper.addAttachment(mailBean.getAttachmentFilename(), mailBean.getFile());
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送附件类型邮件失败",e);
        }
    }

    /**
     * 发送邮件
     * @param mailBean
     */
    private void sendMail(MailBean mailBean,boolean html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(sender);
            helper.setTo(mailBean.getTos());
            helper.setSubject(mailBean.getSubject());
            helper.setText(mailBean.getContent(),html);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送html类型邮件失败",e);
        }
    }

    private Context getContext(Map<String, Object> paramsMap) {
        Context context = new Context();
        paramsMap.forEach((x,y)-> context.setVariable(x,y));
        return context;
    }

}
