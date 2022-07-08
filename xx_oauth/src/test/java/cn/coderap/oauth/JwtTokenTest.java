package cn.coderap.oauth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenTest {

    /**
     * 生成令牌
     */
    @Test
    public void create(){
        //证书路径
        ClassPathResource resource = new ClassPathResource("coderap.jks");
        //创建密钥库工厂对象
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,"cn.coderap".toCharArray());
        //读取密钥对
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("coderap", "cn.coderap".toCharArray());
        //获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义JWT的信息
        Map<String,String> tokenMap = new HashMap<>();
        tokenMap.put("id","9527");
        tokenMap.put("userName","zhangsan");
        //生成令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        //取出令牌
        String jwtEncoded = jwt.getEncoded();
        //eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ijk1MjciLCJ1c2VyTmFtZSI6InpoYW5nc2FuIn0.1lm44fMy1Df4Emh-2YjJ17XDF7f_rbpJVM36JFXhgNCVN1JA-RccSjQQqxEK-fQZZqR8bLz5bEBSOrtLtLnnjXhU-wk-5K32UioCQIMprg4695A3EKHwyH94V_Qe7vIGPcH-AJnlEpaH3I9JLLE-MdyFybDmUNrdsGb8jbxaVqEUPzSS1COTVYCJozq-YhGjRsJUN_8szAqjTFJbb3HsORsyjkOCiNYNMn4e0MZIFxA4j1k6bubGcdDJhlrlvbh884MmeElGwy2aICPSGWlGIKX_7WRB1hvMOsiJAQkiGJnY9rLbYDk8Rxv0s3GfnbXkZkonhjNNsE0RhCs3TFhHgA
        System.out.println(jwtEncoded);
    }

    /**
     * 校验令牌
     */
    @Test
    public void parseJwt(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ijk1MjciLCJ1c2VyTmFtZSI6InpoYW5nc2FuIn0.1lm44fMy1Df4Emh-2YjJ17XDF7f_rbpJVM36JFXhgNCVN1JA-RccSjQQqxEK-fQZZqR8bLz5bEBSOrtLtLnnjXhU-wk-5K32UioCQIMprg4695A3EKHwyH94V_Qe7vIGPcH-AJnlEpaH3I9JLLE-MdyFybDmUNrdsGb8jbxaVqEUPzSS1COTVYCJozq-YhGjRsJUN_8szAqjTFJbb3HsORsyjkOCiNYNMn4e0MZIFxA4j1k6bubGcdDJhlrlvbh884MmeElGwy2aICPSGWlGIKX_7WRB1hvMOsiJAQkiGJnY9rLbYDk8Rxv0s3GfnbXkZkonhjNNsE0RhCs3TFhHgA";
        //指定公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2xnD0mdx0RJViKqdSArf8oZBCNPvdnDXbobfNA1h9H0E/Z1oz+zGt1bhdv3wVHjBVl1j/qW7GP3Hs/+mbo5y76cD0gz/E6v57X3+k23yH3AnONLEC/oM2eOsN+0jCT9tinq7NrJLiGfD30v4xISwMqa/9VpPvNCNwwG3YlFnvTPF5WYGNMnZt8sv0eQd2kjkkqd2lhefpo4qIB1P9EzQxHFVxD0MRHHmBbkAv5epyiKS6M606FJH0pNT5jMtNQRD9U+jCGAhO4T4kQRlpKLnPURnkfvQMwXdvkn9fbq57ze/90Phn44hudktMy0ZNDQ2gyNyTpsKoImvGbu2oq+PqwIDAQAB-----END PUBLIC KEY-----";
        //校验
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        //获取原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
    }

    /**
     * 验证 HTTP BASIC认证是：Authorization: Basic base64(客户端id:客户端密码）
     */
    @Test
    public void decodeString() throws UnsupportedEncodingException {
        String string = "dXNlcndlYjpsYWdvdQ==";
        byte[] decode = Base64.getDecoder().decode(string);
        String decodeString = new String(decode,"UTF-8");
        System.out.println(decodeString);
    }

}
