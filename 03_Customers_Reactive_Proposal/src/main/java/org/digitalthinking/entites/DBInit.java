package org.digitalthinking.entites;

import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.pgclient.PgPool;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class DBInit {

    private final PgPool client;
    private final boolean schemaCreate;

    public DBInit(PgPool client, @ConfigProperty(name = "app.schema.create", defaultValue = "true") boolean schemaCreate) {
        this.client = client;
        this.schemaCreate = schemaCreate;
    }

    void onStart(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }
    }

    private void initdb() {
        client.query("DROP TABLE IF EXISTS product;DROP TABLE IF EXISTS customer").execute()
                .flatMap(r -> client.query("CREATE TABLE customer (id SERIAL PRIMARY KEY, code TEXT, accountNumber TEXT, names TEXT, surname TEXT, phone text, address TEXT)").execute())
                .flatMap(r -> client.query("CREATE TABLE product (id SERIAL PRIMARY KEY,customer int8 not null, product int8 not null)").execute())
                .flatMap(r -> client.query("INSERT INTO customer (code, accountnumber) VALUES ('01','987654321')").execute())
                 .await().indefinitely();
    }
}
