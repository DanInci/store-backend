INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/promotions/1.jpg', 'Promotion 1', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/promotions/2.jpg', 'Promotion 2', 'jpg');
INSERT INTO content (content_id, name, format) VALUES ('clothes.store.app/promotions/3.jpg', 'Promotion 3', 'jpg');

INSERT INTO promotion (title, description, p_product_id, c_content_id, expires_at) VALUES ('Primavara 2018', 'Colectia de primavara 2018', NULL, 'clothes.store.app/promotions/1.jpg', '2019-09-12');
INSERT INTO promotion (title, description, p_product_id, c_content_id, expires_at) VALUES ('Vara 2018', 'Colectia de vara 2018', 2, 'clothes.store.app/promotions/1.jpg', '2019-09-12');
INSERT INTO promotion (title, description, p_product_id, c_content_id, expires_at) VALUES ('Toamna 2018', 'Colectia de toamna 2018', 1, 'clothes.store.app/promotions/1.jpg', '2019-09-12');