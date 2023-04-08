package com.liu.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liu.seckill.entity.Order;
import com.liu.seckill.entity.SeckillOrder;
import com.liu.seckill.entity.User;
import com.liu.seckill.rabbitmq.MQSender;
import com.liu.seckill.service.IGoodsService;
import com.liu.seckill.service.IOrderService;
import com.liu.seckill.service.ISeckillOrderService;
import com.liu.seckill.utils.JsonUtil;
import com.liu.seckill.vo.GoodsVo;
import com.liu.seckill.vo.RespBean;
import com.liu.seckill.vo.RespBeanEnum;

import com.liu.seckill.vo.SeckillMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀
 *
 * @author: liu
 * @date 2022/12/13 11:34 上午
 * @ClassName: SeKillController
 * window优化前qps 785.9
 * 优化后：327
 * linux优化前 :170
 */

@Controller
@RequestMapping("/seckill")
@Api(value = "秒杀", tags = "秒杀")
public class SeKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService itSeckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;
//
  private Map<Long, Boolean> EmptyStockMap = new HashMap<>();
//    @ApiOperation("秒杀功能")
//    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
//    public Object doSecKill(User user, Long goodsId) {
//        if (user == null) {
//            System.out.println("没有用户");
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//        //优化后代码
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //判断是否重复抢购
//        SeckillOrder tSeckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        if (tSeckillOrder != null) {
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
//        //内存标记，减少Redis的访问
//        if (EmptyStockMap.get(goodsId)) {
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//        //预减库存
//       //Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
//        if (stock < 0) {
//            EmptyStockMap.put(goodsId, true);
//            valueOperations.increment("seckillGoods:" + goodsId);
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
////       Order order=orderService.secKill(user,goods);
//        SeckillMessage seckillMessage=new SeckillMessage(user,goodsId);
//        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
//        return RespBean.success(0);
//        //return "orderDetail";
//    }
    @ApiOperation("获取秒杀结果")
    @GetMapping("getResult")
    @ResponseBody
    public Object getResult(User tUser, Long goodsId) {
        if (tUser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = itSeckillOrderService.getResult(tUser, goodsId);
       return RespBean.success(orderId);
        //return "orderDetail";
    }
    @ApiOperation("秒杀功能-废弃")
    @RequestMapping(value = "/doSeckill")
    public String doSecKill(Model model, User user, Long goodsId) {
        model.addAttribute("user", user);
        GoodsVo goodsVo=goodsService.findGoodsVoByGoodsId(goodsId);
        System.out.println("goodsVo"+goodsId);
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = itSeckillOrderService.getOne(new QueryWrapper<SeckillOrder>().
                eq("user_id", user.getId()).eq("goods_id", goodsId));
        System.out.println(seckillOrder);
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "secKillFail";
//        }
        Order tOrder = orderService.secKill(user, goodsVo);
        model.addAttribute("order", tOrder);
        model.addAttribute("goods", goodsVo);
        SeckillMessage seckillMessage=new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return "orderDetail";
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
