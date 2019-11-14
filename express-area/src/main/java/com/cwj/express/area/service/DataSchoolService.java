package com.cwj.express.area.service;

import com.cwj.express.domain.area.DataSchool;

import java.util.List;

public interface DataSchoolService{
//    boolean isExist(Integer id);

    /**
     * 根据行政id查询对应区的学校信息列表
     */
    List<DataSchool> getListByProvinceId(Integer provinceId);

    /**
     * 获取学校行政区id (province_id)
     */
    List<Integer> selectProvincIdDistinct();

}
