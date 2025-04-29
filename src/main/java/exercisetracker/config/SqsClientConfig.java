package exercisetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import java.net.URI;

@Configuration
public class SqsClientConfig {

    @Bean
    public SqsClient createSqsClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create("test", "test");

        return SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.EU_WEST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
