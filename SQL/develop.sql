use `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `select_annunci_by_inserzionista`;

DELIMITER !

CREATE PROCEDURE `select_annunci_by_inserzionista` (
    IN var_inserzionista_id VARCHAR(30), IN var_solo_disponibili boolean
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

        SELECT COUNT(*) INTO counter
        FROM `utente`
        WHERE `username`=var_inserzionista_id;

        IF (counter <> 1) THEN
            SIGNAL SQLSTATE "45002" SET message_text="Utente non esistente";
        END IF;

        SELECT `numero`, `inserzionista`, `descrizione`, `prezzo`, `categoria`, `inserito`, `modificato`, `venduto`
        FROM `annuncio`
        WHERE `inserzionista`=var_inserzionista_id AND ((NOT var_solo_disponibili) AND `venduto` IS NOT NULL);

    COMMIT;
END !

DELIMITER ;