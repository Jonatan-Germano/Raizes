ALTER TABLE produtos
DROP CONSTRAINT produtos_estoque_check,
ADD CONSTRAINT produtos_estoque_check CHECK (estoque >= 0);