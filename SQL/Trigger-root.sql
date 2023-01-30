use `bacheca_annunci`;

DELIMITER !

DROP TRIGGER IF EXISTS `before_messaggio_privato_insert`;
CREATE TRIGGER `before_messaggio_privato_insert`
BEFORE INSERT ON `messaggio_privato` FOR EACH ROW
BEGIN
    IF (NEW.`mittente`=NEW.`destinatario`) THEN
        SIGNAL SQLSTATE "45009" SET message_text="Messaggio non valido";
    END IF;
END!

DROP TRIGGER IF EXISTS `before_categoria_insert`;
CREATE TRIGGER `before_categoria_insert`
BEFORE INSERT ON `categoria` FOR EACH ROW
BEGIN
    IF (NEW.`nome`=NEW.`padre`) THEN
        SIGNAL SQLSTATE "45010" SET message_text="Categoria non valida";
    END IF;
END!

DELIMITER ;
