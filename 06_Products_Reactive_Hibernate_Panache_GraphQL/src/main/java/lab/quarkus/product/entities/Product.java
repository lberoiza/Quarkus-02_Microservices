package lab.quarkus.product.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product extends PanacheEntity {

  private String name;
  private String description;

  public void updateWith(Product product){
    this.setName(product.getName());
    this.setDescription(product.description);
  }

}
