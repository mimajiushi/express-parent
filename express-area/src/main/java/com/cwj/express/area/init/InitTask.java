package com.cwj.express.area.init;

import com.alibaba.fastjson.JSON;
import com.cwj.express.area.service.DataAreaService;
import com.cwj.express.area.service.DataCompanyService;
import com.cwj.express.area.service.DataSchoolService;
import com.cwj.express.area.service.RedisService;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.domain.area.DataArea;
import com.cwj.express.domain.area.DataCompany;
import com.cwj.express.domain.area.DataSchool;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InitTask {
    private final DataAreaService dataAreaService;
    private final DataCompanyService dataCompanyService;
    private final DataSchoolService dataSchoolService;
    private final RedisTemplate<String, DataCompany> redisTemplate;
    private final RedisService redisService;
    private final Integer CACHE_TIME = 60*30;

    /**
     * 定时刷新缓存 半小时刷一次
     * todo 如果数据量过多则不宜集中加载，不然ttl过期后导致集中删除容易引发灾难
     * todo 另外如果这个应用是集群应用，跟推荐使用本地缓存的方法
     *      实现的思路是：获取数据时先从本地缓存获取，如果没有，则redis获取(前提是有)，
     *                   返回给客户端，异步本地缓存
     *                   (本地：无， redis： 有)
     *
     *                   本地和redis都没有，则从数据库查，如果有就返回并异步双缓存
     *                   如果数据库没有，则不异步双缓存，直接返回空
     *                   (本地：无， redis： 无)
     */
    @PostConstruct
    private void init() {
        // 数据加载线程池， https://www.jianshu.com/p/502f9952c09b
        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1,
                new ThreadFactoryBuilder().setNameFormat("data-company-loader").build());
        executorService.scheduleWithFixedDelay(() -> {

            /*
             * 加载全国行政区信息
             */
            log.info("开始加载快递公司数据...");
            List<Integer> parentIdDistinct = dataAreaService.selectParentIdDistinct();
            for (Integer parentId : parentIdDistinct) {
                String key = RedisConfig.AREA_HEAD + "::" + parentId;
                List<DataArea> areasByParentId = dataAreaService.getAreasByParentId(parentId);
                redisService.setKeyValTTL(key, JSON.toJSONString(areasByParentId), RedisConfig.AREA_TTL);
            }
            log.info("加载快递公司数据结束...");


            /*
             * 加载快递公司缓存数据
             */
            log.info("开始加载快递公司数据...");
            List<DataCompany> dataCompanies = dataCompanyService.listAll();
            for (DataCompany dataCompany : dataCompanies) {
                String key = RedisConfig.COMPANY_DATA + "::" + dataCompany.getId();
                redisService.setKeyValTTL(key, JSON.toJSONString(dataCompany), RedisConfig.AREA_TTL);
            }
            redisTemplate.delete(RedisConfig.COMPANY_DATA_LIST);
            redisTemplate.opsForList().rightPushAll(RedisConfig.COMPANY_DATA_LIST, dataCompanies);
            log.info("快递公司数据加载结束...");

            /*
             * 加载学校信息缓存
             */
            log.info("开始加载校园数据...");
            List<Integer> provincIdDistinct = dataSchoolService.selectProvincIdDistinct();
            for (Integer  provincId : provincIdDistinct) {
                String key = RedisConfig.SCHOOL_DATA_LIST + "::" + provincId;
                List<DataSchool> listByProvinceId = dataSchoolService.getListByProvinceId(provincId);
                redisService.setKeyValTTL(key, JSON.toJSONString(listByProvinceId), RedisConfig.AREA_TTL);
            }

            List<DataSchool> allSchool = dataSchoolService.getAllSchool();
            for (DataSchool dataSchool : allSchool) {
                String key = RedisConfig.SCHOOL_DATA + "::" + dataSchool.getId();
                redisService.setKeyValTTL(key, JSON.toJSONString(dataSchool), RedisConfig.AREA_TTL);
            }
            log.info("加载校园数据结束...");
        }, 0, 1, TimeUnit.DAYS);



    }
}
