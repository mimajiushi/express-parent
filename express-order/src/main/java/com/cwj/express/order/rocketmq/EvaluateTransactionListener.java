package com.cwj.express.order.rocketmq;


import com.alibaba.fastjson.JSON;
import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.common.enums.PaymentStatusEnum;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderEvaluate;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.order.service.OrderEvaluateService;
import com.cwj.express.order.service.OrderPaymentService;
import com.cwj.express.vo.order.UpdateOrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 订单分配配送员队列
 */
@RocketMQTransactionListener(txProducerGroup = RocketmqConfig.EVALUATE_SCORE_GROUP)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EvaluateTransactionListener implements RocketMQLocalTransactionListener {

    private final OrderEvaluateService orderEvaluateService;
    private final DataSourceTransactionManager transactionManager;

    /**
     * 执行本地事务
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        MessageHeaders headers = msg.getHeaders();
        try {
            String userId = (String)headers.get("userId");
            String orderId = (String)headers.get("orderId");
            SysRoleEnum role = SysRoleEnum.getByType(Integer.parseInt(headers.get("role")+""));
            BigDecimal score = new BigDecimal((String) Objects.requireNonNull(headers.get("score")));
            String evaluate = (String)headers.get("evaluate");
            ResponseResult res = orderEvaluateService.evaluate(orderId, userId, score, evaluate, role);
            if (10000 != res.getCode()){
                log.error("评分操作异常！用户id：{}，角色：{}，订单号：{}，错误信息：{}", userId, role.getCnName(), orderId, String.valueOf(res.getData()));
                transactionManager.rollback(status);
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            transactionManager.commit(status);
            return RocketMQLocalTransactionState.COMMIT;
        }catch (Exception e){
            e.printStackTrace();
            transactionManager.rollback(status);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 本地事务检查接口
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        // String userId = (String)headers.get("userId");
        String orderId = (String)headers.get("orderId");
        SysRoleEnum role = SysRoleEnum.getByType(Integer.parseInt(headers.get("role")+""));
        // BigDecimal score = new BigDecimal((String) Objects.requireNonNull(headers.get("score")));
        // String evaluate = (String)headers.get("evaluate");
        OrderEvaluate orderEvaluate = orderEvaluateService.getById(orderId);
        if (ObjectUtils.isEmpty(orderEvaluate)){
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        if (SysRoleEnum.COURIER == role){
            if (StringUtils.isEmpty(orderEvaluate.getCourierId())){
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        }else {
            if (StringUtils.isEmpty(orderEvaluate.getUserId())){
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

}
