package cn.coderap.oauth.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.oauth.service.AuthService;
import cn.coderap.oauth.util.AuthToken;
import cn.coderap.oauth.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class AuthController {
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    @Value("${auth.cookieDomain}")
    private String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;
    @Autowired
    private AuthService authService;

    /**
     * 用户登录（认证）：成功,将令牌信息写入Cookie中
     */
    @PostMapping("/login")
    public Result login(String username, String password) {
        // 申请令牌
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);
        if (authToken != null) {
            String token = authToken.getAccessToken();
            // 将令牌存储到Cookie
            writeCookie(token);
            return new Result(true, StatusCode.OK, "登录成功", authToken.getAccessToken());
        } else {
            return new Result(false, StatusCode.ERROR, "登录失败");
        }
    }

    private void writeCookie(String token){
        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        // 生产环境下httpOnly可以设置为true
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }

}
