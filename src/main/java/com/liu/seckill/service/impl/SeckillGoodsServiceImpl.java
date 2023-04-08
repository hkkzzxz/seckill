package com.liu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.seckill.entity.SeckillGoods;
import com.liu.seckill.mapper.SeckillGoodsMapper;
import com.liu.seckill.service.ISeckillGoodsService;
import org.springframework.stereotype.Service;

/**
 * 秒杀商品表
 *
 * @author liu
 * @date 2022-12-03
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

}
