package com.threeape.frame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EntityScan
public class FrameApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrameApplication.class, args);
	}

}
