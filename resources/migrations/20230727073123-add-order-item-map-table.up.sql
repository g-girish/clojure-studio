CREATE TABLE
    IF NOT EXISTS
        gl_order_items_map(
            id serial PRIMARY KEY,
            order_id INTEGER,
            item_id INTEGER,
            qty NUMERIC NOT NULL,
            CONSTRAINT fk_order_item_order_id_map
                FOREIGN KEY (order_id)
                    REFERENCES gl_orders(id)
                        ON DELETE SET NULL,
            CONSTRAINT fk_order_item_item_id_map
                FOREIGN KEY (item_id)
                    REFERENCES gl_menu(id)
                        ON DELETE SET NULL
        )