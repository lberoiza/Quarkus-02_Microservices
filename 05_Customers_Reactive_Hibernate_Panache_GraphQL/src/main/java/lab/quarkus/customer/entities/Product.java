package lab.quarkus.customer.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(
    name = "products",
    uniqueConstraints = @UniqueConstraint(columnNames = {"customer", "product"})
)
public class Product extends PanacheEntity {
  @Transient
  private Long id;
  @ManyToOne
  @JoinColumn(name = "customer", referencedColumnName = "id")
  @JsonBackReference
  private Customer customer;
  @Column
  private Long product;
  @Transient
  private String name;
  @Transient
  private String description;
}
