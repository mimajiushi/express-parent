package com.cwj.express.area.service;

import com.cwj.express.domain.area.DataArea;

import java.util.List;

public interface DataAreaService {

    /**
     * 获取id下的所有地址
     * @param parentId parentId
     * @return 地址信息列表
     */
    List<DataArea> getAreasByParentId(Integer parentId);

    /**
     * 查询所有行政id(parent_id)
     */
    List<Integer> selectParentIdDistinct();
}
