package org.digitalthinking.entites;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.*;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class Customer {


  private Long id;
  private String code;
  private String accountNumber;
  private String names;
  private String surname;
  private String phone;
  private String address;
  private List<Product> products;

  public Customer() {

  }

  public Customer(Long id, String code, String accountNumber) {
    this.id = id;
    this.code = code;
    this.accountNumber = accountNumber;
  }

  public static Multi<Customer> findAll(PgPool client) {
    return client.query("SELECT id, accountnumber,code FROM customer ORDER BY id ASC").execute()
        .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
        .onItem().transform(Customer::from);
  }

  public static Uni<Customer> findById(PgPool client, Long id) {
    return client.preparedQuery("SELECT id, code, accountnumber FROM customer WHERE id = $1").execute(Tuple.of(id))
        .onItem().transform(RowSet::iterator)
        .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
  }

  public Uni<Long> save(PgPool client) {
    return client.preparedQuery("INSERT INTO customer (code, accountnumber,names,surname,phone,address) VALUES ($1, $2,$3, $4,$5, $6) RETURNING (id)").execute(Tuple.of(code, accountNumber, names, surname, phone, address))
        .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"))
        .onFailure().invoke(res -> log.error("Error insertando customer ", res));
  }

  public Uni<Boolean> update(PgPool client) {
    return client.preparedQuery("UPDATE customer SET accountnumber = $1 WHERE id = $2").execute(Tuple.of(accountNumber, id))
        .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
  }

  public static Uni<Boolean> delete(PgPool client, Long id) {
    return client.preparedQuery("DELETE FROM customer WHERE id = $1").execute(Tuple.of(id))
        .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
  }

  private static Customer from(Row row) {
    return new Customer(row.getLong("id"), row.getString("code"), row.getString("accountnumber"));
  }


}
