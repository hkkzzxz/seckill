package com.liu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.seckill.entity.Goods;
import com.liu.seckill.mapper.GoodsMapper;
import com.liu.seckill.service.IGoodsService;
import com.liu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品表
 *
 * @author liu
 * @date 2022-12-03
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
@Autowired
private GoodsMapper goodsMapper;
    @Override
    public List findGoodVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
