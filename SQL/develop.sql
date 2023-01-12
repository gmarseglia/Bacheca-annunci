use `bacheca_annunci`;

DROP TRIGGER IF EXISTS `before_categoria_insert`;
DROP TRIGGER IF EXISTS `before_categoria_update`;

DELIMITER !

CREATE TRIGGER `before_categoria_insert`
BEFORE INSERT ON `categoria` FOR EACH ROW
BEGIN
    IF (NEW.`nome`=NEW.`padre`) THEN
        SIGNAL SQLSTATE "45010" SET message_text="Categoria non valida";
    END IF;
END!

CREATE TRIGGER `before_categoria_update`
BEFORE UPDATE ON `categoria` FOR EACH ROW
BEGIN
    IF (NEW.`nome`=NEW.`padre`) THEN
        SIGNAL SQLSTATE "45010" SET message_text="Categoria non valida";
    END IF;
END!

DELIMITER ;