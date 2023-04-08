package com.liu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.seckill.entity.User;
import com.liu.seckill.exception.GlobalException;
import com.liu.seckill.mapper.UserMapper;
import com.liu.seckill.service.IUserService;
import com.liu.seckill.utils.CookieUtil;
import com.liu.seckill.utils.MD5Util;
import com.liu.seckill.utils.UUIDUtil;
import com.liu.seckill.utils.ValidatorUtil;
import com.liu.seckill.vo.LoginVo;
import com.liu.seckill.vo.RespBean;
import com.liu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户表
 *
 * @author liu
 * @date 2022-12-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
@Autowired
private UserMapper userMapper;
@Autowired
private RedisTemplate redisTemplate;
    @Override
    public RespBean doLogin(LoginVo loginVo,HttpServletRequest request,HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //参数校验
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //TODO 因为我懒测试的时候，把手机号码和密码长度校验去掉了，可以打开。页面和实体类我也注释了，记得打开
       if (!ValidatorUtil.isMobile(mobile)) {
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
//d3b1294a61a07da9b49b6e22b2cbd7f9
        User user = userMapper.selectById(mobile);
        if (user == null) {
            System.out.println("么查到");
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
//        System.out.println(MD5Util.formPassToDBPass(password, user.getSalt()));
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            System.out.println("密码错误"+user.getPassword());
            System.out.println("自己密码"+MD5Util.formPassToDBPass(password, user.getSalt()));
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        String userTicket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:"+userTicket,user);
        //redisTemplate.opsForValue().set("user:" + userTicket, user);
        //request.getSession().setAttribute(userTicket,user);
        CookieUtil.setCookie(request, response, "userTicket", userTicket);
        return RespBean.success(userTicket);
    }


    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        System.out.println("user:"+userTicket);
        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (1 == result) {
            //删除Redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}