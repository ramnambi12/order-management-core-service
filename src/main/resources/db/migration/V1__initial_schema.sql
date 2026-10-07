CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           phone VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          price NUMERIC(19, 2) NOT NULL,
                          stock INTEGER NOT NULL
);

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(255) NOT NULL UNIQUE,
                        customer_id BIGINT NOT NULL,
                        status VARCHAR(255) NOT NULL,
                        total_amount NUMERIC(12, 2) NOT NULL,
                        currency VARCHAR(3) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id)
                                REFERENCES customers(id)
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             product_id BIGINT NOT NULL,
                             quantity INTEGER NOT NULL,
                             unit_price NUMERIC(12, 2) NOT NULL,
                             total_price NUMERIC(12, 2) NOT NULL,
                             order_id BIGINT NOT NULL,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE
);

-- Start auto-generated IDs from 100000
ALTER SEQUENCE customers_id_seq RESTART WITH 100000;
ALTER SEQUENCE products_id_seq RESTART WITH 100000;
ALTER SEQUENCE orders_id_seq RESTART WITH 100000;
ALTER SEQUENCE order_items_id_seq RESTART WITH 100000;