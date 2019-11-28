package com.cwj.express.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderTypeEnum implements IEnum<Integer> {
    /**
     * 上门取件
     */
    PICK_UP("上门取件", 1),

    /**
     * 代拿快递
     */
    TRANSPORT("送件上门", 0);

    private String desc;
    private int type;

    OrderTypeEnum(String desc, int status) {
        this.desc = desc;
        this.type = status;
    }

    public static OrderTypeEnum getByType(Integer type) {
        return Arrays.stream(values()).filter(e -> e.getType() == type).findFirst().orElse(null);
    }

    @Override
    public Integer getValue() {
        return this.type;
    }
}
