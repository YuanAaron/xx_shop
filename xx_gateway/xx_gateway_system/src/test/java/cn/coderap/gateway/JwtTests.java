package cn.coderap.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;

public class JwtTests {

    /**
     * JWT令牌的签发
     */
    @Test
    public void test1() {
        //构建一个jwtBuilder对象
        JwtBuilder jwtBuilder = Jwts.builder().setId("9527").setSubject("xx_shop").setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "123456");
        //创建
        String token = jwtBuilder.compact();
        //eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5NTI3Iiwic3ViIjoieHhfc2hvcCIsImlhdCI6MTY1NzExNjU5MX0.LOR_SXoIKyy889TLTjzHHkMHBk_BmXwJaBiBimX7m80
        //eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5NTI3Iiwic3ViIjoieHhfc2hvcCIsImlhdCI6MTY1NzExNjYzOH0.exbfr0VJX8-SonYr0tFZ_UfIGhcDSFIBejzQ8cnsdf8
        System.out.println(token);
    }

    /**
     * 验证JWT令牌
     */
    @Test
    public void test2() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5NTI3Iiwic3ViIjoieHhfc2hvcCIsImlhdCI6MTY1NzExNzI4NCwiZXhwIjoxNjU3MTE3Mjg0fQ.fWOMr1UV4nZLNxJQjqxngXoOASsyUAn9oabXLjeLWcc";
        Claims claims = Jwts.parser().setSigningKey("123456").parseClaimsJws(token).getBody();
        System.out.println(claims);
    }

    /**
     * 签发带有效期的JWT令牌
     */
    @Test
    public void test3() {
        //指定令牌的有效期1个月
        long time = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30;
        Date expirationDate = new Date(time);
        JwtBuilder jwtBuilder = Jwts.builder().setId("9527").setSubject("xx_shop").setIssuedAt(new Date()).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, "123456");
        String token = jwtBuilder.compact();
        System.out.println(token);
    }

    /**
     * 签发过期的JWT令牌，用于验签测试
     */
    @Test
    public void test4() {
        JwtBuilder jwtBuilder = Jwts.builder().setId("9527").setSubject("xx_shop").setIssuedAt(new Date()).setExpiration(new Date()).signWith(SignatureAlgorithm.HS256, "123456");
        String token = jwtBuilder.compact();
        System.out.println(token);
    }

    //自定义载荷
    @Test
    public void test5() {
        //指定令牌的有效期1个月
        long time = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30;
        Date expirationDate = new Date(time);
        JwtBuilder jwtBuilder = Jwts.builder().setId("9527").setSubject("xx_shop").setIssuedAt(new Date()).setExpiration(expirationDate).claim("role","admin").signWith(SignatureAlgorithm.HS256, "123456");
        String token = jwtBuilder.compact();
        System.out.println(token);
        System.out.println("---------------------------");
        Claims claims = Jwts.parser().setSigningKey("123456").parseClaimsJws(token).getBody();
        System.out.println(claims);
    }

}
