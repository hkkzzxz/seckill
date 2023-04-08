package com.liu.seckill.controller;


import com.liu.seckill.entity.User;
import com.liu.seckill.rabbitmq.MQSender;
import com.liu.seckill.vo.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表
 *
 * @author liu
 * @date 2022-12-03
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户表",tags = "用户表")
public class UserController {
    @Autowired
    private MQSender mqSender;
//        @RequestMapping(value = "/mq", method = RequestMethod.GET)
//    @ResponseBody
//    public void mq() {
//        mqSender.send("Hello");
//    }
//
//    @RequestMapping(value = "/mq/fanout", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqFanout() {
//        mqSender.send("Hello");
//    }
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("返回用户信息")
    public RespBean info(User user) {
        return RespBean.success(user);
    }
//        @RequestMapping(value = "/mq/direct01", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqDirect01() {
//        mqSender.send01("Hello Red");
//    }
//
//    @RequestMapping(value = "/mq/direct02", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqDirect02() {
//        mqSender.send02("Hello Green");
//    }
//        @RequestMapping(value = "/mq/topic01", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqtopic01() {
//        mqSender.send03("Hello Red");
//    }
//
//    @RequestMapping(value = "/mq/topic02", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqtopic02() {
//        mqSender.send04("Hello Green");
//    }
}
