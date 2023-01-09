use `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `seguire_annuncio`;

DELIMITER !

CREATE PROCEDURE `seguire_annuncio` (
    IN var_utente_id VARCHAR (30), IN var_annuncio_id INT UNSIGNED
)
BEGIN
    DECLARE counter INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

        SELECT COUNT(*)
        INTO counter
        FROM `annuncio`
        WHERE `numero`=var_annuncio_id AND `venduto` IS NOT NULL;

        IF (counter > 0) THEN
            SIGNAL SQLSTATE '45001' SET message_text="Annuncio gia venduto.";
        END IF;

        INSERT INTO `segue` (`utente`, `annuncio`) VALUES (var_utente_id, var_annuncio_id);

    COMMIT;
END !

DELIMITER ;