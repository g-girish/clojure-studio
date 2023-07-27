CREATE TABLE
    IF NOT EXISTS
        gl_customers (
            id serial PRIMARY KEY,
            name VARCHAR (100) NOT NULL,
            PHONE VARCHAR (50) NULL
        );
