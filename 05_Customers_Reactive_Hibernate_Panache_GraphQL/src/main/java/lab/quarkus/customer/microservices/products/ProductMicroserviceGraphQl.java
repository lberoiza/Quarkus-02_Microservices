package lab.quarkus.customer.microservices.products;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lab.quarkus.customer.entities.Product;
import org.eclipse.microprofile.graphql.GraphQLApi;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@GraphQLApi
@Named("ProductMicroserviceGraphQl")
public class ProductMicroserviceGraphQl implements ProductMicroservice{

  @Override
  public Uni<Map<Long, Product>> getAllProductsAsMap() {
    return null;
  }

  @Override
  public Uni<List<Product>> getAllProductsAsList() {
    return null;
  }

  @Override
  public Uni<Product> getProductById(Long id) {
    return null;
  }
}

