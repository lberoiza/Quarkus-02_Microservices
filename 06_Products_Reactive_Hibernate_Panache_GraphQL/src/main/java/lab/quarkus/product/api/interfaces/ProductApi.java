package lab.quarkus.product.api.interfaces;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lab.quarkus.product.entities.Product;

import java.util.List;


public interface ProductApi {

  Uni<List<Product>> getProductList();

  Uni<Product> getProduct(Long id);

  Uni<?> updateProduct(Long id, Product product);

  Uni<?> addProduct(Product product);

  Uni<?> deleteProduct(Long id);


}
