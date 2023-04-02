package org.kwater;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan({"org.kwater.persistence.mybatis","org.kwater.pms.repository"})
public class WaterworksApplication {
	private static final Logger logger = LoggerFactory.getLogger (WaterworksApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(WaterworksApplication.class, args);
		logger.info("Water works application started");
	}

}
