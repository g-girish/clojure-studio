ALTER TABLE gl_orders DROP CONSTRAINT fk_customer_order;
--;;
DROP TABLE IF EXISTS gl_orders;
--;;
DROP TYPE order_statuses;