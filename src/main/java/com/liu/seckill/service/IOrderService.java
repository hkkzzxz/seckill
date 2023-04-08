package com.liu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.seckill.entity.Order;
import com.liu.seckill.entity.User;
import com.liu.seckill.vo.GoodsVo;

/**
 * 订单表
 *
 * @author liu
 * @date 2022-12-03
 */
public interface IOrderService extends IService<Order> {

    Order secKill(User user, GoodsVo goodsVo);
}
