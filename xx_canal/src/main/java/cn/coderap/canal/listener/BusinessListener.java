package cn.coderap.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;

/**
 * 监控xx_business库下的表的数据变动
 */
@CanalEventListener
public class BusinessListener {

    /**
     * 设置监控点，即监控xx_business库下的tb_ad表
     * @param entryType 事件类型
     * @param rowData 变化前后的数据
     */
    @ListenPoint(schema = "xx_business",table = "tb_ad")
    public void adUpdate(CanalEntry.EntryType entryType, CanalEntry.RowData rowData) {
        System.out.println("tb_ad表中的数据发生了变化");
        rowData.getBeforeColumnsList().forEach(c-> System.out.println("before:" + c.getName() + ":" +c.getValue()));
        System.out.println("===============================");
        rowData.getAfterColumnsList().forEach(c-> System.out.println("after:" + c.getName() + ":" +c.getValue()));
    }
}
