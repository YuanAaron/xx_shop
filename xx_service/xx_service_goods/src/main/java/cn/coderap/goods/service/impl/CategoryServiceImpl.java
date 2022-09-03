package cn.coderap.goods.service.impl;

import cn.coderap.goods.dao.CategoryMapper;
import cn.coderap.goods.pojo.Category;
import cn.coderap.goods.pojo.vo.Category2Vo;
import cn.coderap.goods.pojo.vo.Category3Vo;
import cn.coderap.goods.service.CategoryService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String CAT1JSON = "cat1Json";
    private static final String SUBCATJSON = "subCatJson";
    private static final String REDISLOCK = "redisLock";

    @Override
    public List<Category> findAllWithTree() {
        //1.查出所有分类
        List<Category> categories = categoryMapper.selectAll();
        /**
         * 一级菜单与二级菜单、二级菜单与三级菜单...，满足cat2.getParentId() == cat1.getId()递推关系，因此可以递归；
         * 零级菜单（虚拟）与一级菜单之间不满足该关系，因此需要单独列出
         */
        //2、组装成父子的树形结构
        //2.1、找到所有的一级分类
        List<Category> cat1List = new ArrayList<>();
        for (Category cat1 : categories) {
            if (cat1.getParentId() == 0) {
                cat1.setSubList(getSubList(cat1,categories));
                cat1List.add(cat1);
            }
        }
        cat1List.sort((c1,c2) -> c1.getSeq() - c2.getSeq());
        return cat1List;
    }

    //2.2、递归查找所有菜单的子菜单
    private List<Category> getSubList(Category cat1, List<Category> categories) {
        List<Category> cat2List = new ArrayList<>();
        for (Category cat2 : categories) {
            if (cat2.getParentId() == cat1.getId()) {
                cat2.setSubList(getSubList(cat2,categories));
                cat2List.add(cat2);
            }
        }
        cat2List.sort((c1,c2) -> c1.getSeq() - c2.getSeq());
        return cat2List;
    }

    @Override
    public Category findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Category category) {
        categoryMapper.insertSelective(category);
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public void delete(Integer id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Category> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return categoryMapper.selectByExample(example);
    }

    @Override
    public Page<Category> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Category>)categoryMapper.selectByExample(example);
    }

    @Override
    public List<Category> getCategory1List() {
        String cacheJson = stringRedisTemplate.opsForValue().get(CAT1JSON);
        if (StringUtils.isEmpty(cacheJson)) {
            List<Category> category1List = getCategory1ListFromDB();
            stringRedisTemplate.opsForValue().set(CAT1JSON, JSON.toJSONString(category1List));
            return category1List;
        }
        return JSON.parseObject(cacheJson, new TypeReference<List<Category>>(){});
    }

    public List<Category> getCategory1ListFromDB() {
        Example example=new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", 0);
        return categoryMapper.selectByExample(example);
    }

    @Override
    public List<Category2Vo> getSubCategory2List(Integer id) {
        return categoryMapper.getSubCategory2List(id);
    }

    @Override
    public Map<String, List<Category2Vo>> getSubCategory2Map() {
        String cacheJson = stringRedisTemplate.opsForValue().get(SUBCATJSON);
        if (StringUtils.isEmpty(cacheJson)) {
            System.out.println("缓存未命中...可能需要查询数据库...");
            Map<String, List<Category2Vo>> subCategory2MapFromDB = getSubCategory2MapWithRedisLock();
            return subCategory2MapFromDB;
        }
        System.out.println("缓存命中...直接返回...");
        return JSON.parseObject(cacheJson, new TypeReference<Map<String, List<Category2Vo>>>(){});
    }

    /**
     * 分布式锁：所有服务（比如商品服务）都去同一个地方"占坑"，如果能占到，就执行逻辑，否则就必须等待，直到释放锁。
     *    1、"占坑"可以去redis，也可以去数据库，即任何这些服务都能访问的地方；
     *    2、等待可以采用自旋的方式
     */
    public Map<String, List<Category2Vo>> getSubCategory2MapWithRedisLock() {
        // 尝试加锁
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(REDISLOCK, "exist");
        if (flag) {
            // 加锁成功
            System.out.println("获取分布式锁成功...");
            // 1、获取锁后，再去缓存中确定一次，如果还没有才查数据库，即双重检验锁
            String cacheJson = stringRedisTemplate.opsForValue().get(SUBCATJSON);
            if (StringUtils.isEmpty(cacheJson)) {
                // 2、查数据库
                Map<String, List<Category2Vo>> subCategory2MapFromDB = getSubCategory2MapFromDB();
                System.out.println("查询了数据库...");
                // 释放锁
                stringRedisTemplate.delete(REDISLOCK);
                System.out.println("查询数据库后，分布式锁释放成功...");
                return subCategory2MapFromDB;
            }
            stringRedisTemplate.delete(REDISLOCK);
            System.out.println("分布式锁释放成功...");
            return JSON.parseObject(cacheJson, new TypeReference<Map<String, List<Category2Vo>>>(){});
        } else {
            // 加锁失败，休眠100ms后重试
            System.out.println("获取分布式锁失败...等待重试");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return getSubCategory2MapWithRedisLock();
        }
    }

    /**
     * 本地锁只能锁住当前进程，分布式环境下，需要分布式锁
     * @return
     */
    public Map<String, List<Category2Vo>> getSubCategory2MapWithLocalLock() {
        // 加锁（categoryServiceImpl在容器中是单例的)
        synchronized (this) {
            // 1、获取锁后，再去缓存中确定一次，如果还没有才查数据库，即双重检验锁
            String cacheJson = stringRedisTemplate.opsForValue().get(SUBCATJSON);
            if (StringUtils.isEmpty(cacheJson)) {
                // 2、查数据库
                Map<String, List<Category2Vo>> subCategory2MapFromDB = getSubCategory2MapFromDB();
                System.out.println("查询了数据库...");
                return subCategory2MapFromDB;
            }
            return JSON.parseObject(cacheJson, new TypeReference<Map<String, List<Category2Vo>>>(){});
        }
    }

    public Map<String, List<Category2Vo>> getSubCategory2MapFromDB() {
        List<Category> categories = categoryMapper.selectAll();

        List<Category> category1List = getByParentId(categories, 0);

        Map<String, List<Category2Vo>> map = new HashMap<>();
        for (Category cat1 : category1List) {
            List<Category2Vo> category2VoList = new ArrayList<>();
            List<Category> category2List = getByParentId(categories, cat1.getId());
            for (Category cat2 : category2List) {
                List<Category> category3List = getByParentId(categories, cat2.getId());
                List<Category3Vo> category3VoList = new ArrayList<>();
                for (Category cat3 : category3List) {
                    category3VoList.add(new Category3Vo(cat3.getId(),cat3.getName(),cat3.getParentId()));
                }
                category2VoList.add(new Category2Vo(cat2.getId(),cat2.getName(),cat2.getParentId(),category3VoList));
            }
            map.put(String.valueOf(cat1.getId()),category2VoList);
        }
        stringRedisTemplate.opsForValue().set(SUBCATJSON, JSON.toJSONString(map));
        return map;
    }

    private List<Category> getByParentId(List<Category> categoryList, int id) {
        List<Category> list = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getParentId() == id) {
                list.add(category);
            }
        }
        return list;
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 分类ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 商品数量
            if(searchMap.get("goodsNum")!=null ){
                criteria.andEqualTo("goodsNum",searchMap.get("goodsNum"));
            }
            // 分类名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 是否显示
            if(searchMap.get("isShow")!=null && !"".equals(searchMap.get("isShow"))){
                criteria.andEqualTo("isShow",searchMap.get("isShow"));
            }
            // 是否导航
            if(searchMap.get("isMenu")!=null && !"".equals(searchMap.get("isMenu"))){
                criteria.andLike("isMenu","%"+searchMap.get("isMenu")+"%");
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }
            // 上级ID
            if(searchMap.get("parentId")!=null ){
                criteria.andEqualTo("parentId",searchMap.get("parentId"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
        }
        return example;
    }
}
