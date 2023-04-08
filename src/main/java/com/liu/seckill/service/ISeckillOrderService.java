package com.liu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.seckill.entity.SeckillOrder;
import com.liu.seckill.entity.User;

/**
 * 秒杀订单表
 *
 * @author liu
 * @date 2022-12-03
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User tUser, Long goodsId);
}
