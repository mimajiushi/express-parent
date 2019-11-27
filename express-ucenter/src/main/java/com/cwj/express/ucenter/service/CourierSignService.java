package com.cwj.express.ucenter.service;

import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.ucenter.CourierSignCount;
import com.cwj.express.domain.ucenter.CourierSignData;

import java.time.LocalDate;
import java.util.List;

public interface CourierSignService {

    /**
     * 配送员日常签到接口
     * @param courierId 配送员id
     * @return 操作成功/失败信息
     */
    ResponseResult courierSignNormal(String courierId);

    /**
     * 配送员加班签到接口
     * @param courierId 配送员id
     * @return 操作成功/失败信息
     */
    ResponseResult courierSignOT(String courierId);

    /**
     * 获取配送员日期范围的签到记录
     * @param signDataType 0-日常签到 1-加班签到
     */
    List<CourierSignData> getSignDataList(String courierId, int signDataType, LocalDate startDate, LocalDate endDate);

    /**
     * 获取配送员连签记录
     * @param courierId 配送员id
     * @param type 0-断签记录 1-连签记录
     * @return 连签记录
     */
    CourierSignCount getSignCount(String courierId, int type);

    /**
     * 根据状态和日期查找签到记录数量
     */
    Integer getSignCount(String courierId, int type, LocalDate startDate, LocalDate endDate);



}
