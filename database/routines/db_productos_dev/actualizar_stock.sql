-- Actualiza el stock de un producto restando la cantidad
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

ALTER FUNCTION actualizar_stock(bigint, integer) OWNER TO postgres;
