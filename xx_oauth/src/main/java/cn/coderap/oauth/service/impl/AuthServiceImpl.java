package cn.coderap.oauth.service.impl;

import cn.coderap.oauth.service.AuthService;
import cn.coderap.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //一、向认证服务器http://localhost:9000/oauth/token申请令牌
        //获取认证服务器的服务实例
        ServiceInstance serviceInstance = loadBalancerClient.choose("oauth");
        // 报错：该服务明明已经启动，有时仍然会报这个错？？？
        if (serviceInstance == null) {
            throw new RuntimeException("找不到认证服务器");
        }
        //拼装申请令牌的url
        String path = serviceInstance.getUri().toString()+"/oauth/token";
        //定义请求Body
        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type","password");
        formData.add("username",username);
        formData.add("password",password);
        //定义header
        MultiValueMap<String,String> header = new LinkedMultiValueMap<>();
        header.add("Authorization",httpBasic(clientId,clientSecret));
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
//            @Override
//            public void handleError(ClientHttpResponse response) throws IOException {
//                //当响应的值为400或401时候也要正常响应，不要抛出异常
//                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
//                    super.handleError(response);
//                }
//            }
//        });
        //执行请求
        Map body = null;
        try {
            ResponseEntity<Map> mapResponseEntity =
                    restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData, header), Map.class);
            body = mapResponseEntity.getBody();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        //这些字段可以从 postman进行密码模式的的返回值中 获取
        if(body == null || body.get("access_token") == null || body.get("refresh_token") == null || body.get("jti") == null) {
            //jti是jwt令牌的唯一标识作为用户身份令牌
            throw new RuntimeException("创建令牌失败！");
        }
        //二、将响应数据封装成令牌信息AuthToken返回
        String access_token = (String) body.get("access_token");
        String refresh_token = (String) body.get("refresh_token");
        String jti = (String) body.get("jti");
        AuthToken authToken = new AuthToken(access_token,refresh_token,jti);
        return authToken;
    }

    private String httpBasic(String clientId,String clientSecret){
        String idAndSecret = clientId+":"+clientSecret;
        byte[] encode = Base64Utils.encode(idAndSecret.getBytes());
        return "Basic "+new String(encode);
    }
}
