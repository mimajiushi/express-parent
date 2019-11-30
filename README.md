# express-parent
正在做:
--  
项目核心功能简述：   
-- 在此之前最好先看下notes、plan目录下的txt文件
本项目是一个自己臆想出需求(当然，大部分以课程设计文档需求为主)的大学快递代拿服务系统，   
后端使用了springboot，mybatis-plus，redis(含lua)，rocketmq等进行功能开发实现。
除此之外也整合了nacos，sentinel实现了简单的微服务   
至于前端，本人前端水平有限，只能使用老一套的jq，bootstrap实现   

各组件使用情况：
--
springboot作为项目使用的基础建设框架
mybatis-plus作为crud核心(为什么不用tk？因为mybatis-plus的使用更为简单，尤其是乐观锁、逻辑删除等)   
spring security+oauth2使用rsa加密实现认证中心(我这里没有使用io.jsonwebtoken，实际上应该使用，nacos的源码也有使用，我没使用只是为了更好的熟悉security的部分源码)      
redis除了用作基本缓存以外，还是程序实现自动分配配送员的核心（详细看plan目录下的编写计划），简单概括下就是zset+lua实现区域按权重分配配送员   
rocketmq则是用作订单超时自动取消、用户支付后发送分配配送员的事务消息，用户评价后发送分数校准的事务消息
nacos作为注册中心服务发现   
sentinel作为服务熔断/限流降级   
实现了配送员签到/加班功能，能统计连签天数   
也实现了不同会员下单时打折的功能   

额外说明：
--
启动express-order之前要启动express-ucenter，   
因为初始化配送员权重的代码写到order去了，order需要远程调用ucenter。   
代码还可能出现大改   
2019/11/22日之前rocketmq的两阶段提交还没学透，所以在这之前写的rocketmq事务处理方式是错的   
账号到数据库看，密码不是 mimajiushi 就是 123   
express-auth 暂时没实现注册，登录页面 http://localhost:40400/page/index   
项目启动需要nacos是启动的，nacos的ip改成自己的， sentinel暂时可有可无    
项目中的反馈功能还未实现，因为只是简单的crud没什么新知识，所以暂时放着不管
   
    
最近要考试，除了课程设计可能漏掉的功能以外，停止更新

# --------------------------示例图-------------------------------

![Image text](<http://www.wenjie.store/express-parent%E4%B8%8B%E5%8D%95%E9%A1%B5.png>)
![Image text](<http://www.wenjie.store/express-parent%E5%AE%8C%E6%88%90%E8%AE%A2%E5%8D%95.png>)
![Image text](<http://www.wenjie.store/express-parent%E6%97%B6%E9%97%B4%E6%9F%A5%E8%AF%A2%E8%8C%83%E5%9B%B4%E5%86%85%E4%B8%89%E7%A7%8D%E8%AE%A2%E5%8D%95%E7%9A%84%E4%BA%A4%E6%98%93%E6%83%85%E5%86%B5.png>)
![Image text](<http://www.wenjie.store/express-parent%E6%97%B6%E9%97%B4%E6%9F%A5%E8%AF%A2%E9%85%8D%E9%80%81%E5%91%98%E5%AE%8C%E6%88%90%E5%8D%95%E6%95%B0.png>)
![Image text](<http://www.wenjie.store/express-parent%E6%9F%A5%E7%9C%8B%E8%AE%A2%E5%8D%95%E8%AF%A6%E6%83%85.png>)
![Image text](<http://www.wenjie.store/express-parent%E6%9F%A5%E7%9C%8B%E8%AE%A2%E5%8D%95%E9%A1%B5.png>)
![Image text](<http://www.wenjie.store/express-parent%E7%94%A8%E6%88%B7%E8%AF%84%E4%BB%B7%E6%9F%A5%E7%9C%8B.png>)
![Image text](<http://www.wenjie.store/express-parent%E7%99%BB%E5%BD%95%E5%90%8E%E9%A6%96%E9%A1%B5.png>)
![Image text](<http://www.wenjie.store/express-parent%E7%AE%A1%E7%90%86%E5%91%98%E6%9F%A5%E7%9C%8B%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8.png>)
![Image text](<http://www.wenjie.store/express-parent%E7%AE%A1%E7%90%86%E5%91%98%E6%9F%A5%E7%9C%8B%E7%94%A8%E6%88%B7%E7%AD%BE%E5%88%B0.png>)
![Image text](<http://www.wenjie.store/express-parent%E7%AE%A1%E7%90%86%E5%91%98%E6%9F%A5%E7%9C%8B%E8%AE%A2%E5%8D%95%E5%88%97%E8%A1%A8.png>)
![Image text](<http://www.wenjie.store/express-parent%E7%AE%A1%E7%90%86%E5%91%98%E9%A6%96%E9%A1%B5.png>)
![Image text](<http://www.wenjie.store/express-parent%E8%87%AA%E5%B7%B1%E4%BF%A1%E6%81%AF%E6%9F%A5%E7%9C%8B.png>)
![Image text](<http://www.wenjie.store/express-parent%E8%AE%A2%E5%8D%95%E5%BC%82%E5%B8%B8.png>)
![Image text](<http://www.wenjie.store/express-parent%E9%85%8D%E9%80%81%E5%91%98%E6%9F%A5%E7%9C%8B%E8%AE%A2%E5%8D%95%E5%88%97%E8%A1%A8.png>)
![Image text](<http://www.wenjie.store/express-parent%E9%85%8D%E9%80%81%E5%91%98%E6%9F%A5%E7%9C%8B%E8%AF%84%E4%BB%B7.png>)
![Image text](<http://www.wenjie.store/express-parent%E9%85%8D%E9%80%81%E5%91%98%E7%AD%BE%E5%88%B0.png>)
![Image text](<http://www.wenjie.store/express-parent%E9%85%8D%E9%80%81%E5%91%98%E9%A6%96%E9%A1%B5.png>)
