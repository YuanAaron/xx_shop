package cn.coderap.oauth.util;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class AdminToken {

    /**
     * 创建管理员令牌
     * @return
     */
    public static String create(){
        //证书文件路径
        String location="coderap.jks";
        //秘钥库密码
        String key_password="cn.coderap";
        //秘钥密码
        String keypwd = "cn.coderap";
        //秘钥别名
        String alias = "coderap";

        //访问证书路径
        ClassPathResource resource = new ClassPathResource(location);
        //创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,key_password.toCharArray());
        //读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());
        //获取私钥
        RSAPrivateKey rsaPrivate = (RSAPrivateKey) keyPair.getPrivate();

        //定义Payload
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "1");
        tokenMap.put("name", "zhangsan");
        tokenMap.put("authorities", new String[]{"oauth","admin"});

        //生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivate));
        //取出令牌
        String token = jwt.getEncoded();
        return token;
    }
}