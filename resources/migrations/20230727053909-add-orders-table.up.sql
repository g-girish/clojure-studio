CREATE TYPE order_statuses AS ENUM ('placed', 'accepted', 'preparing', 'complete');
--;;
CREATE TABLE
    IF NOT EXISTS
        gl_orders(
            id serial PRIMARY KEY,
            customer_id INTEGER,
            subtotal DECIMAL(3) NOT NULL,
            tax DECIMAL(3) NOT NULL,
            total DECIMAL(5) NOT NULL,
            payment_mode VARCHAR(50) NULL,
            status order_statuses,
            CONSTRAINT fk_customer_order
                FOREIGN KEY (customer_id)
                    REFERENCES gl_customers(id)
                        ON DELETE SET NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
--;;
CREATE index gl_order_status on gl_orders(status);
