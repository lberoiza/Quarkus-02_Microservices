package lab.quarkus.product.api.graphql;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import lab.quarkus.product.api.interfaces.ProductApi;
import lab.quarkus.product.entities.Product;
import lab.quarkus.product.services.ProductService;
import org.eclipse.microprofile.graphql.*;

import java.util.List;

@GraphQLApi
public class ProductGraphQl implements ProductApi {

  @Inject
  ProductService productService;

  @Query("productList")
  @Description("Get the List of all Products")
  @Override
  public Uni<List<Product>> getProductList() {
    return productService.getProductList();
  }

  @Query("product")
  @Description("Get Information of a Product with the Id")
  @Override
  public Uni<Product> getProduct(@Name("productId") Long id) {
    return productService.getProduct(id);
  }

  @Mutation("updateProduct")
  @Description("Update a Product")
  @Override
  public Uni<Product> updateProduct(@Name("productId") Long id, @Name("product") Product product) {
    return productService.updateProduct(id, product);
  }

  @Mutation
  @Description("Create a new Product")
  @Override
  public Uni<Product> addProduct(@Name("product") Product product) {
    return productService.addProduct(product);
  }

  @Mutation("deleteProduct")
  @Description("Delete a Product from the DataBase")
  @Override
  public Uni<Boolean> deleteProduct(@Name("productId") Long id) {
    return productService.deleteProductById(id);
  }
}
