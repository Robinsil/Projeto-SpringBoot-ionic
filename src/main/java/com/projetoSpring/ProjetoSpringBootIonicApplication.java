package com.projetoSpring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.projetoSpring.services.S3Service;

@SpringBootApplication
public class ProjetoSpringBootIonicApplication implements CommandLineRunner {
	
	
	@Autowired
	private S3Service s3Service;
	
	public static void main(String[] args) {
		SpringApplication.run(ProjetoSpringBootIonicApplication.class, args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		

		s3Service.uploadFile("C:\\Temp\\fotos\\foto1.jpg");

	}

}
