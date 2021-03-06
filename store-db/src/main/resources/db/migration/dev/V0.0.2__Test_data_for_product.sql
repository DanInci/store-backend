INSERT INTO category (name, sex) VALUES ('Looks', 'F');
INSERT INTO category (name, sex) VALUES ('Dresses', 'F');
INSERT INTO category (name, sex) VALUES ('Skirts', 'F');
INSERT INTO category (name, sex) VALUES ('Jackets', 'F');
INSERT INTO category (name, sex) VALUES ('Tops', 'F');

INSERT INTO product (c_category_id, name, price, discount, is_available_on_command, is_favourite, description, care, added_at) VALUES (1, 'La vie en rose dress', 32.99, 0.0, false, false, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care'], '2019-01-10 03:00:00');
INSERT INTO product (c_category_id, name, price, discount, is_available_on_command, is_favourite, description, care, added_at) VALUES (2, 'Valentina dress', 13.99, 0.0, true, false, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care'], '2018-12-10 03:00:00');
INSERT INTO product (c_category_id, name, price, discount, is_available_on_command, is_favourite, description, care, added_at) VALUES (3, 'Cassia dress', 14.99, 20.0, true, false, ARRAY['Mesh body suit with dropped arm holes', '84% Nylon / 16% Spandex'], ARRAY['Hand rinse, dry in shade only', 'Avoid rough surfaces, lotions, sunscreens, and overly chlorinated pools', 'Suits should be worn with care'], '2018-11-10 03:00:00');

INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/1/DSC_3689.jpg', 'La vie en rose dress 1', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/1/DSC_3707.jpg', 'La vie en rose dress 2', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/1/DSC_3761.jpg', 'La vie en rose dress 3', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/2/DSC_3314.jpg', 'Valentina dress 1', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/2/DSC_3377.jpg', 'Valentina dress 2', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/2/DSC_3380.jpg', 'Valentina dress 3', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/3/DSC_4638.jpg', 'Cassia dress 1', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/3/DSC_4649.jpg', 'Cassia dress 2', 'jpg', false, false);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/product/3/DSC_4708.jpg', 'Cassia dress 3', 'jpg', false, false);

INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/promotion/1.jpg', 'Cassia dress 2', 'jpg', false, true);
INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES ('ancamorar.com/promotion/2.jpg', 'Cassia dress 3', 'jpg', false, true);

INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (1, 'ancamorar.com/product/1/DSC_3689.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (1, 'ancamorar.com/product/1/DSC_3707.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (1, 'ancamorar.com/product/1/DSC_3761.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (2, 'ancamorar.com/product/2/DSC_3314.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (2, 'ancamorar.com/product/2/DSC_3377.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (2, 'ancamorar.com/product/2/DSC_3380.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (3, 'ancamorar.com/product/3/DSC_4638.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (3, 'ancamorar.com/product/3/DSC_4649.jpg');
INSERT INTO product_content_map (p_product_id, c_content_id) VALUES (3, 'ancamorar.com/product/3/DSC_4708.jpg');

INSERT INTO stock (p_product_id, product_size, available_count) VALUES (1, 'M', 23);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (1, 'S', 18);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (1, 'XL', 3);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (2, 'XS', 5);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (2, 'M', 2);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (2, 'S', 8);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (3, 'L', 20);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (3, 'XL', 15);
INSERT INTO stock (p_product_id, product_size, available_count) VALUES (3, 'M', 12);