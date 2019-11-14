package com.cwj.express.area.controller;

import com.cwj.express.api.area.AreaControllerApi;
import com.cwj.express.area.service.DataAreaService;
import com.cwj.express.area.service.DataCompanyService;
import com.cwj.express.area.service.DataSchoolService;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.area.DataArea;
import com.cwj.express.domain.area.DataCompany;
import com.cwj.express.domain.area.DataSchool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AreaController extends BaseController implements AreaControllerApi {

    private final DataAreaService dataAreaService;
    private final DataCompanyService dataCompanyService;
    private final DataSchoolService dataSchoolService;

    /**
     * 根据id获取行政区信息
     */
    @GetMapping("/{id}/child")
    @Override
    public List<DataArea> getAreaDataByParentId(@PathVariable Integer id) throws Exception {
        List<DataArea> dataAreas = dataAreaService.getAreasByParentId(id);
        return dataAreas;
    }

    /**
     * 获取所有快递公司的数据
     */
    @GetMapping("/companyList")
    @Override
    public List<DataCompany> listCompany(){
        return dataCompanyService.listAll();
    }

    /**
     * 根据id获取快递公司数据
     */
    @GetMapping("/company/{id}")
    @Override
    public DataCompany getCompanyById(@PathVariable Integer id){
        return dataCompanyService.getById(id);
    }

    /**
     * 根据行政id获取该区学校列表
     */
    @GetMapping("/school/{provinceId}")
    @Override
    public List<DataSchool> getSchoolByProvinceId(@PathVariable Integer provinceId) {
        return dataSchoolService.getListByProvinceId(provinceId);
    }


    /**
     * 测试mvc是否生效的接口
     */
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
