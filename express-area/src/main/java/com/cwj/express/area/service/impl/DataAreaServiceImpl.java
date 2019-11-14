package com.cwj.express.area.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.area.dao.DataAreaMapper;
import com.cwj.express.area.service.DataAreaService;
import com.cwj.express.area.service.RedisService;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.domain.area.DataArea;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataAreaServiceImpl implements DataAreaService {

    private final DataAreaMapper dataAreaMapper;
    private final RedisService redisService;

    @Override
    public List<DataArea> getAreasByParentId(Integer parentId) {
        String key = RedisConfig.AREA_HEAD + ":" + parentId;
        String redisRes = redisService.get(key);
        if (StringUtils.isEmpty(redisRes)){
            List<DataArea> dataAreas = dataAreaMapper.
                    selectList(
                            new QueryWrapper<DataArea>().select("parent_id", "name").eq("parent_id", parentId).orderByAsc("sort"));
            if (!ObjectUtils.isEmpty(dataAreas)){
                String value = JSON.toJSONString(dataAreas);
                redisService.set(key, value);
            }
            return dataAreas;
        }
        return JSON.parseObject(redisRes, List.class);
    }

    @Override
    public List<Integer> selectParentIdDistinct() {
        return dataAreaMapper.selectParentIdDistinct();
    }
}
