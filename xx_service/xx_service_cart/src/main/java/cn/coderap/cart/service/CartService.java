package cn.coderap.cart.service;

import java.util.Map;

public interface CartService {
    void add(String id, Integer num, String userName);

    Map list(String userName);

    void delete(String skuId, String userName);

    void updateCheckedStatus(String skuId, Boolean checked, String userName);
}
