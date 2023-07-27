ALTER TABLE gl_order_item_map DROP CONSTRAINT fk_order_item_order_id_map;
--;;
ALTER TABLE gl_order_item_map DROP CONSTRAINT fk_order_item_item_id_map;
--;;
DROP TABLE IF EXISTS gl_order_item_map;