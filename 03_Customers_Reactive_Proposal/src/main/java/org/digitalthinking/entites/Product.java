package org.digitalthinking.entites;


import lombok.Data;



@Data
public class Product {

    private Long id;
    private Long customer;

    private Long product;

    private String name;

    private String code;

    private String description;
}
