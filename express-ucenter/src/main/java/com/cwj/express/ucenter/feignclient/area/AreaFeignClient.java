package com.cwj.express.ucenter.feignclient.area;

import com.cwj.express.common.constant.ExpressServiceListConstant;
import com.cwj.express.domain.area.DataCompany;
import com.cwj.express.domain.area.DataSchool;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author cwj
 * 订单服务调用
 * 提醒： fallbackFactory可以拿到异常， fallback不可以
 */

@FeignClient(
        name = ExpressServiceListConstant.EXPRESS_AREA,
        fallbackFactory = AreaFeignClientFallbackFactory.class
)
public interface AreaFeignClient {
    @GetMapping("/area/company/{id}")
    public DataCompany getCompanyById(@PathVariable("id") Integer id);

    @GetMapping("/area/school/info/{schoolId}")
    public DataSchool getSchoolInfoById(@PathVariable("schoolId") String schoolId);
}
