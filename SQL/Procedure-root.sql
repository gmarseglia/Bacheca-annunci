-- Procedure DEVELOP

USE `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `registrazione_utente`;
DROP PROCEDURE IF EXISTS `inserire_annuncio`;
DROP PROCEDURE IF EXISTS `dettagli_annuncio`;
DROP PROCEDURE IF EXISTS `scrivere_commento`;
DROP PROCEDURE IF EXISTS `vendere_annuncio`;
DROP PROCEDURE IF EXISTS `dettagli_utente`;
DROP PROCEDURE IF EXISTS `seguire_annuncio`;
DROP PROCEDURE IF EXISTS `controllare_annunci_seguiti`;
DROP PROCEDURE IF EXISTS `get_all_child_categories`;
DROP PROCEDURE IF EXISTS `select_annunci_categorie_figlie`;
DROP PROCEDURE IF EXISTS `select_annunci_by_inserzionista`;

DELIMITER !

CREATE PROCEDURE `registrazione_utente` (
    in var_username VARCHAR(30), in var_password VARCHAR(30), in var_ruolo ENUM('base', 'gestore'),
    in var_codice_fiscale CHAR(16), in var_nome VARCHAR(100), in var_cognome VARCHAR(100),
    in var_sesso ENUM('donna', 'uomo'),
    in var_data_nascita DATE, in var_comune_nascita VARCHAR(100),
    in var_indirizzo_residenza VARCHAR(100), in var_indirizzo_fatturazione VARCHAR(100),
    in var_valore_recapito_preferito VARCHAR(60),
    in var_tipo_recapito_preferito ENUM('telefono', 'cellulare', 'email') )
BEGIN
    declare exit handler for sqlexception
    begin
    	rollback;
    	resignal;
    end;

	set transaction isolation level read uncommitted; 
	start transaction;

		insert into `utente` (`username`)
			values (var_username);

		insert into `credenziali` (`username`, `password`, `ruolo`)
			values (var_username, var_password, var_ruolo);

		insert into `anagrafica` (`codice_fiscale`, `nome`, `cognome`, `sesso`, `data_nascita`,
			`comune_nascita`, `indirizzo_residenza`, `indirizzo_fatturazione`, `utente`)
			values (var_codice_fiscale, var_nome, var_cognome, var_sesso, var_data_nascita,
			var_comune_nascita, var_indirizzo_residenza, var_indirizzo_fatturazione, var_username);

		insert into `recapito` (`valore`, `anagrafica`, `tipo`)
			values (var_valore_recapito_preferito, var_codice_fiscale, var_tipo_recapito_preferito);

		insert `recapito_preferito` (`anagrafica`, `recapito`)
			values (var_codice_fiscale, var_valore_recapito_preferito);
	commit;
END!

CREATE PROCEDURE `inserire_annuncio` (
	in var_inserzionista VARCHAR(30), in var_descrizione TEXT, in var_prezzo NUMERIC(7, 2),
	in var_categoria VARCHAR(60), out var_numero INT UNSIGNED )
BEGIN
	declare exit handler for sqlexception
    begin
    	rollback;
    	resignal;
    end;

	set transaction isolation level read uncommitted; 
	start transaction;

		insert into `annuncio` (`inserzionista`, `descrizione`, `prezzo`, `categoria`)
			values (var_inserzionista, var_descrizione, var_prezzo, var_categoria);

		update `utente`
			set `annunci_inseriti`=`annunci_inseriti`+1
			where `username`=var_inserzionista;

		set var_numero = last_insert_id();
	commit;
END!

CREATE PROCEDURE `scrivere_commento` (
	in var_utente VARCHAR(30), in var_annuncio INT UNSIGNED, in var_testo VARCHAR(250))
BEGIN
	declare counter INT;
	declare exit handler for sqlexception
    begin
    	rollback;
    	resignal;
    end;

	set transaction isolation level read uncommitted; 
	start transaction;
	
		select count(`numero`) into counter 
			from `annuncio` 
			where `numero`=var_annuncio and `venduto` is not null;

		if(counter=1) then signal sqlstate '45001' set message_text="Annuncio già venduto";
		end if;

		insert into `commento` (`utente`, `annuncio`, `testo`)
			values (var_utente, var_annuncio, var_testo);

		update `annuncio`
			set `modificato`=CURRENT_TIMESTAMP
			where `numero`=var_annuncio;
	commit;
END!

CREATE PROCEDURE `dettagli_annuncio` (
	in var_annuncio_id INT UNSIGNED)
BEGIN
	declare exit handler for sqlexception
	begin
		rollback;
		resignal;
	end;

	set transaction isolation level read committed;
	start transaction;
		select `annuncio`.`numero`, `annuncio`.`inserzionista`, `annuncio`.`descrizione`, `annuncio`.`prezzo`,
		`annuncio`.`categoria`, `annuncio`.`inserito`, `annuncio`.`modificato`, `annuncio`.`venduto`,
		`commento`.`utente`, `commento`.`scritto`, `commento`.`testo`
		from `annuncio` left join `commento` on `annuncio`.`numero`=`commento`.`annuncio`
		where `annuncio`.`numero`=var_annuncio_id;
	commit;
END!

CREATE PROCEDURE `vendere_annuncio` (
	in var_annuncio_id INT UNSIGNED, in var_utente_id VARCHAR(30))
BEGIN
	declare counter INT;

	declare exit handler for sqlexception
	begin
		rollback;
		resignal;
	end;

	start transaction;

	select count(`numero`) into counter 
		from `annuncio` 
		where `numero`=var_annuncio_id and `venduto` is not null;

	if(counter=1) then signal sqlstate '45001' set message_text="Annuncio già venduto";
	end if;

	select count(`numero`) into counter
		from `annuncio`
		where `numero`=var_annuncio_id AND `inserzionista`=var_utente_id;

	IF (counter<>1) THEN
		SIGNAL SQLSTATE "45002" SET message_text="Utente non è inserzionista";
	END IF;

	update `annuncio`
		set `venduto`=CURRENT_TIMESTAMP
		where `numero`=var_annuncio_id;

	update `utente`
		set `annunci_venduti`=`annunci_venduti`+1
		where `username`=var_utente_id;

	commit;
END!

CREATE PROCEDURE `dettagli_utente` (in var_utente_id VARCHAR(30))
BEGIN
	declare exit handler for sqlexception
    begin
    	rollback;
    	resignal;
    end;

	set transaction isolation level read committed;
	start transaction;

		select `u`.`annunci_inseriti`, `u`.`annunci_venduti`,
			`a`.`codice_fiscale`, `a`.`nome` ,`a`.`cognome`, `a`.`sesso`, `a`.`data_nascita`, `a`.`indirizzo_residenza`, `a`.`indirizzo_fatturazione`,
			`r`.`valore`, `r`.`tipo`
		from `utente` as `u`
			inner join `anagrafica` as `a` on `u`.`username` = `a`.`utente`
			inner join `recapito_preferito` as `rp` on `a`.`codice_fiscale` = `rp`.`anagrafica`
			inner join `recapito` as `r` on `rp`.`recapito` = `r`.`valore`
		where `username`=var_utente_id;

		select `r`.`valore`, `r`.`tipo`
		from `recapito` as `r`
			inner join `anagrafica` as a on `r`.`anagrafica` = `a`.`codice_fiscale`
		where `a`.`utente`=var_utente_id and `r`.`valore` not in (
			select `recapito`
			from `recapito_preferito` as `rp`
				inner join `anagrafica` as `a` on `rp`.`anagrafica` = `a`.`codice_fiscale`
			where `a`.`utente`=var_utente_id);

	commit;
END!

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
        WHERE `u`.`username`=var_utente_id AND `a`.`modificato`>=`u`.`contr_seguiti`;

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

CREATE PROCEDURE `get_all_child_categories` (
    IN var_categoria_id VARCHAR(60)
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

        SELECT *
        FROM `temp_categoria`;

    COMMIT;
END !

CREATE PROCEDURE `select_annunci_categorie_figlie` (
    IN var_categoria_id VARCHAR(60), IN var_solo_disponibili boolean
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
    	FROM `categoria`
    	WHERE `nome`=var_categoria_id;

    	IF (counter <> 1) THEN
    		SIGNAL SQLSTATE "45003" SET message_text="categoria non esistente";
		END IF;	

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
        WHERE `inserzionista`=var_inserzionista_id AND ((NOT var_solo_disponibili) OR `venduto` IS NULL);

    COMMIT;
END !

DELIMITER ;

-- GRANT SU PROCEDURE ------------------------------------------------------------------------------------------------------

GRANT EXECUTE ON PROCEDURE `registrazione_utente` TO `registratore`;
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `base`;
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `base`;
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `scrivere_commento` TO `base`;
GRANT EXECUTE ON PROCEDURE `scrivere_commento` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `vendere_annuncio` TO `base`;
GRANT EXECUTE ON PROCEDURE `vendere_annuncio` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `dettagli_utente` TO `base`;
GRANT EXECUTE ON PROCEDURE `dettagli_utente` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `seguire_annuncio` TO `base`;
GRANT EXECUTE ON PROCEDURE `seguire_annuncio` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `controllare_annunci_seguiti` TO `base`;
GRANT EXECUTE ON PROCEDURE `controllare_annunci_seguiti` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `get_all_child_categories` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `select_annunci_categorie_figlie` TO `base`;
GRANT EXECUTE ON PROCEDURE `select_annunci_categorie_figlie` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `select_annunci_by_inserzionista` TO `base`;
GRANT EXECUTE ON PROCEDURE `select_annunci_by_inserzionista` TO `gestore`;
