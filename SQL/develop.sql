use `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `controllare_annunci_seguiti`;

DELIMITER !

CREATE PROCEDURE `controllare_annunci_seguiti` (
    IN var_utente_id VARCHAR (30),
    IN var_aggiornare_contr BOOLEAN, IN var_eliminare_venduti BOOLEAN
)
BEGIN
    DECLARE start_timestamp TIMESTAMP;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

        SET start_timestamp = CURRENT_TIMESTAMP;

        SELECT `a`.`numero`, `a`.`inserzionista`, `a`.`descrizione` , `a`.`prezzo`,
            `a`.`categoria`, `a`.`inserito`, `a`.`modificato`, `a`.`venduto`
        FROM `utente` as `u`
            INNER JOIN `segue` as `s` ON `u`.`username`=`s`.`utente`
            INNER JOIN `annuncio` as `a` ON `s`.`annuncio`=`a`.`numero`
        WHERE `u`.`username`=var_utente_id AND `a`.`modificato`>`u`.`contr_seguiti`;

        IF(var_eliminare_venduti=true) THEN
            DELETE `s`
            FROM `segue` AS `s` INNER JOIN `annuncio` AS `a` ON `s`.`annuncio`=`a`.`numero`
            WHERE `s`.`utente`=var_utente_id AND `a`.`venduto` IS NOT NULL;
        END IF;

        IF(var_aggiornare_contr=true) THEN
            UPDATE `utente`
            SET `contr_seguiti`=start_timestamp
            WHERE `username`=var_utente_id;
        END IF;

    COMMIT;
END !

DELIMITER ;