package com.cwj.express.order.rocketmq;


import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.common.enums.PaymentStatusEnum;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.order.service.OrderPaymentService;
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

/**
 * 订单分配配送员队列
 */
@RocketMQTransactionListener(txProducerGroup = RocketmqConfig.DISTRIBUTION_COURIER_GROUP)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DistributionCourierTransactionListener implements RocketMQLocalTransactionListener {

    private final OrderPaymentService orderPaymentService;
    private final DataSourceTransactionManager transactionManager;

    /**
     * 执行本地事务
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            RocketMQLocalTransactionState rocketMQLocalTransactionState = checkPaymentStatus(msg);
            transactionManager.commit(status);
            return rocketMQLocalTransactionState;
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
        try {
            return checkPaymentStatus(msg);
        }catch (Exception e){
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查订单是否为空及订单是否是已支付
     */
    private RocketMQLocalTransactionState checkPaymentStatus(Message msg){
        MessageHeaders headers = msg.getHeaders();
        String orderId = (String) headers.get("orderId");
        OrderPayment orderPayment = orderPaymentService.getByOrderId(orderId);
        log.debug(orderId);
        if (ObjectUtils.isEmpty(orderPayment)) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        if (PaymentStatusEnum.TRADE_SUCCESS != orderPayment.getPaymentStatus()) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }
}
