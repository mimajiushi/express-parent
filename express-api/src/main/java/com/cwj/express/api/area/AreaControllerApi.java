package com.cwj.express.api.area;


import com.cwj.express.domain.area.DataArea;
import com.cwj.express.domain.area.DataCompany;
import com.cwj.express.domain.area.DataSchool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api(value="获取地址相关api",tags = "地址controller")
public interface AreaControllerApi {

    @ApiOperation("获取地址列表")
    public List<DataArea> getAreaDataByParentId(@PathVariable Integer id) throws Exception;

    @ApiOperation("获取所有快递公司信息列表")
    public List<DataCompany> listCompany();


    @ApiOperation("根据id获取快递公司信息列表")
    public DataCompany getCompanyById(@PathVariable Integer id);

    @ApiOperation("根据行政id（provinceId）获取该区的学校信息列表")
    public List<DataSchool> getSchoolByProvinceId(@PathVariable Integer provinceId);

    @ApiOperation("根据id获取学校详细信息")
    public DataSchool getSchoolInfoById(@PathVariable String schoolId);
}
