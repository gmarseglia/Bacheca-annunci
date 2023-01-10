use `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `select_annunci_categorie_figlie`;

DELIMITER !

CREATE PROCEDURE `select_annunci_categorie_figlie` (
    IN var_categoria_id VARCHAR(60), IN var_solo_disponibili boolean
)
BEGIN
    DECLARE counter INT DEFAULT 1;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

        CREATE TEMPORARY TABLE `temp_categoria`
        SELECT * FROM `categoria` WHERE `nome`=var_categoria_id OR `padre`=var_categoria_id;

        WHILE counter > 0 DO
            CREATE TEMPORARY TABLE `temp_categoria_2` SELECT * FROM `temp_categoria`;
            SELECT count(*) INTO counter
            FROM `categoria`
            WHERE `nome` NOT IN (SELECT `nome` FROM `temp_categoria`) AND `padre` IN (SELECT `nome` FROM `temp_categoria_2`);

            IF (counter > 0) THEN
                CREATE TEMPORARY TABLE `temp_categoria_3` SELECT * FROM `temp_categoria`;
                INSERT INTO `temp_categoria`
                SELECT *
                FROM `categoria`
                WHERE `nome` NOT IN (SELECT `nome` FROM `temp_categoria_2`) AND `padre` IN (SELECT `nome` FROM `temp_categoria_3`);
                DROP TEMPORARY TABLE `temp_categoria_3`;
            END IF;

            DROP TEMPORARY TABLE `temp_categoria_2`;
        END WHILE;

        SELECT `a`.`numero`, `a`.`inserzionista`, `a`.`descrizione` , `a`.`prezzo`,
            `a`.`categoria`, `a`.`inserito`, `a`.`modificato`, `a`.`venduto`
        FROM `annuncio` as `a`
        WHERE `categoria` IN (SELECT `nome` FROM `temp_categoria`)
            AND ((NOT var_solo_disponibili) OR `venduto` IS NULL);

    COMMIT;
END !

DELIMITER ;