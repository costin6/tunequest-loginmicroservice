package tunequest.config;

import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmosConfig {

    private static final String ENDPOINT = "https://tunequestdb.documents.azure.com:443/";
    private static final String KEY = "mg8batufAkz57wiwMqMV7jAfEbbFfMnNXdBfa4o0drydMsMpxuaR5ZdCrlMCLbGBLSpdIUkuYbLcACDbW8Worg==";           // Replace with your Cosmos DB primary key
    private static final String DATABASE_NAME = "tunequestdb";
    private static final String CONTAINER_NAME = "user_data";

    @Bean
    public CosmosClient cosmosClient() {
        return new CosmosClientBuilder()
                .endpoint(ENDPOINT)
                .key(KEY)
                .preferredRegions(java.util.Collections.singletonList("Central US"))
                .consistencyLevel(com.azure.cosmos.ConsistencyLevel.SESSION)
                .buildClient();
    }

    @Bean
    public CosmosDatabase cosmosDatabase(CosmosClient cosmosClient) {
        return cosmosClient.getDatabase(DATABASE_NAME);
    }

    @Bean
    public CosmosContainer cosmosContainer(CosmosDatabase cosmosDatabase) {
        return cosmosDatabase.getContainer(CONTAINER_NAME);
    }
}
