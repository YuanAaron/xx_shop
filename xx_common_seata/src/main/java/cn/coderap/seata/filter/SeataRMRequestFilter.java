package cn.coderap.seata.filter;

import cn.coderap.seata.config.SeataConfig;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class SeataRMRequestFilter extends OncePerRequestFilter {

    /**
     * 给每次线程请求绑定一个XID
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String currentXID = request.getHeader(SeataConfig.SEATA_XID);
        if(!StringUtils.isEmpty(currentXID)){
            RootContext.bind(currentXID);
            log.info("当前线程绑定的XID :" + currentXID);
        }
        try{
            filterChain.doFilter(request, response);
        } finally {
            String unbindXID = RootContext.unbind();
            if(unbindXID != null){
                log.info("当前线程从指定XID中解绑 XID :" + unbindXID);
                if(!currentXID.equals(unbindXID)){
                    log.info("当前线程的XID发生变更");
                }
            }
            if(currentXID != null){
                log.info("当前线程的XID发生变更");
            }
        }
    }
}
