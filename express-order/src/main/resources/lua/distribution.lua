--- 学校所属id
local school_id = KEYS[1]
--- 订单id
local order_id = ARGV[1]
--- 有效时间
local expire = ARGV[2]

--- 获取分数最高配送员
local res_courier_id = redis.call("ZREVRANGE", school_id, 0, 0)
--- 不为空再执行
if res_courier_id  then
    --- 扣除该配送员10分
    if redis.call("ZINCRBY", school_id , "-10", res_courier_id[1]) then
        --- 记录日志，记录成功再设置过期时间
        if redis.call("set", order_id, res_courier_id[1]) then
            --- 需要类型转换才能比较
            if tonumber(expire) > 0 then
                redis.call("expire", order_id, expire)
            end
        end
    end
end
return res_courier_id