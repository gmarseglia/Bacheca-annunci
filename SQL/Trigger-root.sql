use `bacheca_annunci`;

DROP TRIGGER IF EXISTS `after_annuncio_update`;
DROP TRIGGER IF EXISTS `before_messaggio_privato_insert`;

DELIMITER !

CREATE TRIGGER `after_annuncio_update`
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

DELIMITER ;