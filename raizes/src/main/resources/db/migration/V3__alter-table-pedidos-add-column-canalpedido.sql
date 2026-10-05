ALTER TABLE pedidos
ADD COLUMN canal_pedido VARCHAR (255) NOT NULL CHECK (canal_pedido IN ('APP', 'TOTEM', 'BALCAO', 'PICKUP', 'WEB'));