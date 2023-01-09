use `bacheca_annunci`;

DROP TRIGGER IF EXISTS after_annuncio_update;

DELIMITER !

CREATE TRIGGER after_annuncio_update
BEFORE UPDATE ON `annuncio` FOR EACH ROW
BEGIN
    IF (OLD.`venduto` IS NULL AND NEW.`venduto` IS NOT NULL) THEN
        SET NEW.`modificato` = NEW.`venduto`;
    END IF;
END!

DELIMITER ;