package com.practica.productoswriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsProductosWriterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsProductosWriterApplication.class, args);
    }
}
