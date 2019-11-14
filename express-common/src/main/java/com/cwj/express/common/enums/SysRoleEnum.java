package com.cwj.express.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.Arrays;

/**
 * 角色枚举(和数据库对应)
 * @author chenwenjie
 * @date 2019年04月17日 0:11
 * 因为对于一个正常的系统来说，这块的改动应该是几乎没有的，所以就写成枚举
 * 不过到订单等关键模块还是要强校验
 */
@Getter
public enum SysRoleEnum implements IEnum<Integer> {
    DIS_FORMAL(-1, "ROLE_DIS_FORMAL", "非正式用户"),
    ADMIN(1, "ROLE_ADMIN", "系统管理员"),
    COURIER(2, "ROLE_COURIER", "配送员"),
    USER(3, "ROLE_USER", "普通用户"),
    VIP_USER(4, "ROLE_VIP_USER", "vip邮客"),
    SVIP_USER(5, "ROLE_SVIP_USER", "集团邮客")
    ;

    private int type;

    private String name;

    private String cnName;

    SysRoleEnum(int type, String name, String cnName) {
        this.type = type;
        this.name = name;
        this.cnName = cnName;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }

    public static SysRoleEnum getByType(int type) {
        return Arrays.stream(values()).filter(e -> e.getType() == type).findFirst().orElse(null);
    }

    public static SysRoleEnum getByName(String name) {
        return Arrays.stream(values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
}
