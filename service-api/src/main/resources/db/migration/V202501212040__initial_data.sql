
SET client_encoding TO utf8;

-- Inserir categorias
INSERT INTO project110.category (name) VALUES
('Electronics'),
('Books'),
('Clothing'),
('Toys'),
('Furniture'),
('Beauty'),
('Groceries'),
('Automotive'),
('Sports'),
('Health');

-- Inserir produtos
INSERT INTO project110.product (name, description, price, category_id) VALUES
-- Produtos da categoria Electronics
('Smartphone', 'A modern smartphone with a powerful processor', 699.99, 1),
('Laptop', 'High-performance laptop for work and gaming', 1199.99, 1),
('Wireless Headphones', 'Noise-cancelling wireless headphones', 199.99, 1),
('Smartwatch', 'A smartwatch with health tracking features', 299.99, 1),
('4K TV', 'Ultra HD 4K television with smart features', 799.99, 1),

-- Produtos da categoria Books
('Programming 101', 'Learn the basics of programming with this book', 29.99, 2),
('Advanced Java', 'Deep dive into Java programming', 49.99, 2),
('History of the World', 'A comprehensive guide to world history', 19.99, 2),
('Science for Everyone', 'Science concepts explained simply', 24.99, 2),
('Fiction Novel', 'A thrilling fiction novel', 15.99, 2),

-- Produtos da categoria Clothing
('T-shirt', 'Cotton T-shirt available in multiple colors', 19.99, 3),
('Jeans', 'Comfortable blue jeans', 49.99, 3),
('Jacket', 'Warm winter jacket', 79.99, 3),
('Sneakers', 'Stylish sneakers for everyday use', 69.99, 3),
('Cap', 'Baseball cap in various styles', 14.99, 3),

-- Produtos da categoria Toys
('Action Figure', 'Popular action figure for kids', 24.99, 4),
('Board Game', 'Fun board game for the whole family', 34.99, 4),
('Doll', 'Beautiful doll with accessories', 19.99, 4),
('RC Car', 'Remote-controlled car with fast speed', 59.99, 4),
('Puzzle', '500-piece puzzle for adults and kids', 9.99, 4),

-- Produtos da categoria Furniture
('Sofa', 'Comfortable and stylish sofa', 499.99, 5),
('Dining Table', 'Wooden dining table for six people', 299.99, 5),
('Chair', 'Ergonomic chair for office use', 129.99, 5),
('Bed Frame', 'Queen size bed frame', 349.99, 5),
('Bookshelf', 'Wooden bookshelf with five shelves', 89.99, 5),

-- Produtos da categoria Beauty
('Lipstick', 'Matte lipstick in various shades', 14.99, 6),
('Shampoo', 'Organic shampoo for healthy hair', 9.99, 6),
('Perfume', 'Luxury perfume with a floral scent', 59.99, 6),
('Moisturizer', 'Hydrating face moisturizer', 19.99, 6),
('Eyeliner', 'Waterproof eyeliner', 12.99, 6),

-- Produtos da categoria Groceries
('Apple', 'Fresh red apple', 0.99, 7),
('Bread', 'Whole grain bread', 2.49, 7),
('Milk', '1L of organic milk', 1.99, 7),
('Eggs', 'Carton of 12 eggs', 3.49, 7),
('Rice', '5kg of white rice', 7.99, 7),

-- Produtos da categoria Automotive
('Car Battery', 'Long-lasting car battery', 129.99, 8),
('Tire', 'All-season car tire', 79.99, 8),
('Car Wax', 'Premium car wax for a shiny finish', 19.99, 8),
('Wiper Blades', 'Durable wiper blades', 14.99, 8),
('Engine Oil', '5L synthetic engine oil', 34.99, 8),

-- Produtos da categoria Sports
('Football', 'Standard size football', 19.99, 9),
('Tennis Racket', 'Lightweight tennis racket', 89.99, 9),
('Yoga Mat', 'Non-slip yoga mat', 24.99, 9),
('Dumbbells', 'Set of two 5kg dumbbells', 39.99, 9),
('Basketball Hoop', 'Portable basketball hoop', 99.99, 9),

-- Produtos da categoria Health
('Vitamin C', '500mg Vitamin C tablets', 14.99, 10),
('First Aid Kit', 'Comprehensive first aid kit', 29.99, 10),
('Face Mask', 'Pack of 50 disposable face masks', 19.99, 10),
('Blood Pressure Monitor', 'Digital blood pressure monitor', 49.99, 10),
('Thermometer', 'Digital thermometer for fever measurement', 14.99, 10);
