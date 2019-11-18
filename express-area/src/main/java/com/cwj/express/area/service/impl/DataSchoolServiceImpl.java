package com.cwj.express.area.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.area.dao.DataSchoolMapper;
import com.cwj.express.area.service.DataSchoolService;
import com.cwj.express.area.service.RedisService;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.domain.area.DataSchool;
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
public class DataSchoolServiceImpl implements DataSchoolService {

    private final DataSchoolMapper dataSchoolMapper;
    private final RedisTemplate<String, DataSchool> redisTemplate;
    private final RedisService redisService;

    @Override
    public List<DataSchool> getListByProvinceId(Integer provinceId) {
        String key = RedisConfig.SCHOOL_DATA_LIST + "::" + provinceId;
        String value = redisService.get(key);
        if (StringUtils.isEmpty(value)){
            List<DataSchool> dataSchools = dataSchoolMapper.selectList(
                    new QueryWrapper<DataSchool>().eq("province_id", provinceId)
            );
            if (!ObjectUtils.isEmpty(dataSchools)){
                redisService.setKeyValTTL(key, JSON.toJSONString(dataSchools), RedisConfig.AREA_TTL);
            }
            return dataSchools;
        }
        return JSON.parseObject(value, List.class);
    }


    /**
     * 获取所有学校的行政区id (去重)
     */
    @Override
    public List<Integer> selectProvincIdDistinct(){
        return dataSchoolMapper.selectProvincIdDistinct();
    }

    @Override
    public DataSchool getById(String id) {
        String key = RedisConfig.SCHOOL_DATA + "::" + id;
        String value = redisService.get(key);
        if (StringUtils.isEmpty(value)){
            DataSchool dataSchool = dataSchoolMapper.selectById(id);
            redisService.setKeyValTTL(key, JSON.toJSONString(dataSchool), RedisConfig.AREA_TTL);
            return dataSchool;
        }
        return JSON.parseObject(value, DataSchool.class);
    }

    @Override
    public List<DataSchool> getAllSchool() {
        return dataSchoolMapper.selectList(null);
    }
}
