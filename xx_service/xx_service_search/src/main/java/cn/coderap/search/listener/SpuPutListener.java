package cn.coderap.search.listener;

import cn.coderap.search.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "search_add_queue")
@Component
public class SpuPutListener {
    @Autowired
    private SearchService searchService;

    @RabbitHandler
    public void addDataToES(String spuId){
        System.out.println("接收到上架的商品id: " + spuId);
        //通过spuId查询skuList保存到索引库
        searchService.importDataToES(spuId);
    }

}
