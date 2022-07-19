package cn.coderap.page.feign;

import cn.coderap.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("goods")
@RequestMapping("/spu")
public interface SpuFeign {
    /**
     * 通过商品id（spuId）查询商品对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id);

}
