package cn.coderap.page.feign;

import cn.coderap.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("goods")
@RequestMapping("/category")
public interface CategoryFeign {
    /**
     * 通过分类ID查询分类对象（其实想要的只是分类的名字）
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id);
}
