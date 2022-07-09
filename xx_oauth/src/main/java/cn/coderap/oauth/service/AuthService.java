package cn.coderap.oauth.service;

import cn.coderap.oauth.util.AuthToken;

public interface AuthService {
    /**
     * 用户登录（认证）：采用密码模式认证并获取令牌，采用RestTemplate向Oauth服务发起认证请求
     */
    AuthToken login(String username, String password, String clientId, String clientSecret);
}
