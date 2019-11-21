package com.cwj.express.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.*;

/**
 * <p>
 * 配送员请假日志对象
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-21
 */
@Getter
public enum  CourierLeaveStatusEnum implements IEnum<Integer> {

    LEAVING(1, "请假中"),
    RETURNED(0, "已回岗位")
    ;

    private int status;

    private String cname;

    CourierLeaveStatusEnum(int status, String cname) {
        this.status = status;
        this.cname = cname;
    }

    @Override
    public Integer getValue() {
        return status;
    }
}
