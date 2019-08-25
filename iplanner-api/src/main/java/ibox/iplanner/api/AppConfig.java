package ibox.iplanner.api;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import ibox.iplanner.api.controller.EventController;
import ibox.iplanner.api.service.EventDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@EnableWebMvc
@Configuration
@Import({EventController.class})
public class AppConfig extends SpringBootServletInitializer {

    public AppConfig() {    }

    // silence console logging
    @Value("${logging.level.root:OFF}")
    String message = "";

    /*
     * Create required HandlerMapping, to avoid several default HandlerMapping instances being created
     */
    @Bean
    public HandlerMapping handlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    /*
     * Create required HandlerAdapter, to avoid several default HandlerAdapter instances being created
     */
    @Bean
    public HandlerAdapter handlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }

    /*
     * optimization - avoids creating default exception resolvers; not required as the serverless container handles
     * all exceptions
     *
     * By default, an ExceptionHandlerExceptionResolver is created which creates many dependent object, including
     * an expensive ObjectMapper instance.
     *
     * To enable custom @ControllerAdvice classes remove this bean.
     */
    /*
    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new HandlerExceptionResolver() {

            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                return null;
            }
        };
    }
    */

    @Bean
    public DynamoDB dynamoDB() {
        AWSCredentialsProvider awsCredentialsProvider = InstanceProfileCredentialsProvider.getInstance();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard().withCredentials(awsCredentialsProvider).build();
        return new DynamoDB(client);
    }

    @Bean
    public EventDataService eventDataService() {
        return new EventDataService(dynamoDB());
    }

}
