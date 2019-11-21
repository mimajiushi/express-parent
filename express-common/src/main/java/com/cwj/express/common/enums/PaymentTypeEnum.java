package com.cwj.express.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PaymentTypeEnum implements IEnum<Integer> {
    /**
     * 支付宝支付
     */
    AliPay(1, "支付宝");

    private int type;

    private String name;

    PaymentTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }
}
