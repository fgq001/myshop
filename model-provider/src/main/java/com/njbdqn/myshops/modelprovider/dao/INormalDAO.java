package com.njbdqn.myshops.modelprovider.dao;

import com.njbdqn.vo.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
@Mapper
public interface INormalDAO {

    public List<Shopinfos> randShop(List params);

    //查寻商品分类
    @Select("select * from shoptypes")
    public List<Shopinfos> findTypes();
    //根据typeid确定哪种商品
    @Select("select * from shoptypes where typeid=#{typeid}")
    public Shopinfos findTypeByTypeid(int typeid);
    //根据typeid值查寻具体品牌...
//    @Select("select * from shopfeature where typeid=#{typeid} and featureenname not like '%id'")
    @Select("select * from shopfeature where typeid=#{typeid} and featureenname not like '%id'")
    public List<Features> findTypeFeatureByTypeid(int typeid);


    @Select("select ${colname} from ${tablename} group by ${colname}")
    public List<String> findTypeFeature(Map info);

    public List<Shopinfos> getBean(@Param("tablename")String tablename,@Param("mymap")Map mymap);

    @Select("select typeid from shops where shopid=#{shopid}")
    public int getTypeidByShopid(int shopid);
    @Select("select * from shopimgs where shopid=#{shopid}")
    public List<Shopimgs> findImgByShopid(int shopid);
    @Select("select * from ${tablename} where shopid=#{shopid}")
    public List<Map> findShopFeature(@Param("tablename")String tablename,@Param("shopid") int shopid);
    public Shopinfos findShopById(int shopid);

    @Select("select * from userbase where loginname=#{loginname} and loginpwd=#{loginpwd}")
    public Userinfos login(Userinfos user);

    @Update("lock tables userbase write,userdetails write")
    public void lockUserTable();
    @Update("unlock tables")
    public void destoryTable();
    @Select("select ifnull(max(${colname})+1,1) from ${tablename}")
    public int nextSequence(@Param("colname")String colname,@Param("tablename") String tablename);
    @Insert("insert into userbase values(#{userid},#{roleid},#{username},#{loginname},#{loginpwd},#{sex},#{telephone})")
    public void saveUser(Userinfos user);
    @Insert("insert into userdetails values(#{udetid},#{userid},#{emertcontact},#{emerttelephone})")
    public void saveUserDetails(UserDetails details);
}
