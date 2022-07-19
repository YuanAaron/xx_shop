package cn.coderap.page.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端传递过来一个spu的id
 */
//@RestController
//@RequestMapping("/page")
//public class PageController {
//
//    @Autowired
//    private PageService pageService;
//
//    @RequestMapping("/createHtml/{id}")
//    public Result createHtml(@PathVariable(name = "id") String id){
//        pageService.createHtml(id);
//        return new Result(true, StatusCode.OK,"success");
//    }
//
//}
