package tunequest.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.CosmosItemResponse;
import org.springframework.stereotype.Repository;
import tunequest.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final CosmosContainer cosmosContainer;

    public UserRepository(CosmosContainer cosmosContainer) {
        this.cosmosContainer = cosmosContainer;
    }

    public User save(User user) {

        CosmosItemResponse<User> response = cosmosContainer.createItem(user);
        return response.getItem();
    }

    public User findById(String id) {
        return cosmosContainer.readItem(id, new PartitionKey(id), User.class).getItem();
    }

    public List<User> findAll() {
        return cosmosContainer.readAllItems(new PartitionKey(""), User.class)
                .stream()
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        cosmosContainer.deleteItem(id, new PartitionKey(id), options);
    }
}