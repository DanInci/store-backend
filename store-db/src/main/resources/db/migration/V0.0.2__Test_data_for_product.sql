INSERT INTO category (name, sex) VALUES ('Tops', 'F');
INSERT INTO category (name, sex) VALUES ('Bottoms', 'F');
INSERT INTO category (name, sex) VALUES ('Full pieces', 'F');

INSERT INTO product (c_category_id, name, price, discount, is_on_promotion, c_promotion_image, availability_on_command, description, care, added_at) VALUES (3, 'La vie en rose dress', 32.99, 0.0, false, NULL, false, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care'], NOW());
INSERT INTO product (c_category_id, name, price, discount, is_on_promotion, c_promotion_image, availability_on_command, description, care, added_at) VALUES (3, 'Valentina dress', 13.99, 0.9, false, NULL, true, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care'], NOW());
INSERT INTO product (c_category_id, name, price, discount, is_on_promotion, c_promotion_image, availability_on_command, description, care, added_at) VALUES (3, 'Cassia dress', 14.99, 20.0, false, NULL, true, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care'], NOW());

INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/1/DSC_3689.jpg', 'La vie en rose dress 1', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/1/DSC_3707.jpg', 'La vie en rose dress 2', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/1/DSC_3761.jpg', 'La vie en rose dress 3', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/2/DSC_3314.jpg', 'Valentina dress 1', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/2/DSC_3377.jpg', 'Valentina dress 2', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/2/DSC_3380.jpg', 'Valentina dress 3', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/3/DSC_4638.jpg', 'Cassia dress 1', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/3/DSC_4649.jpg', 'Cassia dress 2', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/products/3/DSC_4708.jpg', 'Cassia dress 3', 'jpg');

INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (1, 'clothes.store.app/products/1/DSC_3689.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (1, 'clothes.store.app/products/1/DSC_3707.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (1, 'clothes.store.app/products/1/DSC_3761.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (2, 'clothes.store.app/products/2/DSC_3314.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (2, 'clothes.store.app/products/2/DSC_3377.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (2, 'clothes.store.app/products/2/DSC_3380.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (3, 'clothes.store.app/products/3/DSC_4638.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (3, 'clothes.store.app/products/3/DSC_4649.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (3, 'clothes.store.app/products/3/DSC_4708.jpg');

INSERT INTO stock (p_product_id, product_size, available_count) VALUES (1, 'M', 23);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (1, 'S', 18);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (1, 'XL', 3);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (2, 'XS', 5);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (2, 'M', 2);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (2, 'S', 8);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (3, 'L', 20);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (3, 'XL', 15);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (3, 'M', 12);