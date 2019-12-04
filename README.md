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
1. springboot作为项目使用的基础建设框架    
2. mybatis-plus作为crud核心(为什么不用tk？因为mybatis-plus的使用更为简单，尤其是乐观锁、逻辑删除等)    
3. spring security+oauth2使用rsa加密实现认证中心(我这里没有使用io.jsonwebtoken，实际上应该使用，nacos的源码也有使用jjwt，我没使用只是为了更好的熟悉security的部分源码)      
4. redis除了用作基本缓存以外，还是程序实现自动分配配送员的核心（详细看plan目录下的编写计划），简单概括下就是zset+lua实现区域按权重分配配送员   
5. rocketmq则是用作订单超时自动取消、用户支付后发送分配配送员的事务消息，用户评价后发送分数校准的事务消息   
6. nacos作为注册中心服务发现   
7. sentinel作为服务熔断/限流降级    
8. 实现了配送员签到/加班功能，能统计连签天数    
9. 也实现了不同会员下单时打折的功能    

额外说明：
--
1. 启动express-order之前要启动express-ucenter，   
2. 因为初始化配送员权重的代码写到order去了，order需要远程调用ucenter。   
3. 代码还可能出现大改   
4. 2019/11/22日之前rocketmq的两阶段提交还没学透，所以在这之前写的rocketmq事务处理方式是错的   
5. 账号到数据库看，密码不是 mimajiushi 就是 123   
6. express-auth 暂时没实现注册，登录页面 http://localhost:40400/page/index   
7. 项目启动需要nacos是启动的，nacos的ip改成自己的， sentinel暂时可有可无    
8. 项目中的反馈功能还未实现，因为只是简单的crud(包括某些查询)没什么新知识，所以暂时放着不管
9. 本项目偏向于个人的未知领域探索实现(绝对不是因为偷懒，笑)，比如分布式事务如何保证最终一致性等，所以对个别功能定了计划但并没有基于实现，   
    比如：百度地图的区域可视化，收获地址管理等（以前的作品都实现过的功能我就不再实现了）

缺点：
--
1. 部份表可能显得比较肿，主要原因还是时间有限，不想重复过多无意义的dao代码    
2. 我前端比较垃圾    
3. 授权粒度只细化到角色级别，白话就是角色即权限，这么做一方面本项目没有前后分离也没有实现前后分离的动态路由，所以没必要   
4. 一些开源组件实现可能还存在比较细节的错误    

一些容易出错的坑和项目主要关注的点：
--
1. 一个大坑就是rocketmq，我是现学现用的，曾因为理论不完善而导致代码多次出现大改，   
    1.1. 主要坑有两个，一个两阶段提交确保事务最终一致性（其实就是这学期nosql的理论实践）  
    1.2. 还有就是rocketmq不同于其它mq，消息可能会重复发送，要保证业务更改操作前查询的的幂等性   
2. 主要关注的点：    
    2.2. 配送员自动分配是用红黑树(开源实现treemap)，还是跳表(开源实现redis zset)好，为此磕了4，5天的红黑树和跳表    
    2.3. 最终还是选用了redis的zset，下面讲讲这么做的原因：   
        2.3.1. 从业务角度来分析，程序每取一次最高权重的配送员，之后还要对该配送员的权重-10，   
               简单来说就是同时涉及两个操作，查找和更新，如果单从查找角度出发，二叉树的查询和跳表的查询速度并没有太大差异，   
               但是，涉及到更新就不一定了，跳表虽然浪费了更多空间，但是查询和更新删除都有绝对的优势，不像红黑树，查询不破坏平衡，更新可就不一定了    
        2.3.2. treemap是非线程安全的，如果采用treemap实现，还要额外考虑且解决线程安全的问题，还有就是使用treemap实现在数据持久化方面还要自己实现，   
               显然，这么做的话两周之内可能项目没写出来就死在这些问题上了       
        2.3.4. 最后一点就是可能出现限制配送员的接单数(当配送员权重低于某值时，不再接单)，这时候说白了就是需要高效的范围查询
                而跳表的范围查询也比红黑树快。           
3. 还有一个就是redis要主要的点，lua脚本虽然能保证原子性，但因为redis是单线程io多路复用模型，
    所以再执行lua的时候，会阻塞住当前redis库/集群的所有其它lua读写请求，直到上一个lua脚本执行完成，   
    这也就意味着，lua脚本必须不允许出现死锁。   
    
    还有就是redis的lua脚本特性所衍生出来的长连接/任务超时问题：   
    如果lua脚本因某种原因导致长时间阻塞，那么就有可能导致分配配送员消息消费失败重试/长连接导致连接丢失   
    然后重试的消息不断重试还是失败，最终消息积压爆表，可能导致服务雪崩/长连接过多导致新用户无法再连接   
    对于长连接的解决我自然是用rocketmq解决，毕竟假设在海量数据的情况下，完整的插入事务流程肯定比发送一条事务消息更耗时   
    至于失败消息积压方面解决的方案就有很多，包括sentinel限流、rocketmq限制消费的线程池等
       
    
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
