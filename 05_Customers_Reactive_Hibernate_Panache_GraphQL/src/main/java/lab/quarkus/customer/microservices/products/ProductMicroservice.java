package lab.quarkus.customer.microservices.products;

import io.smallrye.mutiny.Uni;
import lab.quarkus.customer.entities.Product;

import java.util.List;
import java.util.Map;

public interface ProductMicroservice {
  Uni<Map<Long, Product>> getAllProductsAsMap();

  Uni<List<Product>> getAllProductsAsList();

  Uni<Product> getProductById(Long id);

}
