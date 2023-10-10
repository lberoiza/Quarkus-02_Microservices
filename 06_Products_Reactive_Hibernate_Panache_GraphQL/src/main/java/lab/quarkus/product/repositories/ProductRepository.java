package lab.quarkus.product.repositories;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lab.quarkus.product.entities.Product;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, Long> {

  public Uni<Product> addProduct(Product product) {
    return Panache.withTransaction(() -> this.persist(product));
  }

  public Uni<Product> updateProduct(Long id, Product productUpdateData) {
    return Panache.withTransaction(() -> this.findById(id)
        .onItem().ifNotNull()
        .invoke(foundedProduct -> foundedProduct.updateWith(productUpdateData))
    );
  }

  public Uni<Boolean> deleteProductById(Long id) {
    return Panache.withTransaction(() -> this.deleteById(id));
  }

}
