INSERT INTO category (name) VALUES ('Tricouri');
INSERT INTO category (name) VALUES ('Pantaloni');
INSERT INTO category (name) VALUES ('Chiloti');

INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care) VALUES (1, 'Tricou roz', 32.99, 0.0, false, ARRAY['A se consuma inainte de data inscrisa pe amabalaj', 'A nu se lasa la soare'], ARRAY['A se spala cu grija', 'Lasa-ti la indemana persoanelor autorizate']);
INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care) VALUES (1, 'Tricou galben', 13.99, 0.9, true, ARRAY['A se pastra la loc de cinste', 'A nu se lasa la soare'], ARRAY['A se spala cu grija', 'Lasa-ti la indemana persoanelor autorizate']);
INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care) VALUES (2, 'Pantalon verde', 14.99, 20.0, true, ARRAY['A se consuma inainte de data inscrisa pe amabalaj', 'A nu se lasa la soare'], ARRAY['A se spala cu grija', 'Lasa-ti la indemana persoanelor autorizate']);
INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care) VALUES (3, 'Chiloti cu batman', 69.99, 15.0, false, ARRAY['A se consuma inainte de data inscrisa pe amabalaj', 'A nu se lasa la soare'], ARRAY['A se spala cu grija', 'Lasa-ti la indemana persoanelor autorizate']);