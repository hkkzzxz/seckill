package com.liu.seckill.controller;

import com.liu.seckill.entity.User;
import com.liu.seckill.service.IGoodsService;
import com.liu.seckill.service.IUserService;
import com.liu.seckill.vo.DetailVo;
import com.liu.seckill.vo.GoodsVo;
import com.liu.seckill.vo.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;

import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 商品表
 *
 * windows优化前QPS 335.32966259129347
 * linux优化前QPS   170.0044201149229
 * @author liu
 * @date 2022-12-03
 */
@Controller
@RequestMapping("/goods")
@Api(value = "商品表",tags = "商品表")
public class GoodsController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    //初始版本
//@RequestMapping("/toList")
//    public String toList(Model model,User user)
//{
////    if (StringUtils.isEmpty(userTicket))
////    {
////        System.out.println("ticket是空的");
////        return "login";
////    }
////    User user= userService.getUserByCookie(userTicket,request,response);
////    if (null == user){
////        System.out.println("是空的");
////        return "login";
////    }
//    model.addAttribute("user",user);
//    model.addAttribute("goodList",goodsService.findGoodVo());
//    return "goodsList";
//}
@ApiOperation("商品列表")
@RequestMapping(value = "/toList", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
@ResponseBody
public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
    ValueOperations valueOperations = redisTemplate.opsForValue();
    String html = (String) valueOperations.get("goodsList");
    if (!StringUtils.isEmpty(html)) {
        return html;
    }

    model.addAttribute("user", user);
    model.addAttribute("goodList", goodsService.findGoodVo());
//存在redis
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
    if (!StringUtils.isEmpty(html)) {
        valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
    }
    return html;
}
    @ApiOperation("商品详情")
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
    @ResponseBody
    public String toDetail3(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int seckillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;

        if (nowDate.before(startDate)) {
            //秒杀还未开始0
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            //秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            //秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("seckillStatus", seckillStatus);

        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }

        return html;
    }
    //初始版本
//@RequestMapping("/toDetail/{goodsId}")
//@RequestMapping(value = "/toDetail/{goodsId}", method = RequestMethod.GET)
//    public String toDetail(Model model,User user,@PathVariable Long goodsId){
//    model.addAttribute("user",user);
//    GoodsVo goodsVo=goodsService.findGoodsVoByGoodsId(goodsId);
//    Date startDate=goodsVo.getStartDate();
//    Date endDate=goodsVo.getEndDate();
//    Date nowDate=new Date();
//    int secKillStatus=0;88
//    //秒杀倒计时
//    int remainSeconds = 0;
//    if (nowDate.before(startDate)){
//        remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
//        System.out.println(remainSeconds);
//    }else if (nowDate.after(endDate))
//    {
//        secKillStatus=2;
//        remainSeconds = -1;
//    }else {
//        secKillStatus=1;
//        remainSeconds = 0;
//    }
//    System.out.println(remainSeconds);
//    model.addAttribute("remainSeconds", remainSeconds);
//    model.addAttribute("secKillStatus",secKillStatus);
//    model.addAttribute("goods",goodsVo);
//    //System.out.println(goodsService.findGoodsVoByGoodsId(goodsId).getGoodsName());
//    return "goodsDetail";
//    }

}
