package cn.coderap.search.service.impl;

import cn.coderap.entity.Result;
import cn.coderap.search.feign.SkuFeign;
import cn.coderap.search.dao.SearchMapper;
import cn.coderap.search.pojo.SkuInfo;
import cn.coderap.search.service.SearchService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ElasticsearchTemplate esTemplate;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SearchMapper searchMapper;

    @Override
    public void createIndexAndMapping() {
        //创建索引
        esTemplate.createIndex(SkuInfo.class);
        //创建映射
        esTemplate.putMapping(SkuInfo.class);
    }

    @Override
    public void importAll() {
        //通过Feign远程调用商品微服务中的接口获取sku列表
        Map paramMap = new HashMap();
        paramMap.put("status", "1"); //上架
        Result result = skuFeign.findList(paramMap);
        // SKU列表 -> JSON字符串 -> SkuInfo列表
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(result.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            skuInfo.setSpecMap(JSON.parseObject(skuInfo.getSpec(), Map.class));
        }
        searchMapper.saveAll(skuInfoList);
    }

    @Override
    public void importDataToES(String spuId) {
        Map paramMap = new HashMap();
        paramMap.put("spuId", spuId);
        //通过spuId查询sku列表
        Result result = skuFeign.findList(paramMap);
        //转换
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(result.getData()), SkuInfo.class);
        //设置规格
        for (SkuInfo skuInfo : skuInfoList) {
            skuInfo.setSpecMap(JSON.parseObject(skuInfo.getSpec(), Map.class));
        }
        searchMapper.saveAll(skuInfoList);
    }
}
