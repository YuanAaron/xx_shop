package cn.coderap.search.service;

import java.util.Map;

public interface SearchService {

    void createIndexAndMapping();

    void importAll();

    void importDataToES(String spuId);
}
