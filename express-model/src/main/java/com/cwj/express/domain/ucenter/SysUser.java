package com.cwj.express.domain.ucenter;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.*;
import com.cwj.express.common.enums.SexEnum;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.enums.ThirdLoginTypeEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 可以考虑继承org.springframework.security.core.userdetails.User
 * @author cwj
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements UserDetails, CredentialsContainer, Serializable {
    @TableId(type = IdType.UUID)
    private String id;

    private String username;

    @JsonIgnore
    private String password;

    /**
     * 人脸唯一标识
     */
    @JsonIgnore
    private String faceToken;
    /**
     * 用户角色
     */
    @TableField("role_id")
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private SysRoleEnum role;
    /**
     * 性别
     */
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private SexEnum sex;

    /**
     * 真实姓名
     */
//    @JsonIgnore
    private String realName;

    /**
     * 身份证号
     */
    @JsonIgnore
    private String idCard;

    /**
     * 学生证号
     */
    private String studentIdCard;


    private String tel;
    /**
     * 学校
     */
    private Integer schoolId;
    /**
     * 三方登陆类型
     */
    @TableField("third_login_type")
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private ThirdLoginTypeEnum thirdLogin;
    /**
     * 三方登陆ID
     */
    @JsonIgnore
    private String thirdLoginId;
    /**
     * 是否启用
     * 1：启用；0：禁用
     */
    private Integer hasEnable;
    /**
     * 解冻时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lockDate;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;


    @Version
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;

    public SysUser(SysUser sysUser) {
        this.id = sysUser.getId();
        this.username = sysUser.getUsername();
        this.password = sysUser.getPassword();
        this.faceToken = sysUser.getFaceToken();
        this.role = sysUser.getRole();
        this.sex = sysUser.getSex();
        this.realName = sysUser.getRealName();
        this.idCard = sysUser.getIdCard();
        this.studentIdCard = sysUser.getStudentIdCard();
        this.tel = sysUser.getTel();
        this.schoolId = sysUser.getSchoolId();
        this.thirdLogin = sysUser.getThirdLogin();
        this.thirdLoginId = sysUser.getThirdLoginId();
        this.hasEnable = sysUser.getHasEnable();
        this.lockDate = sysUser.getLockDate();
        this.createDate = sysUser.getCreateDate();
        this.updateDate = sysUser.getUpdateDate();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>(){{
            add(new SimpleGrantedAuthority(getRole().getName()));
        }};
    }

    /**
     * 是否未冻结
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        if(ObjectUtils.isEmpty(this.lockDate)) {
            return true;
        }

        return LocalDateTime.now().isAfter(this.lockDate);
//        return true;
    }
    /**
     * 是否启用
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.hasEnable == 1;
    }

    @Override
    @JsonIgnore
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }
}
