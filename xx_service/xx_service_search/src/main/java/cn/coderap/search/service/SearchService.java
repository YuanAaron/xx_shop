package cn.coderap.search.service;

import java.util.Map;

public interface SearchService {

    void createIndexAndMapping();

    void importAll();

    void importDataToES(String spuId);

    void deleteDataFromES(String spuId);

    Map search(Map<String, String> paramMap);
}
