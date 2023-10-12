package lab.quarkus.customer.microservices.products;

import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.core.Document;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lab.quarkus.customer.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.GraphQLApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.smallrye.graphql.client.core.Argument.arg;
import static io.smallrye.graphql.client.core.Argument.args;
import static io.smallrye.graphql.client.core.Field.field;
import static io.smallrye.graphql.client.core.Operation.operation;

@Slf4j
@GraphQLApi
@Named("ProductMicroserviceGraphQl")
@ApplicationScoped
public class ProductMicroserviceGraphQl implements ProductMicroservice {

  @Inject
  @GraphQLClient("microservice.product.graphql")
  DynamicGraphQLClient dynamicGraphQLClient;


  @Override
  public Uni<Map<Long, Product>> getAllProductsAsMap() {
    return getAllProductsAsList()
        .onItem()
        .transform(this::ProductListToMap);
  }


  @Override
  public Uni<List<Product>> getAllProductsAsList() {
    log.info("Query to Product Microservice: productList");
    Document query = Document.document(
        operation(
            field("productList",
                field("id"),
                field("name"),
                field("description")
            )
        )
    );
    return dynamicGraphQLClient.executeAsync(query)
        .onItem()
        .transform(this::responseToProductList);
  }


  @Override
  public Uni<Product> getProductById(Long id) {
    log.info("Query to Product Microservice: product with id: {}", id);
    Document query = Document.document(
        operation(
            field("product",
                args(arg("productId", id)),
                field("id"),
                field("name"),
                field("description")
            )
        )
    );
    return dynamicGraphQLClient.executeAsync(query)
        .onItem()
        .transform(this::responseToProduct);
  }

  private Product responseToProduct(Response response) {
    Product product = response.getObject(Product.class, "product");
    if(product != null){
      product.setProductId(product.id);
    }
    return product;
  }

  private List<Product> responseToProductList(Response response) {
    List<Product> productList = response.getList(Product.class, "productList");
    productList.forEach(product -> product.setProductId(product.id));
    return productList;
  }

  private Map<Long, Product> ProductListToMap(List<Product> productList) {
    Map<Long, Product> productMap = new HashMap<>();
    productList.forEach(product -> productMap.put(product.getProductId(), product));
    return productMap;
  }

}

