package com.njbdqn.myshops.modelprovider.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.njbdqn.myshops.modelprovider.dao.INormalDAO;
import com.njbdqn.services.INormalService;
import com.njbdqn.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class NormalServiceImpl implements INormalService{
    @Autowired
    private INormalDAO ndao;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 随机获取数据库数据
     * 1. select * from shops orfer by rand() limit 12
     * 2. 批量查询
     * @return
     */
    @Override
    public List<Shopinfos> randShop() {
       List nums = new ArrayList();
       Random rand = new Random();
       for (int i=0;i<12;i++){
           nums.add(rand.nextInt(8000));
       }
       return ndao.randShop(nums);
    }

    /**
     * 查询所有分类
     * @return
     */
    @Override
    public List<Shopinfos> findTypes() {
        return ndao.findTypes();
    }

    /**
     * 按照分类编号查询各个分类的特征信息
     * @param typeid
     * @return
     */
    @Override
    public String findFeatByTypeid(int typeid) {
        //查redis 如果里面已有数据则直接返回
        //1.获得一个简单的redis 健值操作器，进行简单的redis操作
        ValueOperations<String,String> vos = redisTemplate.opsForValue();
        if(vos.get("typefeature"+typeid)!=null){
//            System.out.println("此次走缓存");
            return vos.get("typefeature"+typeid);
        }
//        System.out.println("此次走数据库");
        //查询对应的表名
        String tabname = ndao.findTypeByTypeid(typeid).getTypeenname();
        //查所有的分组列名
        List<Features> feas = ndao.findTypeFeatureByTypeid(typeid);

        //填充Map参数
        Map map = new HashMap();
        map.put("tablename",tabname);

        for (Features fs:feas){
            map.put("colname",fs.getFeatureenname());
            fs.setFeas(ndao.findTypeFeature(map));
        }
        //把数据库找到的数据提出来转为json格式存放到redis中
        String res  = null;
        try {
            res = new ObjectMapper().writeValueAsString(feas);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        vos.set("typefeature"+typeid,res);
        return res;

    }

    /**
     * 商品分页数据
     * @param cp
     * @param map
     * @return
     */
    @Override
    public PageInfo<Shopinfos> getBean(int cp, Map map) {
        String tablename = ndao.findTypeByTypeid(Integer.parseInt(map.get("typeid").toString())).getTypeenname();
        Page<Shopinfos> pg = PageHelper.startPage(cp,20);
        ndao.getBean(tablename,map);
        return new PageInfo<Shopinfos>(pg);
    }

    /**
     * 查询单个商品的信息跨3表  手工填充数据
     * @param shopid
     * @return
     */
    @Override
    public Shopinfos findShopById(int shopid) {
        String tablename = ndao.findTypeByTypeid(ndao.getTypeidByShopid(shopid)).getTypeenname();
        //查询商品的特征信息
        List<Map> feas = ndao.findShopFeature(tablename,shopid );
        Shopinfos sp = ndao.findShopById(shopid);
        sp.setFeatures(feas);
        return sp;
    }

    /**
     * 用户登录  同时获取token信息
     * @param user
     * @return
     */
    @Override
    public Userinfos login(Userinfos user) {
        Userinfos us = ndao.login(user);
        if (us!=null){
            //先去缓存根据用户编号查询是否有购物车
            Set<String> yourkey =redisTemplate.keys(us.getUserid()+"_*");
            if(yourkey.isEmpty()){
                //填充一个token
                us.setToken(getToken());
            }else{
                //在缓存服务器上发现对应的key说明用户曾经购买过商品
                Iterator it = yourkey.iterator();
                //用户id_token编号
                String key = it.next().toString();
                us.setToken(key.split("_")[1]);
            }
        }
        return us;
    }

    /**
     * 用户注册信息 同时注入两张表  主键没有自增 使用意向排他锁  锁表
     * @param user
     * @param details
     */
    @Override
    public void registist(Userinfos user, UserDetails details) {
        //锁表
        ndao.lockUserTable();
        //获取userbase表中的最大id+1
        int userid = ndao.nextSequence("userid","userbase");
        //把编号存放在userbase中
        ndao.saveUser(user);
        //获取userdetails表中的最大id+1
        int udetid = ndao.nextSequence("udetid","userdetails");
        //填充details
        details.setUdetid(udetid);
        details.setUserid(userid);
        //保存details到数据库
        ndao.saveUserDetails(details);
        //释放表
        ndao.destoryTable();
    }

    /**
     * 购物 并把商品存放到购物车 购物车存放在缓存服务器上
     * @param map  用户传入userid\token\shopid\buynum
     */
    @Override
    public void buyShop(Map map) {
        String keyname = map.get("userid")+"_"+map.get("token");
        ObjectMapper om  = new ObjectMapper();
        //判断redis服务器中是否有购物车的存在
        String shops = redisTemplate.opsForValue().get(keyname);
        List<Map> sps = new ArrayList<Map>();
        if (shops == null){
            //说明用户没有购物  本次购物为首次购物
//            Shopinfos sp = ndao.findShopById(Integer.parseInt(map.get("shopid").toString()));
//            sp.setBuynum(Integer.parseInt(map.get("buynum").toString()));
            Map sp = new HashMap();
            sp.put(map.get("shopid").toString(),map.get("buynum").toString());
            sps.add(sp);
        }else{
            //用户已经有购物车
            CollectionType listType = om.getTypeFactory()
                    .constructCollectionType(ArrayList.class,Map.class);
            try {
                sps = om.readValue(shops,listType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //判断sps中是否已有该商品 List<Map<1,10>,Map<2,5>,Map<3,6>> map(2,2)
            boolean isHave = false;
            for (Map mp:sps){
                String spid = map.get("shopid").toString();
                if (mp.containsKey(spid)){
                    mp.put(spid,mp.get(spid).toString()
                            +map.get("buynum").toString());
                    isHave = true;
                    break;
                }
            }
            if (!isHave){
                Map newmp = new HashMap();
                newmp.put(map.get("shopid"),map.get("buynum"));
                sps.add(newmp);
            }
        }
        //把购物车存放到缓存中
        try {
            redisTemplate.opsForValue().set(keyname,om.writeValueAsString(sps));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取购物车中商品信息
     * @param map
     * @return
     */
    @Override
    public List<Shopinfos> findCartShop(Map map) {
        return null;
    }

    @Override
    public void destoryCart(Map map) {

    }

    /**
     * 为每个用户产生唯一的一个编号，用来替代sessionid
     * @return
     */
    @Override
    public synchronized String getToken() {

        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 根据购物车信息生产对应的订单信息
     * @param order
     */
    @Override
    public void genOrder(Orderinfos order) {

    }
}
