USE `bacheca_annunci`;

DELIMITER !

-- U0100
DROP PROCEDURE IF EXISTS `login`!
CREATE PROCEDURE `login` (IN var_username VARCHAR(30), IN var_password VARCHAR(30))
BEGIN
    SELECT `ruolo`
    FROM `credenziali`
	WHERE `username`=var_username AND `password`=SHA1(var_password);
END!
GRANT EXECUTE ON PROCEDURE `login` TO `registratore`!

-- U0000
DROP PROCEDURE IF EXISTS `registrazione_utente`!
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
			values (var_username, SHA1(var_password), var_ruolo);

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
GRANT EXECUTE ON PROCEDURE `registrazione_utente` TO `registratore`!

-- U0001
DROP PROCEDURE IF EXISTS `inserire_recapito`!
CREATE PROCEDURE `inserire_recapito` (IN var_valore VARCHAR(60), IN var_anagrafica CHAR(16), IN var_tipo ENUM('telefono', 'cellulare', 'email'))
BEGIN
    INSERT IGNORE INTO `recapito` (`valore`, `anagrafica`, `tipo`)
    VALUES (var_valore, var_anagrafica, var_tipo);
END!
GRANT EXECUTE ON PROCEDURE `inserire_recapito` TO `registratore`!

-- A0000
DROP PROCEDURE IF EXISTS `inserire_annuncio`!
CREATE PROCEDURE `inserire_annuncio` (in var_inserzionista VARCHAR(30), in var_descrizione TEXT, in var_categoria VARCHAR(60), out var_numero INT UNSIGNED)
BEGIN
    declare exit handler for sqlexception
    begin
        rollback;
        resignal;
    end;

    set transaction isolation level read uncommitted;
    start transaction;

        INSERT INTO `annuncio` (`descrizione`, `categoria`, `inserzionista`) VALUES
            (var_descrizione, var_categoria, var_inserzionista);

        SET var_numero = LAST_INSERT_ID();

        INSERT INTO `annuncio_disponibile` (`annuncio`) VALUES
            (var_numero);

        update `utente`
            set `annunci_inseriti`=`annunci_inseriti`+1
            where `username`=var_inserzionista;

    commit;
END!
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `base`!
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `gestore`!

-- A0100
DROP PROCEDURE IF EXISTS `dettagli_annuncio`!
CREATE PROCEDURE `dettagli_annuncio` (in var_annuncio_id INT UNSIGNED)
BEGIN
	DECLARE counter INT;
	declare exit handler for sqlexception
	begin
		rollback;
		resignal;
	end;

	set transaction isolation level read committed;
	start transaction;

		SELECT COUNT(*) INTO counter
		FROM `annuncio`
		WHERE `numero`=var_annuncio_id;

		IF (counter <> 1) THEN
			SIGNAL SQLSTATE "45004" SET message_text="Annuncio non esistente";
		END IF;

		select `annuncio`.`numero`, `annuncio`.`inserzionista`, `annuncio`.`descrizione`,
		`annuncio`.`categoria`, `annuncio`.`inserito`, `annuncio`.`modificato`, `annuncio`.`venduto`,
		`commento`.`utente`, `commento`.`scritto`, `commento`.`testo`
		from `annuncio` left join `commento` on `annuncio`.`numero`=`commento`.`annuncio`
		where `annuncio`.`numero`=var_annuncio_id
		ORDER BY `commento`.`scritto` ASC;

	commit;
END!
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `base`!
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `gestore`!

-- A0200
DROP PROCEDURE IF EXISTS `select_annunci_categorie_figlie`!
CREATE PROCEDURE `select_annunci_categorie_figlie` (IN var_categoria_id VARCHAR(60), IN var_solo_disponibili boolean)
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
    		SIGNAL SQLSTATE "45003" SET message_text="ategoria non esistente";
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

        SELECT `a`.`numero`, `a`.`inserzionista`, `a`.`descrizione` , `a`.`categoria`, `a`.`inserito`, `a`.`modificato`, `a`.`venduto`
        FROM `annuncio` as `a`
        WHERE `categoria` IN (SELECT `nome` FROM `temp_categoria`) AND ((NOT var_solo_disponibili) OR `venduto` IS NULL);

        DROP TEMPORARY TABLE `temp_categoria`;
    COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_categorie_figlie` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_categorie_figlie` TO `gestore`!

-- A0202
DROP PROCEDURE IF EXISTS `select_annunci_by_inserzionista`!
CREATE PROCEDURE `select_annunci_by_inserzionista` (IN var_inserzionista_id VARCHAR(30), IN var_solo_disponibili boolean)
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

        SELECT `numero`, `inserzionista`, `descrizione`, `categoria`, `inserito`, `modificato`, `venduto`
        FROM `annuncio`
        WHERE `inserzionista`=var_inserzionista_id AND ((NOT var_solo_disponibili) OR `venduto` IS NULL);

    COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_inserzionista` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_inserzionista` TO `gestore`!

-- A0203
DROP PROCEDURE IF EXISTS `select_annunci_by_descrizione`!
CREATE PROCEDURE `select_annunci_by_descrizione` (IN var_descrizione TEXT, IN var_only_available BOOLEAN)
BEGIN
        SELECT `numero`, `inserzionista`, `descrizione` , `categoria`, `inserito`, `modificato`, `venduto`
        FROM `annuncio` 
        WHERE MATCH(`descrizione`) AGAINST (var_descrizione IN NATURAL LANGUAGE MODE)
        AND ((NOT var_only_available) OR `venduto` IS NULL);
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_descrizione` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_descrizione` TO `gestore`!

-- A0204
DROP PROCEDURE IF EXISTS `select_annunci_without_clauses`!
CREATE PROCEDURE `select_annunci_without_clauses` (IN var_only_available BOOLEAN)
BEGIN
    SELECT `numero`, `inserzionista`, `descrizione`, `categoria`, `inserito`, `modificato`, `venduto` FROM `annuncio` WHERE (`venduto` IS NULL OR NOT var_only_available);
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_without_clauses` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_without_clauses` TO `gestore`!

-- A0300
DROP PROCEDURE IF EXISTS `seguire_annuncio`!
CREATE PROCEDURE `seguire_annuncio` (IN var_utente_id VARCHAR (30), IN var_annuncio_id INT UNSIGNED)
BEGIN
    DECLARE counter_totale INT;
    DECLARE counter_venduto INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;

        SELECT COUNT(*), COUNT(`venduto`)
        INTO counter_totale, counter_venduto
        FROM `annuncio`
        WHERE `numero`=var_annuncio_id;

        IF (counter_totale <> 1) THEN
        	SIGNAL SQLSTATE '45004' SET message_text="Annuncio non esistente.";
        END IF;

        IF (counter_venduto = 1) THEN
        	SIGNAL SQLSTATE '45001' SET message_text="Annuncio gia venduto.";
        END IF;

        INSERT INTO `segue` (`utente`, `annuncio`) VALUES (var_utente_id, var_annuncio_id);

    COMMIT;
END !
GRANT EXECUTE ON PROCEDURE `seguire_annuncio` TO `base`!
GRANT EXECUTE ON PROCEDURE `seguire_annuncio` TO `gestore`!

-- A0301
DROP PROCEDURE IF EXISTS `delete_segue`!
CREATE PROCEDURE `delete_segue` (IN var_utente VARCHAR(30), IN var_annuncio INT UNSIGNED)
BEGIN
    DELETE FROM `segue` WHERE `utente`=var_utente AND `annuncio`=var_annuncio;
END!
GRANT EXECUTE ON PROCEDURE `delete_segue` TO `base`!
GRANT EXECUTE ON PROCEDURE `delete_segue` TO `gestore`!

-- A0400
DROP PROCEDURE IF EXISTS `controllare_annunci_seguiti`!
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

        SELECT `a`.`numero`, `a`.`inserzionista`, `a`.`descrizione` ,
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
END!
GRANT EXECUTE ON PROCEDURE `controllare_annunci_seguiti` TO `base`!
GRANT EXECUTE ON PROCEDURE `controllare_annunci_seguiti` TO `gestore`!

-- A0500
DROP PROCEDURE IF EXISTS `vendere_annuncio`!
CREATE PROCEDURE `vendere_annuncio` (in var_annuncio_id INT UNSIGNED, in var_utente_id VARCHAR(30))
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
GRANT EXECUTE ON PROCEDURE `vendere_annuncio` TO `base`!
GRANT EXECUTE ON PROCEDURE `vendere_annuncio` TO `gestore`!

-- A0600
DROP PROCEDURE IF EXISTS `dettagli_utente`!
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
			`a`.`codice_fiscale`, `a`.`nome` ,`a`.`cognome`, `a`.`sesso`, `a`.`data_nascita`, `a`.`comune_nascita`, `a`.`indirizzo_residenza`, `a`.`indirizzo_fatturazione`,
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
GRANT EXECUTE ON PROCEDURE `dettagli_utente` TO `base`!
GRANT EXECUTE ON PROCEDURE `dettagli_utente` TO `gestore`!

-- M0000
DROP PROCEDURE IF EXISTS `insert_messaggio`!
CREATE PROCEDURE `insert_messaggio` (IN var_mittente VARCHAR(30), IN var_destinatario VARCHAR(30), IN var_testo VARCHAR(250))
BEGIN
    INSERT INTO `messaggio_privato` (`mittente`, `destinatario`, `testo`)
    VALUES (var_mittente, var_destinatario, var_testo);
END!
GRANT EXECUTE ON PROCEDURE `insert_messaggio` TO `base`!
GRANT EXECUTE ON PROCEDURE `insert_messaggio` TO `gestore`!

-- M0100
DROP PROCEDURE IF EXISTS `select_messaggi_con_utente`!
CREATE PROCEDURE `select_messaggi_con_utente` (IN var_utente_1 VARCHAR(30), IN var_utente_2 VARCHAR(30))
BEGIN
    SELECT `mittente`, `destinatario`, `inviato`, `testo`
    FROM `messaggio_privato`
    WHERE (`mittente`=var_utente_1 AND `destinatario`=var_utente_2) OR (`mittente`=var_utente_2 AND `destinatario`=var_utente_1)
    ORDER BY `inviato` ASC;
END!
GRANT EXECUTE ON PROCEDURE `select_messaggi_con_utente` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_messaggi_con_utente` TO `gestore`!

-- M0101
DROP PROCEDURE IF EXISTS `select_utenti_con_messaggi`!
CREATE PROCEDURE `select_utenti_con_messaggi` (IN var_target_utente VARCHAR(30))
BEGIN

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

    START TRANSACTION;

    SELECT `destinatario`
    FROM `messaggio_privato`
    WHERE `mittente`=var_target_utente
    UNION
    SELECT `mittente`
    FROM `messaggio_privato`
    WHERE `destinatario`=var_target_utente;

    COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `select_utenti_con_messaggi` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_utenti_con_messaggi` TO `gestore`!

-- C0000
DROP PROCEDURE IF EXISTS `scrivere_commento`!
CREATE PROCEDURE `scrivere_commento` (in var_utente VARCHAR(30), in var_annuncio INT UNSIGNED, in var_testo VARCHAR(250))
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
GRANT EXECUTE ON PROCEDURE `scrivere_commento` TO `base`!
GRANT EXECUTE ON PROCEDURE `scrivere_commento` TO `gestore`!

-- T0000
DROP PROCEDURE IF EXISTS `select_categoria`!
CREATE PROCEDURE `select_categoria` ()
BEGIN
    SELECT `nome`,`padre` FROM `categoria`;
END!
GRANT EXECUTE ON PROCEDURE `select_categoria` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_categoria` TO `gestore`!

-- G0000
DROP PROCEDURE IF EXISTS `insert_categoria`!
CREATE PROCEDURE `insert_categoria` (IN var_nome VARCHAR(60), IN var_padre VARCHAR(60))
BEGIN
    INSERT INTO `categoria` VALUES (var_nome, var_padre);
END!
GRANT EXECUTE ON PROCEDURE `insert_categoria` TO `gestore`!

-- R0001
DROP PROCEDURE IF EXISTS `generate_report`!
CREATE PROCEDURE `generate_report` ()
BEGIN
    SELECT `username`, COALESCE(`annunci_venduti` / `annunci_inseriti` * 100.0, 0.0) AS `percentuale`, `annunci_inseriti` FROM `utente`
    GROUP BY `username`
    ORDER BY `username` ASC;
    -- SELECT `username`, COALESCE(count(`venduto`) / count(*) * 100.0, 0) AS `percentuale` FROM `utente` LEFT JOIN `annuncio` ON `utente`.`username`=`annuncio`.`inserzionista`;
END!
GRANT EXECUTE ON PROCEDURE `generate_report` TO `gestore`!

DELIMITER ;