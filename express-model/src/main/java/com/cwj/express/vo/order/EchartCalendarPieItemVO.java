package com.cwj.express.vo.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分析图表数据结构
 * （因为前端有序）无序Map<dateStr, List<VO>>
 * 	List<VO>
 * 		VO 成员变量 name 和 value
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EchartCalendarPieItemVO {
    /**
     * 类型名称
     */
    private String name;

    /**
     * 类型值
     */
    private Integer value;
}
