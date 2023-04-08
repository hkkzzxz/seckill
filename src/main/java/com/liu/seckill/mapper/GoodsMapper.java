package com.liu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liu.seckill.entity.Goods;
import com.liu.seckill.vo.GoodsVo;

import java.util.List;

/**
 * 商品表 Mapper 接口
 *
 * @author liu
 * @date 2022-12-03
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
