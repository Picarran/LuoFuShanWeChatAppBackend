package com.example.luofushan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.luofushan.dao.mapper")
public class LuofushanApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuofushanApplication.class, args);
	}

}
