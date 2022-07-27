package cn.coderap.order.feign;

import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /**
     * 库存变更&销量变更
     */
    @PostMapping("/changeCount")
    public Result changeInventoryAndSaleNumber(@RequestParam(value = "username") String username);

    /**
     * 恢复库存&恢复销量
     * @param skuId
     * @param num
     */
    @PostMapping("/resumeStockNum")
    public Result resumeStockNum(@RequestParam String skuId, @RequestParam Integer num);

    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable String id);
}
