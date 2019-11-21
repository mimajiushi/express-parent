package com.cwj.express.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * 订单删除类型枚举
 */
@Getter
public enum OrderDeleteEnum implements IEnum<Integer> {
    /**
     * 没有被删除
     */
    NONE("没被删除", 0),

    /**
     * 订单撤销
     */
    CANCEL("订单撤销", 1),
    /**
     * 手动删除
     */
    MANUAL("手动删除", 2),
    /**
     * 系统删除
     */
    SYSTEM("系统删除", 3);

    private String name;
    private int type;

    OrderDeleteEnum(String name, int type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }
}
