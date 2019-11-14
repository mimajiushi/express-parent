package com.cwj.express.area.service;

import com.cwj.express.domain.area.DataCompany;

import java.util.List;

public interface DataCompanyService{
    /**
     * 获取所有快递公司的信息
     * @return 快递公司信息列表
     */
    List<DataCompany> listAll();

    /**
     * 根据id获取快递公司信息(缓存实现由 LoadingCache 换成 redis)
     * @param id 快递公司id
     * @return 快递公司信息
     */
    DataCompany getById(Integer id);
}
