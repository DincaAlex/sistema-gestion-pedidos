-- Crear base de datos para productos
CREATE DATABASE db_productos_dev;

-- Conectarse a la base de datos
\c db_productos_dev;

-- Tabla productos
CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de ejemplo
INSERT INTO productos (nombre, descripcion, precio, stock, activo) VALUES
('Mouse Logitech MX Master', 'Mouse inalámbrico ergonómico', 99.99, 50, true),
('Teclado Mecánico Keychron K2', 'Switches Brown, RGB', 89.99, 35, true),
('Monitor LG 27" 4K', 'IPS, HDR10, 60Hz', 449.99, 20, true),
('Webcam Logitech C920', 'Full HD 1080p', 79.99, 5, true),
('Laptop HP', 'Intel Core i5, 8GB RAM, 256GB SSD', 599.99, 25, true),
('Auriculares Sony WH-1000XM4', 'Cancelación de ruido activa', 349.99, 15, true),
('Mouse Pad RGB XL', 'Superficie de tela, iluminación RGB', 29.99, 100, true),
('Hub USB-C 7 en 1', 'HDMI, USB 3.0, lector SD', 59.99, 40, true);

-- Procedimientos almacenados
CREATE OR REPLACE FUNCTION actualizar_stock(p_producto_id bigint, p_cantidad integer)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE productos
    SET stock = stock - p_cantidad
    WHERE id = p_producto_id AND activo = true;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Producto no encontrado o inactivo';
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION productos_bajo_stock(p_minimo integer)
RETURNS TABLE(id bigint, nombre character varying, stock integer, precio numeric)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT p.id, p.nombre, p.stock, p.precio
        FROM productos p
        WHERE p.stock < p_minimo AND p.activo = true
        ORDER BY p.stock ASC;
END;
$$;
