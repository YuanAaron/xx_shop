package cn.coderap.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 监控xx_business库下的表的数据变动
 */
@CanalEventListener
public class BusinessListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 设置监控点，即监控xx_business库下的tb_ad表
     * @param entryType 事件类型
     * @param rowData 变化前后的数据
     */
    @ListenPoint(schema = "xx_business",table = "tb_ad")
    public void adUpdate(CanalEntry.EntryType entryType, CanalEntry.RowData rowData) {
        System.out.println("tb_ad表中的数据发生了变化");
        //将修改后的position发送到MQ中（tb_ad表不仅存储了网站首页中的广告缓存信息，还存储了其他）
        for(CanalEntry.Column column : rowData.getAfterColumnsList()){
            if(column.getName().equals("position")){
                System.out.println("发送消息到mq ad_update_queue " + column.getValue());
                //将position发送到mq
                //不管客户端有没有消费，只要是监听到变动就发送
                rabbitTemplate.convertAndSend("","ad_update_queue",column.getValue());
                //只有确定消费者已经接收到了消息，才会发送下一条消息
                //rabbitTemplate.convertSendAndReceive()
                break;
            }
        }
    }
}
