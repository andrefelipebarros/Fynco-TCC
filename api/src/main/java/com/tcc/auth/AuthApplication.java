package com.tcc.auth;

import java.security.Security;
import org.springframework.boot.SpringApplication;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAsync
@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		SpringApplication.run(AuthApplication.class, args);
		System.out.println("API Started :)");
	}

}
