package com.liu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.seckill.entity.Goods;
import com.liu.seckill.vo.GoodsVo;

import java.util.List;

/**
 * 商品表
 *
 * @author liu
 * @date 2022-12-03
 */
public interface IGoodsService extends IService<Goods> {
    List findGoodVo();
    GoodsVo findGoodsVoByGoodsId(Long goodsId);


}
