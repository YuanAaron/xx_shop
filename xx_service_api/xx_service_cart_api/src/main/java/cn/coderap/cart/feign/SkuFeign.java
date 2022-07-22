package cn.coderap.cart.feign;

import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable String id);

}
