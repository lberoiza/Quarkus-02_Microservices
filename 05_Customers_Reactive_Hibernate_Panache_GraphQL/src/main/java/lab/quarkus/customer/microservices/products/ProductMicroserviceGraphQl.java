package lab.quarkus.customer.microservices.products;

import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.core.Document;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lab.quarkus.customer.entities.Product;
import org.eclipse.microprofile.graphql.GraphQLApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.smallrye.graphql.client.core.Field.field;
import static io.smallrye.graphql.client.core.Operation.operation;

@ApplicationScoped
@GraphQLApi
@Named("ProductMicroserviceGraphQl")
public class ProductMicroserviceGraphQl implements ProductMicroservice {

  @Inject
  @GraphQLClient("microservice.product.graphql")
  DynamicGraphQLClient dynamicGraphQLClient;


  @Override
  public Uni<Map<Long, Product>> getAllProductsAsMap() {
    return null;
  }

  @Override
  public Uni<List<Product>> getAllProductsAsList() {
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
    return null;
  }

  private List<Product> responseToProductList(Response response) {
    List<Product> productList = response.getList(Product.class, "productList");
    productList.forEach(product -> product.setProductId(product.id));
    return productList;
  }

}

