package com.cwj.express.order.feignclient.area;

import com.cwj.express.domain.area.DataCompany;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AreaFeignClientFallbackFactory implements FallbackFactory<AreaFeignClient> {
    @Override
    public AreaFeignClient create(Throwable throwable) {
        return new AreaFeignClient() {
            @Override
            public DataCompany getCompanyById(Integer id) {
                log.info("获取快递公司信息异常， 快递公司id:{}，异常信息:{}", id, throwable.getMessage());
                return DataCompany.builder().name("获取快递公司信息异常!!!请联系网站管理员").build();
            }
        };
    }

}
