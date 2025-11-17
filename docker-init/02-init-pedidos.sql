-- Crear base de datos para pedidos
CREATE DATABASE db_pedidos_dev;

-- Conectarse a la base de datos
\c db_pedidos_dev;

-- Tabla pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id SERIAL PRIMARY KEY,
    cliente VARCHAR(255) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(50) DEFAULT 'PENDIENTE'
);

-- Tabla detalle_pedidos
CREATE TABLE IF NOT EXISTS detalle_pedidos (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

-- Datos de ejemplo
INSERT INTO pedidos (cliente, total, estado) VALUES
('Juan Perez', 469.95, 'PENDIENTE'),
('Maria Lopez', 349.99, 'PROCESADO'),
('Carlos Rodriguez', 129.98, 'PENDIENTE');

INSERT INTO detalle_pedidos (pedido_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1, 2, 99.99),
(1, 2, 3, 89.99),
(2, 6, 1, 349.99),
(3, 4, 1, 79.99),
(3, 7, 1, 49.99);
