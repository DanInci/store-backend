INSERT INTO category (name) VALUES ('Tops');
INSERT INTO category (name) VALUES ('Bottoms');
INSERT INTO category (name) VALUES ('Full pieces');

INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care) VALUES (3, 'La vie en rose dress', 32.99, 0.0, false, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care']);
INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care) VALUES (3, 'Valentina dress', 13.99, 0.9, true, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care']);
INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care) VALUES (3, 'Cassia dress', 14.99, 20.0, true, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care']);

INSERT INTO content (content_id, p_product_id, name) VALUES ('products/1/DSC_3689.jpg', 1, 'La vie en rose dress 1');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/1/DSC_3707.jpg', 1, 'La vie en rose dress 2');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/1/DSC_3761.jpg', 1, 'La vie en rose dress 3');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/2/DSC_3314.jpg', 2, 'Valentina dress 1');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/2/DSC_3377.jpg', 2, 'Valentina dress 2');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/2/DSC_3380.jpg', 2, 'Valentina dress 3');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/3/DSC_4638.jpg', 3, 'Cassia dress 1');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/3/DSC_4649.jpg', 3, 'Cassia dress 2');
INSERT INTO content (content_id, p_product_id, name) VALUES ('products/3/DSC_4708.jpg', 3, 'Cassia dress 3');

INSERT INTO stock (p_product_id, size, count) VALUES (1, 'M', 23);
INSERT INTO stock (p_product_id, size, count) VALUES (1, 'S', 18);
INSERT INTO stock (p_product_id, size, count) VALUES (1, 'XL', 3);
INSERT INTO stock (p_product_id, size, count) VALUES (2, 'XS', 5);
INSERT INTO stock (p_product_id, size, count) VALUES (2, 'M', 2);
INSERT INTO stock (p_product_id, size, count) VALUES (2, 'S', 8);
INSERT INTO stock (p_product_id, size, count) VALUES (3, 'L', 20);
INSERT INTO stock (p_product_id, size, count) VALUES (3, 'XL', 15);
INSERT INTO stock (p_product_id, size, count) VALUES (3, 'M', 12);