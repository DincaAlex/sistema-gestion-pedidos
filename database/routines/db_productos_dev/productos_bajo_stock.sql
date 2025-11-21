-- Retorna productos activos con stock por debajo del mínimo
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

ALTER FUNCTION productos_bajo_stock(integer) OWNER TO postgres;
