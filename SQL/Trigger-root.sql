use `bacheca_annunci`;

DROP TRIGGER IF EXISTS `before_annuncio_update`;
DROP TRIGGER IF EXISTS `before_messaggio_privato_insert`;
DROP TRIGGER IF EXISTS `before_messaggio_privato_update`;
DROP TRIGGER IF EXISTS `before_categoria_insert`;
DROP TRIGGER IF EXISTS `before_categoria_update`;

DELIMITER !

CREATE TRIGGER `before_annuncio_update`
BEFORE UPDATE ON `annuncio` FOR EACH ROW
BEGIN
    IF (OLD.`venduto` IS NULL AND NEW.`venduto` IS NOT NULL) THEN
        SET NEW.`modificato` = NEW.`venduto`;
    END IF;
END!

CREATE TRIGGER `before_messaggio_privato_insert`
BEFORE INSERT ON `messaggio_privato` FOR EACH ROW
BEGIN
    IF (NEW.`mittente`=NEW.`destinatario`) THEN
        SIGNAL SQLSTATE "45009" SET message_text="Messaggio non valido";
    END IF;
END!

CREATE TRIGGER `before_messaggio_privato_update`
BEFORE UPDATE ON `messaggio_privato` FOR EACH ROW
BEGIN
    IF (NEW.`mittente`=NEW.`destinatario`) THEN
        SIGNAL SQLSTATE "45009" SET message_text="Messaggio non valido";
    END IF;
END!

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