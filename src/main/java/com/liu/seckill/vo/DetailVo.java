package com.liu.seckill.vo;

import com.liu.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {


    private User tUser;

    private GoodsVo goodsVo;

    private int secKillStatus;

    private int remainSeconds;


}
