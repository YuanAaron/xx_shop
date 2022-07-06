package cn.coderap.system.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * 用于签发JWT令牌
 */
public class JwtUtil {

    //有效期一个月
    public static final Long JWT_TTL = 1000L * 60 * 60 * 24 * 30;
    //密钥明文
    private static final String JWT_KEY = "oshacker";

    /**
     * 生成令牌
     *
     * @param id       令牌唯一id
     * @param subject  主题
     * @param ttlMills 有效期
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMills) {
        if (ttlMills == null) {
            ttlMills = JWT_TTL;
        }
        Date expDate = new Date(System.currentTimeMillis() + ttlMills);
        //密钥密文
        SecretKey secretKey = generateKey();
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(id)
                .setSubject(subject)
                .setIssuer("admin")
                .setIssuedAt(new Date())
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS256, secretKey);
        // 生成JWT令牌
        return jwtBuilder.compact();
    }

    /**
     * 生成密钥密文（非必须）
     */
    public static SecretKey generateKey() {
        byte[] bytes = Base64.getEncoder().encode(JWT_KEY.getBytes());
        SecretKey secretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES");
        return secretKey;
    }

}
