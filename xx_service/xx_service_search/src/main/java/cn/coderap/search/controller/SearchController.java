package cn.coderap.search.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/createIndexAndMapping")
    public Result createIndexAndMapping(){
        searchService.createIndexAndMapping();
        return new Result(true, StatusCode.OK,"创建成功");
    }

    /**
     * 导入符合上架条件的SKU列表
     */
    @GetMapping("/importAll")
    public Result importAllSkuList(){
        searchService.importAll();
        return new Result(true,StatusCode.OK,"导入成功");
    }

}
