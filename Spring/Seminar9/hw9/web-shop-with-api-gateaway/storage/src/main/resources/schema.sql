CREATE TABLE if not exists products (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL check (price > 0 ),
    quantity LONG NOT NULL check (quantity >= -1)
);