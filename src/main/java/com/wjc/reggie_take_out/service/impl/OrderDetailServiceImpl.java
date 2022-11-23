package com.wjc.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjc.reggie_take_out.entity.OrderDetail;
import com.wjc.reggie_take_out.mapper.OrderDetailMapper;
import com.wjc.reggie_take_out.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
