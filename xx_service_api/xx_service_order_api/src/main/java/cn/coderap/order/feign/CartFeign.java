package cn.coderap.order.feign;

import cn.coderap.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("cart")
@RequestMapping("/cart")
public interface CartFeign {

    @GetMapping("/list")
    public Map list();

    @DeleteMapping
    public Result delete(@RequestParam(name="skuId") String skuId);
}
