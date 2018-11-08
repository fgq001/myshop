package com.njbdqn.myshops.modelprovider;

import com.njbdqn.myshops.modelprovider.services.NormalServiceImpl;
import com.njbdqn.vo.UserDetails;
import com.njbdqn.vo.Userinfos;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ModelProviderApplicationTests {
	@Autowired
	private NormalServiceImpl nsi;
	@Test
	public void contextLoads() {
		Map map = new HashMap();
		map.put("typeid",1);
		map.put("airbrand","格力");
		System.out.println(nsi.getBean(1,map));

//		String s = nsi.findFeatByTypeid(1);
//		Assert.assertEquals("[{\"featureid\":3,\"featurename\":\"空调品牌\",\"featureenname\":\"airbrand\",\"feas\":[\"三菱\",\"格力\",\"美的\"]},{\"featureid\":4,\"featurename\":\"空调功率\",\"featureenname\":\"airpower\",\"feas\":[\"1.5匹\",\"1匹\",\"2.5匹\",\"2匹\",\"3匹\"]},{\"featureid\":5,\"featurename\":\"空调种类\",\"featureenname\":\"airtype\",\"feas\":[\"挂壁\",\"柜机\",\"窗机\"]}]",s);
//		System.out.println(s.equals(s));
	}
	@Test
	public void testShopbyid() {
		System.out.println(nsi.findShopById(1));
	}


	@Test
	public void testUserStore(){
		Userinfos user = new Userinfos();
		user.setUsername("张三");
		user.setAddress("南京");
		user.setBirthday(new Date());
		user.setLoginname("tese123");
		user.setLoginpwd("123");
		user.setRoleid(1);
		user.setSex(1);
		user.setTelephone("122312312");
		UserDetails ud = new UserDetails();
		ud.setEmertcotact("哆啦A梦");
		ud.setEmerttelephone("12233311111");
		nsi.registist(user,ud);
	}
}
