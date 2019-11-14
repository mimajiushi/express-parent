package com.cwj.express.domain.auth;

import com.cwj.express.domain.ucenter.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author root
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserExt extends SysUser {
    private String authType;

    public SysUserExt(SysUser sysUser){
        super(sysUser);
    }
}
