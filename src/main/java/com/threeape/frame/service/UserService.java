package com.threeape.frame.service;

import com.threeape.frame.bean.MailBean;
import com.threeape.frame.config.email.EmailHelper;
import com.threeape.frame.entity.system.SysRetrievePassword;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.RetrievePasswordRepository;
import com.threeape.frame.repository.UserRepository;
import com.threeape.frame.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.persistence.criteria.Predicate;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailHelper emailHelper;
    @Autowired
    private RetrievePasswordRepository retrievePasswordRepository;

    private PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     * 分页获取用户信息
     * @param user
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<SysUser> findWithPage(SysUser user, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Specification<SysUser> specification = (Specification<SysUser>) (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(user != null){
                String loginName = user.getLoginName();
                if (!loginName.isEmpty()) {
                    // 此处为查询serverName中含有key的数据
                    list.add(criteriaBuilder.like(root.get("loginName"), "%" + loginName + "%"));
                }
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return userRepository.findAll(specification,pageable).getContent();
    }

    /**
     * 获取所有用户
     * @return
     */
    public List<SysUser> getAllUser(){
        return userRepository.findAll();
    }

    /**
     * 根据用户名查找详细信息
     * @param loginName
     * @return
     */
    public SysUser findUser(String loginName){
        if(StringUtils.isEmpty(loginName)){
            throw new BusinessException(ErrorCodes.SystemManagerEnum.USER_EMPTY_USER_NAME.getCode(),
                    ErrorCodes.SystemManagerEnum.USER_EMPTY_USER_NAME.getZhMsg());
        }
        return userRepository.findByLoginName(loginName);
    }

    /**
     * modifyLifecycle
     * @param loginName
     * @param userId
     * @param userStatus 1 正常 0 冻结
     * @return
     */
    @Transactional
    public void modifyLifecycle(String loginName,Integer userStatus,Integer userId){
        SysUser user = this.findUser(loginName);
        BusinessUtil.notNull(user,ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS);
        user.setUserStatus(userStatus);
        user.setUpdateUserId(userId);
        userRepository.save(user);
    }

    /**
     * 忘记密码-修改密码
     * @param loginName
     * @param sid
     * @param newPwd
     * @return
     */
    @Transactional
    public boolean modifyPwd(String loginName,String sid,String newPwd){

        SysUser user = userRepository.findByLoginName(loginName);
        BusinessUtil.notNull(user, ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS);
        BusinessUtil.notNull(user.getEmail(),ErrorCodes.SystemManagerEnum.USER_EMAIL_INVALID);

        SysRetrievePassword retrievePassword =
                retrievePasswordRepository.findByUseridAndRandomCode(user.getUserId(), sid);

        BusinessUtil.notNull(retrievePassword,ErrorCodes.SystemManagerEnum.USER_FOGET_EMAIL_URL_INVALID);

        log.info("User {} resets the password",user.getLoginName());
        user.setLoginPwd(passwordEncoder.encode(newPwd));
        user.setPwdInvalidTime(DateUtil.addDays(new Date(),365 * 10));
        user.setUpdateUserId(user.getUserId());
        userRepository.save(user);

        retrievePassword.setStatus(0);
        retrievePasswordRepository.save(retrievePassword);
        return true;
    }

    /**
     * 用户-修改密码
     * @param loginName
     * @param oldPwd
     * @param newPwd
     * @param userId
     */
    @Transactional
    public void modifyPwd(String loginName,String oldPwd,String newPwd,Integer userId){
        SysUser user = this.findUser(loginName);
        if(user == null){
            throw new BusinessException(ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS.getCode(),
                    ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS.getZhMsg());
        }
        if(!passwordEncoder.matches(oldPwd,user.getLoginPwd())){
            throw new BusinessException(ErrorCodes.SystemManagerEnum.USER_INVALID_PASSWORD.getCode(),
                    ErrorCodes.SystemManagerEnum.USER_INVALID_PASSWORD.getZhMsg());
        }
        user.setLoginPwd(passwordEncoder.encode(newPwd));
        user.setUpdateUserId(userId);
        userRepository.save(user);
    }

    /**
     * 发送忘记密码邮件
     * @param loginName
     */
    @Transactional
    public boolean sendForgetEmail(String loginName){
        SysUser user = userRepository.findByLoginName(loginName);
        BusinessUtil.notNull(user, ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS);

        MailBean mailBean = new MailBean();
        mailBean.setTemplateName(EmailHelper.MAIL_TEMPLATE.FORGET_PWD.getTemplateName());
        mailBean.setSubject("忘记密码邮件");
        mailBean.setTos(user.getEmail());
        String randomCode = UUID.randomUUID().toString();
        Map<String,Object> map = new HashMap<>();
        map.put("url",String.format("%s/pwd?sid=%s&loginName=%s","",randomCode,loginName));
        map.put("loginName",loginName);
        mailBean.setParams(map);

        emailHelper.sendHtmlMail(mailBean);

        //保存
        Date now = new Date();
        SysRetrievePassword retrievePassword = new SysRetrievePassword();
        retrievePassword.setUserid(user.getUserId());
        retrievePassword.setRandomCode(randomCode);
        retrievePassword.setInvalidTime(DateUtil.addMinutes(now,30));
        retrievePassword.setStatus(1);

        retrievePasswordRepository.save(retrievePassword);
        return true;
    }

    /**
     * 重置密码
     * @param loginName
     * @param currentUser
     */
    @Transactional
    public void resetUserPwd(String loginName,SysUser currentUser){
        SysUser user = this.findUser(loginName);
        if(user == null){
            throw new BusinessException(ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS.getCode(),
                    ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS.getZhMsg());
        }
        user.setUpdateUserId(currentUser.getUserId());
        String newPasswod = PasswordHelper.generateRandomPassword();
        user.setLoginPwd(passwordEncoder.encode(newPasswod));
        userRepository.save(user);

        //发送邮件
        this.sendEmail(user, newPasswod, "密码重置邮件", EmailHelper.MAIL_TEMPLATE.RESET_PWD);
    }

    private void sendEmail(SysUser user, String password, String subject, EmailHelper.MAIL_TEMPLATE userCreate) {
        MailBean mailBean = new MailBean();
        mailBean.setTos(user.getEmail());
        mailBean.setSubject(subject);
        Map<String, Object> map = new HashMap<>();
        map.put("loginName", user.getLoginName());
        map.put("password", password);
        mailBean.setParams(map);
        mailBean.setTemplateName(userCreate.getTemplateName());
        emailHelper.sendHtmlMail(mailBean);
    }

    /**
     * 校验验证码
     * @param verifyCode
     * @param request
     */
    public boolean checkVerifyCode(String verifyCode, HttpServletRequest request){
        //如果不是dev环境,校验验证码
        if(!"dev".equals(SpringContextUtil.getActiveProfile())){
            String sessionVerifyCode = String.valueOf(request.getSession().getAttribute("verifyCode"));
            if(StringUtils.isBlank(verifyCode)
                    || !StringUtils.equals(verifyCode, sessionVerifyCode)){

                return false;
            }
        }
        return true;
    }

    /**
     * 获取验证码
     * @param req
     * @param resp
     */
    public void getVerifyCode(HttpServletRequest req, HttpServletResponse resp){
        // 调用工具类生成的验证码和验证码图片
        Map<String, Object> codeMap = VerifyCodeUtil.generateCodeAndPic();
        // 将四位数字的验证码保存到Session中。
        req.getSession().setAttribute("verifyCode", codeMap.get("code").toString());
        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", -1);
        resp.setContentType("image/jpeg");
        ServletOutputStream sos;
        try {
            sos = resp.getOutputStream();
            ImageIO.write((RenderedImage) codeMap.get("codePic"), "jpeg", sos);
            sos.close();
        } catch (IOException e) {
            log.error("获取验证码异常", e);
            throw new BusinessException(ErrorCodes.SystemManagerEnum.VERIFY_CODE_ERROR);
        }
    }
}
