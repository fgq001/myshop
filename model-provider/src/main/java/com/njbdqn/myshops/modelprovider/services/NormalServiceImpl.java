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
    @Override
    public List<Shopinfos> randShop() {
        List nums = new ArrayList();
        Random rand = new Random();
        for (int i=0;i<12;i++){
            nums.add(rand.nextInt(8000));
        }
        return ndao.randShop(nums);
    }

    @Override
    public List<Shopinfos> findTypes() {
        return ndao.findTypes();
    }

    @Override
    public String findFeatByTypeid(int typeid) {
        //查redis 如果里面已有数据则直接返回
        ValueOperations<String,String> vos = redisTemplate.opsForValue();
        if(vos.get("typefeature"+typeid)!=null){
            System.out.println("此次走缓存");
            return vos.get("typefeature"+typeid);
        }
        System.out.println("此次走数据库");
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
        //把数据库找到的数据提出来转为json格式存放在redis中
        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(feas);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        vos.set("typefeature"+typeid,res);
        return res;
    }

    @Override
    public PageInfo<Shopinfos> getBean(int cp, Map map) {
        String tablename = ndao.findTypeByTypeid(Integer.parseInt(map.get("typeid").toString())).getTypeenname();
        Page<Shopinfos> pg = PageHelper.startPage(cp,20);
        ndao.getBean(tablename,map);
        return new PageInfo<Shopinfos>(pg);
    }

    @Override
    public Shopinfos findShopById(int shopid) {
        String tablename = ndao.findTypeByTypeid(ndao.getTypeidByShopid(shopid)).getTypeenname();
        //查询商品的特征信息
        List<Map> feas = ndao.findShopFeature(tablename,shopid );
        Shopinfos sp = ndao.findShopById(shopid);
        sp.setFeatures(feas);
        return sp;
    }

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
        //判断redis服务器中是否有购物车的存在
        String shops = redisTemplate.opsForValue().get(map.get("userid")+"+"+map.get("token"));
        List<Map<Integer,Integer>> sps = new ArrayList<Map<Integer, Integer>>();
        if (shops == null){
            //说明用户没有购物  本次购物为首次购物
//            Shopinfos sp = ndao.findShopById(Integer.parseInt(map.get("shopid").toString()));
//            sp.setBuynum(Integer.parseInt(map.get("buynum").toString()));
            Map<Integer,Integer> sp = new HashMap<Integer, Integer>();
            sp.put(Integer.parseInt(map.get("shopid").toString()),Integer.parseInt(map.get("buynum").toString()));
            sps.add(sp);
        }else{
            //用户已经有购物车
            ObjectMapper om = new ObjectMapper();
            CollectionType listType = om.getTypeFactory().constructCollectionType(ArrayList.class,Map.class);
            try {
                sps = om.readValues(shops,listType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Map<Integer,Integer> mp:sps){
                int spid = Integer.parseInt(map.get("shopid").toString());
                if (mp.containsKey(spid)){
                    mp.put(spid,mp.get(spid)+Integer.parseInt(map.get("buynum").toString()));
                }
            }
        }
    }

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

    @Override
    public void genOrder(Orderinfos order) {

    }
}
