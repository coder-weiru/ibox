package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.rules.ExternalResource;

public class LocalDynamoDBCreationRule extends ExternalResource {
    private DynamoDBProxyServer server;

    public LocalDynamoDBCreationRule() {
        System.setProperty("sqlite4java.library.path", "native-libs");
    }

    @Override
    protected void before() throws Exception {
        String port = "8000";
        server = ServerRunner.createServerFromCommandLineArgs(
                new String[]{"-inMemory", "-port", port});
        server.start();
    }

    @Override
    protected void after() {
        this.stopUnchecked(server);
    }

    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        try {
            dynamoDbServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
