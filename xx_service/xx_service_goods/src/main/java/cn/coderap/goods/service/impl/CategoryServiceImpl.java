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
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        //问题1：如果某个线程占到了坑，但业务代码（查数据库）出现异常，可能无法释放锁，导致死锁
        //解决办法：必须要在finally释放锁
        //问题2：如果某个线程占到了坑，但是程序在执行过程中宕机，还没有来得及执行释放锁逻辑,就会导致死锁
        //解决办法：设置锁的自动过期（注意：设置过期时间必须和加锁是原子的，即加锁和设置过期时间必须是一条命令，而不能分开写）
        // 尝试加锁
        String uuid = UUID.randomUUID().toString();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(REDISLOCK, uuid,300, TimeUnit.SECONDS);
        if (flag) {
            // 加锁成功
            System.out.println("获取分布式锁成功..." + uuid);
            try {
                // 1、获取锁后，再去缓存中确定一次，如果还没有才查数据库，即双重检验锁
                String cacheJson = stringRedisTemplate.opsForValue().get(SUBCATJSON);
                if (StringUtils.isEmpty(cacheJson)) {
                    Map<String, List<Category2Vo>> subCategory2MapFromDB;
                    // 2、查数据库
                    subCategory2MapFromDB = getSubCategory2MapFromDB();
                    System.out.println("查询了数据库..." + uuid);
                    return subCategory2MapFromDB;
                }
                return JSON.parseObject(cacheJson, new TypeReference<Map<String, List<Category2Vo>>>(){});
            } finally {
                // 问题3：如果业务代码执行时间较长，锁过期了（其他线程抢占到了锁），这时（业务代码执行完后）我们直接删除锁，可能删除原来自己过期的锁，这无影响，但更坏的情况是有可能删掉别的线程正在持有的锁
                // 解决办法：占锁时，值指定为uuid，每个人匹配自己的锁时才删除，这样就不会删除掉别人的锁。
                // 释放锁
//                    String value = stringRedisTemplate.opsForValue().get(REDISLOCK); // 1
//                    if (value.equals(uuid)) { // 2
//                        // 删除我自己的锁
//                        stringRedisTemplate.delete(REDISLOCK); // 3
//                    }
                // 问题4：假设(lock,xxx)锁10s过期，到此程序执行了9.5s，假设下面的1操作分为两步，第一步请求到达redis花费0.3s，第二步redis响应回来花费0.5（在这个过程中，(lock,xxx)锁过期了，
                // 另外一个线程加了(lock，yyy)锁），由于响应回来的值是原来的xxx，所以下面的2判断成立，这时再执行下面的3操作就又删除了别的线程正在持有的锁。
                // 解决办法：与加锁类似，获取值与删除锁不是原子操作，这里可以使用lua脚本（原子操作）来完成
                // 问题5：如果业务执行时间很长，可能业务还没有执行完，锁先过期了
                // TODO 解决办法：简单但并不完美的解决方案是将锁过期时间设置的长一些，比如300s；更好的解决方案是执行业务期间，给锁自动续期，比如redisson的实现
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end"; //该lua脚本来自官网http://redis.cn/commands/set.html
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(REDISLOCK), uuid);
                System.out.println("分布式锁释放成功..." + uuid);
            }
        } else {
            // 加锁失败，休眠100ms后重试
            //感觉还是有问题，竟然让如此多的线程在等待重试，为何不再其重试期间让其去查一下缓存呢
            System.out.println("获取分布式锁失败...等待重试...");
            try {
                Thread.sleep(500);
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
