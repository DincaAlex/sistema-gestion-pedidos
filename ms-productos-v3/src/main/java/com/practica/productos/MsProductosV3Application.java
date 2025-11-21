package com.practica.productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsProductosV3Application {

    public static void main(String[] args) {
        SpringApplication.run(MsProductosV3Application.class, args);
    }
}
