package com.cwj.express.vo.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvaluateItemVO {
    private String orderId;
    private BigDecimal score;
    private String evaluate;
}
