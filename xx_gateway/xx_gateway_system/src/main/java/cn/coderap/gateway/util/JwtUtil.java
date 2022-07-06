package cn.coderap.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 用于验证JWT令牌
 */
public class JwtUtil {

    //有效期一个月
    public static final Long JWT_TTL = 1000L * 60 * 60 * 24 * 30;
    //密钥明文
    private static final String JWT_KEY = "oshacker";

    /**
     * 解析Token：当token不合法时解析会发生异常
     */
    public static Claims parseJWT(String token) throws Exception{
        SecretKey secretKey = generateKey();
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /**
     * 生成密钥密文（非必须）
     */
    public static SecretKey generateKey() {
        byte[] bytes = Base64.getEncoder().encode(JwtUtil.JWT_KEY.getBytes());
        return new SecretKeySpec(bytes, 0, bytes.length, "AES");
    }

}
