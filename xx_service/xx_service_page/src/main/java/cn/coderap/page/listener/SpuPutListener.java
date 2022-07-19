package cn.coderap.page.listener;

import cn.coderap.page.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "page_create_queue")
@Component
public class SpuPutListener {
    @Autowired
    private PageService pageService;

    @RabbitHandler
    public void createPage(String spuId){
        System.out.println("接收到上架的商品id: " + spuId);
        pageService.createHtml(spuId);
    }

}
