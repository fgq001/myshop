package com.njbdqn.myshops.modelprovider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.njbdqn.myshops.modelprovider.dao")
public class ModelProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModelProviderApplication.class, args);
	}
}
