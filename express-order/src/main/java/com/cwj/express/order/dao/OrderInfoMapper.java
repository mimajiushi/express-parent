package com.cwj.express.order.dao;

import com.cwj.express.domain.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.express.vo.order.CourierRankVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * @author 森七柚子茶
     * @return 配送员id、完成单数，从大到小
     */
    @Select("select courier_id,count(courier_id) sum from order_info where (status='3') and (update_date between #{startTime} and #{endTime} ) group by courier_id order by sum")
    List<CourierRankVO> selectCourierRank(@Param("startTime") String startTime, @Param("endTime") String endTime);

}
