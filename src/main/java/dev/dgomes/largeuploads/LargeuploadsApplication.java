package dev.dgomes.largeuploads;

import dev.dgomes.largeuploads.fileupload.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		StorageProperties.class
})
public class LargeuploadsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LargeuploadsApplication.class, args);
	}

}
