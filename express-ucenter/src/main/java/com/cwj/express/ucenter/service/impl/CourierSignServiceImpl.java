package com.cwj.express.ucenter.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.ucenter.CourierSignCount;
import com.cwj.express.domain.ucenter.CourierSignData;
import com.cwj.express.ucenter.dao.CourierSignCountMapper;
import com.cwj.express.ucenter.dao.CourierSignDataMapper;
import com.cwj.express.ucenter.service.CourierSignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class CourierSignServiceImpl implements CourierSignService {
    private final CourierSignCountMapper courierSignCountMapper;
    private final CourierSignDataMapper courierSignDataMapper;


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseResult courierSignNormal(String courierId) {
        // 获取时间
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate now = nowDateTime.toLocalDate();
        // 19：00：00~23：59：59是加班时间
        LocalDate tor = now.plusDays(1);
        LocalDate yes = now.plusDays(-1);
        // 检查是否进行日常签到了
        List<CourierSignData> signDataList = getSignDataList(courierId, 0, now, tor);
        if (signDataList.size() > 0){
            return ResponseResult.FAIL(CommonCode.COURIER_SIGN_NORMAL_EXIST);
        }
        // 进行日常签到
        // 先查看有无前一天的签到记录
        List<CourierSignData> signDataList1 = getSignDataList(courierId, 0, yes, now);
        // 如果存在前一天记录，则取连签记录+1
        CourierSignCount updateSignCount = null;
        if (signDataList1.size() > 0){
            // 乐观锁更新
            CourierSignCount signCount = getSignCount(courierId, 1);
            signCount.setSignCount(signCount.getSignCount() + 1);
            updateSignCount = signCount;
        }else {
            // 创建新的签到记录，如果有旧的连签记录则
            updateSignCount = CourierSignCount.builder()
                    .courierId(courierId)
                    .signCount(1)
                    .signCountType(1)
                    .createDate(nowDateTime)
                    .updateDate(nowDateTime).build();
            // 获取旧的连签记录，如果存在则设置为断签记录
            CourierSignCount signCountBefore = getSignCount(courierId, 1);
            if (!ObjectUtils.isEmpty(signCountBefore)){
                signCountBefore.setSignCountType(0);
                courierSignCountMapper.updateById(signCountBefore);
            }
        }
        int updateCount = courierSignCountMapper.updateById(updateSignCount);
        if (updateCount < 1){
            return ResponseResult.FAIL(CommonCode.COURIER_SIGN_DATA_CHANGED);
        }
        return ResponseResult.SUCCESS();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult courierSignOT(String courierId) {
        // 获取时间
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate now = nowDateTime.toLocalDate();
        LocalDate tor = now.plusDays(1);
        // 19：00：00~00：00：00 是加班时间
        LocalDateTime[] limitDate = new LocalDateTime[]{now.atTime(19,0,0), now.atTime(0,0,0)};
        if (nowDateTime.isBefore(limitDate[0]) || nowDateTime.isBefore(limitDate[1])){
            return ResponseResult.FAIL(CommonCode.COURIER_SIGNOT_TIME_ERROER);
        }else {
            List<CourierSignData> signDataNormal = getSignDataList(courierId, 0, now, tor);
            if (signDataNormal.size() < 1){
                return ResponseResult.FAIL(CommonCode.COURIER_SIGN_NOTMAL_NOT_EXIST);
            }
            // 查询今天加班签到数据
            List<CourierSignData> signDataList = getSignDataList(courierId, 1, now, tor);
            if (signDataList.size() > 0){
                return ResponseResult.FAIL(CommonCode.COURIER_SINGOT_EXIST);
            }
            courierSignDataMapper.insert(
                    CourierSignData.builder()
                            .courierId(courierId).signDataType(1).signDate(nowDateTime).build());
            return ResponseResult.SUCCESS();
        }
    }

    @Override
    public List<CourierSignData> getSignDataList(String courierId, int signDataType, LocalDate startDate, LocalDate endDate) {
        return courierSignDataMapper.selectList(new QueryWrapper<CourierSignData>()
                .eq("courier_id", courierId)
                .eq("sign_data_type", signDataType)
                .between("sign_date", startDate.toString(), endDate.toString()));
    }

    @Override
    public CourierSignCount getSignCount(String courierId, int type) {
        return courierSignCountMapper.selectOne(new QueryWrapper<CourierSignCount>()
                .eq("courier_id", courierId)
                .eq("sign_count_type", type)
        .orderByDesc("update_date"));
    }
}
