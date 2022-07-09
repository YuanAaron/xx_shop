package cn.coderap.oauth.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户令牌封装
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthToken implements Serializable{

    //令牌信息
    String accessToken;
    //刷新token(refresh_token)
    String refreshToken;
    //jwt短令牌
    String jti;

}