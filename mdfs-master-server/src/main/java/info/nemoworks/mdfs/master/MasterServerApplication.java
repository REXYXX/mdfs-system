package info.nemoworks.mdfs.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.io.File;

/**
 * @author Yu xiang
 */
@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
public class MasterServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MasterServerApplication.class, args);
    }
}
