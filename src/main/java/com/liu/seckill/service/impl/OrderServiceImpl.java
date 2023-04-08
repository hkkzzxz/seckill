package com.liu.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.seckill.entity.Order;
import com.liu.seckill.entity.SeckillGoods;
import com.liu.seckill.entity.SeckillOrder;
import com.liu.seckill.entity.User;
import com.liu.seckill.mapper.OrderMapper;
import com.liu.seckill.service.IGoodsService;
import com.liu.seckill.service.IOrderService;
import com.liu.seckill.service.ISeckillGoodsService;
import com.liu.seckill.service.ISeckillOrderService;
import com.liu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 订单表
 *
 * @author liu
 * @date 2022-12-03
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ISeckillGoodsService itSeckillGoodsService;
    @Autowired
    private OrderMapper OrderMapper;
    @Autowired
    private ISeckillOrderService itSeckillOrderService;
    @Autowired
    private IGoodsService itGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Order secKill(User user, GoodsVo goodsVo) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        SeckillGoods seckillGoods = itSeckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
//        itSeckillGoodsService.updateById(seckillGoods);
//      boolean seckillGoodsResult = itSeckillGoodsService.update(new UpdateWrapper<TSeckillGoods>()
//                .set("stock_count", seckillGoods.getStockCount())
//                .eq("id", seckillGoods.getId())
//                .gt("stock_count", 0) /        );
        boolean seckillGoodsResult = itSeckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = " + "stock_count-1")
                .eq("goods_id", goodsVo.getId())
                .gt("stock_count", 0)
        );
//        if (!seckillGoodsResult) {
//            return null;
//        }

        if (seckillGoods.getStockCount() < 1) {
            //判断是否还有库存
            valueOperations.set("isStockEmpty:" + goodsVo.getId(), "0");
            return null;
        }

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        OrderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder tSeckillOrder = new SeckillOrder();
        tSeckillOrder.setUserId(user.getId());
        tSeckillOrder.setOrderId(order.getId());
        tSeckillOrder.setGoodsId(goodsVo.getId());
        itSeckillOrderService.save(tSeckillOrder);
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), tSeckillOrder);
        return order;
    }
    }
