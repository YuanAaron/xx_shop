package cn.coderap.oauth.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户令牌封装
 **/
@Data
public class AuthToken implements Serializable{

    //令牌信息
    String accessToken;
    //刷新token(refresh_token)
    String refreshToken;
    //jwt短令牌
    String jti;

}