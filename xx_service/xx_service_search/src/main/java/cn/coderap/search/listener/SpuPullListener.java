package cn.coderap.search.listener;

import cn.coderap.search.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "search_delete_queue")
@Component
public class SpuPullListener {
    @Autowired
    private SearchService searchService;

    @RabbitHandler
    public void deleteDataFromES(String spuId){
        System.out.println("接收到下架的商品id: " + spuId);
        //通过spuId查询skuList并从索引库删除
        searchService.deleteDataFromES(spuId);
    }

}
