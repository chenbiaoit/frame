package com.threeape.frame.entity.system;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity(name = "t_sys_operation_log")
public class SysOperationLog {

    @Id
    @GeneratedValue
    private Integer id;
    private String operator;
    private String url;
    private String invoke;
    private String businessKey;
    private String errorMsg;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime;
}
