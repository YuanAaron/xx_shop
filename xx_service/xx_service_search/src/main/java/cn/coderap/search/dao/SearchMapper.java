package cn.coderap.search.dao;

import cn.coderap.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
