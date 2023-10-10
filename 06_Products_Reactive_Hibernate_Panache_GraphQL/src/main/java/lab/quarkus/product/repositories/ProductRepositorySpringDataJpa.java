package lab.quarkus.product.repositories;

import lab.quarkus.product.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepositorySpringDataJpa extends JpaRepository<Product, Long> {
}
