package ibox.iplanner.api.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import ibox.iplanner.api.setup.DynamoDBSetup;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public abstract class LocalDynamoDBIntegrationTestSupport {

    @ClassRule
    public static LocalDynamoDBCreationRule localDynamoDBCreationRule = new LocalDynamoDBCreationRule();

    protected static AmazonDynamoDB amazonDynamoDB;
    protected static DynamoDBSetup dynamoDBSetup;

    private static final String DYNAMODB_ENDPOINT = "amazon.dynamodb.endpoint";
    private static final String AWS_ACCESSKEY = "amazon.aws.accesskey";
    private static final String AWS_SECRETKEY = "amazon.aws.secretkey";

    @BeforeClass
    public static void setupClass() {
        Properties testProperties = loadFromFileInClasspath("test.properties")
                .filter(properties -> !isEmpty(properties.getProperty(AWS_ACCESSKEY)))
                .filter(properties -> !isEmpty(properties.getProperty(AWS_SECRETKEY)))
                .filter(properties -> !isEmpty(properties.getProperty(DYNAMODB_ENDPOINT)))
                .orElseThrow(() -> new RuntimeException("Unable to get all of the required test property values"));

        String amazonAWSAccessKey = testProperties.getProperty(AWS_ACCESSKEY);
        String amazonAWSSecretKey = testProperties.getProperty(AWS_SECRETKEY);
        String amazonDynamoDBEndpoint = testProperties.getProperty(DYNAMODB_ENDPOINT);

        final BasicAWSCredentials awsCredentials = new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
        final String region = new DefaultAwsRegionProviderChain().getRegion();
        final AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, region);

        amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(endpointConfig)
                .build();

        dynamoDBSetup = DynamoDBSetup.of(new DynamoDB(amazonDynamoDB));
    }

    private static Optional<Properties> loadFromFileInClasspath(String fileName) {
        InputStream stream = null;
        try {
            Properties config = new Properties();
            Path configLocation = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
            stream = Files.newInputStream(configLocation);
            config.load(stream);
            return Optional.of(config);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
