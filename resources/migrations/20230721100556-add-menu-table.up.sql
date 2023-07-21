CREATE TYPE food_type AS ENUM ('veg', 'non-veg');
--;;
CREATE TABLE
    IF NOT EXISTS
        gl_menu (
            id serial PRIMARY KEY,
            name VARCHAR (150) NOT NULL,
            price DECIMAL (3) NOT NULL,
            type food_type
            );
--;;
CREATE index gl_menu_name on gl_menu(name);