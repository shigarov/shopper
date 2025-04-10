---- Удаление таблиц
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_details;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS items;

-- Таблица товаров
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    img_path VARCHAR(255),
    price DECIMAL(10,2) NOT NULL DEFAULT 0.0 CHECK (price >= 0.0)
);

-- Таблица корзин
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64)
);

-- Таблица деталей корзин
CREATE TABLE cart_details (
    cart_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity >= 1),          -- Количество товара в корзине (по умолчанию 1)
    price DECIMAL(10,2) NOT NULL DEFAULT 0.0 CHECK (price >= 0.0),  -- Цена товара в корзине (по умолчанию 0.0)

    PRIMARY KEY (cart_id, item_id),
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE RESTRICT
);

-- Таблица заказов
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,

    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE RESTRICT
);

-- Таблица деталей заказов
CREATE TABLE order_details (
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity >= 1),          -- Количество товара в заказе (по умолчанию 1)
    price DECIMAL(10,2) NOT NULL DEFAULT 0.0 CHECK (price >= 0.0),  -- Цена товара в заказе (по умолчанию 0.0)

    PRIMARY KEY (order_id, item_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE RESTRICT
);