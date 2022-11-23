package com.wjc.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjc.reggie_take_out.entity.Orders;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(@RequestBody Orders orders);

}
