package lab.quarkus.customer.entities;

import lombok.Data;

@Data
public class Product {
  private Long id;
  private Customer customer;
  private Long product;
  private String name;
  private String description;
}
