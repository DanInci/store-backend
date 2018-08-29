INSERT INTO shipping_method (name) VALUES ('Cargus');
INSERT INTO shipping_method (name) VALUES ('Fan Courier');

INSERT INTO "order" (sm_shipping_method_id, placed_at, billing_firstname, billing_lastname, billing_address, billing_city, billing_country, billing_county, billing_postal_code, billing_phone_number, order_token) VALUES (1, NOW(), 'Daniel', 'Incicau', 'Alea Studentilor', 'Timisoara', 'Timis', 'Romania', '332448', '4073243323', 'token1');

INSERT INTO buyer (o_order_id, email, subscribed, firstname, lastname, address, city, county, country, postal_code, phone_number) VALUES (1, 'daniel_incicau@yahoo.com', true, 'Daniel', 'Incicau', 'Alea Studentilor', 'Timisoara', 'Timis', 'Romania', '332448', '4073243323');

INSERT INTO ordered_product (p_product_id, o_order_id, product_size, ordered_count) VALUES (1, 1, 'XL', 5);
INSERT INTO ordered_product (p_product_id, o_order_id, product_size, ordered_count) VALUES (2, 1, 'S', 2);