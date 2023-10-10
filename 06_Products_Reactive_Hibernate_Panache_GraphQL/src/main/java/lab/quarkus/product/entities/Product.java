package lab.quarkus.product.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String description;

  public void updateWith(Product product){
    this.setName(product.getName());
    this.setDescription(product.description);
  }

}
