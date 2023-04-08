package com.liu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.seckill.entity.User;
import com.liu.seckill.vo.LoginVo;
import com.liu.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户表
 *
 * @author liu
 * @date 2022-12-03
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request,HttpServletResponse response);
    //根据cookie获取用户
    User getUserByCookie(String userTicker,HttpServletRequest request, HttpServletResponse response);
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
