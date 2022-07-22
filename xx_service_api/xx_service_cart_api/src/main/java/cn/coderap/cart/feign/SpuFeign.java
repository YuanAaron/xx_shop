package cn.coderap.cart.feign;

import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("goods")
@RequestMapping("/spu")
public interface SpuFeign {

    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable String id);

}
