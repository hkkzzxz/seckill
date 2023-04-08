package com.liu.seckill.vo;

import com.liu.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {

    private User tUser;

    private Long goodsId;
}
