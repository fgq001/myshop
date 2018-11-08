package com.njbdqn.services;

import com.github.pagehelper.PageInfo;
import com.njbdqn.vo.Orderinfos;
import com.njbdqn.vo.Shopinfos;
import com.njbdqn.vo.UserDetails;
import com.njbdqn.vo.Userinfos;

import java.util.List;
import java.util.Map;

public interface INormalService {
    //随机产生12条商品信息
    public List<Shopinfos> randShop();
    //获取所有的分类
    public List<Shopinfos> findTypes();
    //获取某分类所有商品的特征
    public String findFeatByTypeid(int typeid);
//    public List findDatelFeatValByEnname(String nname);
    //获取查询对应分类商品以及查询结果
    public PageInfo<Shopinfos> getBean(int cp,Map map);
    //查询单个商品
    public Shopinfos findShopById(int shopid);
    //用户登录
    public Userinfos login(Userinfos user);
    //注册
    public void registist(Userinfos user, UserDetails details);
    //购物车购买功能
    public void buyShop(Map map);
    //购物车查看
    public List<Shopinfos> findCartShop(Map map);
    //清空购物车
    public void destoryCart(Map map);
    //手工制作token
    public String getToken();
    //下达订单
    public void genOrder(Orderinfos order);




}
