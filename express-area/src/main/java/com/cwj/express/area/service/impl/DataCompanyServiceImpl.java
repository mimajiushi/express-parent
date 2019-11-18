package com.cwj.express.area.service.impl;

import com.alibaba.fastjson.JSON;
import com.cwj.express.area.dao.DataCompanyMapper;
import com.cwj.express.area.service.DataCompanyService;
import com.cwj.express.area.service.RedisService;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.domain.area.DataCompany;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataCompanyServiceImpl implements DataCompanyService {

    private final DataCompanyMapper dataCompanyMapper;
    private final RedisTemplate<String, DataCompany> redisTemplate;
    private final RedisService redisService;

    @Override
    public List<DataCompany> listAll() {
        List<DataCompany> list = redisTemplate.opsForList().range(RedisConfig.COMPANY_DATA_LIST, 0, -1);
        if (ObjectUtils.isEmpty(list)){
            list = dataCompanyMapper.selectList(null);
            redisTemplate.opsForList().rightPushAll(RedisConfig.COMPANY_DATA_LIST, list);
        }
        return  list;
    }

    @Override
    public DataCompany getById(Integer id) {
        String key = RedisConfig.COMPANY_DATA + "::" + id;
        String value = redisService.get(key);
        if (StringUtils.isEmpty(value)){
            DataCompany dataCompany = dataCompanyMapper.selectById(id);
            if (!ObjectUtils.isEmpty(dataCompany)){
                redisService.setKeyValTTL(key, JSON.toJSONString(dataCompany), RedisConfig.AREA_TTL);
            }
            // todo 其它服务调用注意可能出现空的状况
            return dataCompany;
        }
        return dataCompanyMapper.selectById(id);
    }

}
