create TABLE if not exists accounts
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    number BIGINT NOT NULL UNIQUE,
    amount DECIMAL NOT NULL,
    description VARCHAR(50) NOT NULL
);