package com.threeape.frame.service;

import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.UserRepository;
import com.threeape.frame.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

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
            String loginName = user.getLoginName();
            if (!loginName.isEmpty()) {
                // 此处为查询serverName中含有key的数据
                list.add(criteriaBuilder.like(root.get("loginName"), "%" + loginName + "%"));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return userRepository.findAll(specification,pageable).getContent();
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
        user.setUpdateTime(DateUtil.getCurrentTS());
        user.setUpdateUserId(userId);
        userRepository.save(user);
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
}
