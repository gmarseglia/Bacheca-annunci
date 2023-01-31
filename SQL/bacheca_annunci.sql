START TRANSACTION;

-- RECREATE USERS -----------------------------------------------------------------------------------------
DROP USER IF EXISTS 'base';
CREATE USER 'base'@'%' IDENTIFIED WITH mysql_native_password BY 'base';

DROP USER IF EXISTS 'gestore';
CREATE USER 'gestore'@'%' IDENTIFIED WITH mysql_native_password BY 'gestore';

DROP USER IF EXISTS 'registratore';
CREATE USER 'registratore'@'%' IDENTIFIED WITH mysql_native_password BY 'registratore';

-- RECREATE SCHEMA --------------------------------------------------------------------------------------
DROP SCHEMA IF EXISTS bacheca_annunci;
CREATE SCHEMA bacheca_annunci;
USE bacheca_annunci;

-- RECREATE TABLES ----------------------------------------------------------------------------------------
CREATE TABLE utente (
	username 			VARCHAR(30),
	annunci_inseriti 	INT UNSIGNED 	NOT NULL 	DEFAULT 0,
	annunci_venduti 	INT UNSIGNED 	NOT NULL 	DEFAULT 0,
	PRIMARY KEY (username)
) ENGINE = InnoDB;

CREATE TABLE credenziali (
	username		VARCHAR(30),
	password		CHAR(40)					NOT NULL,
	ruolo			ENUM('base', 'gestore')		NOT NULL,
	PRIMARY KEY (username),
	FOREIGN KEY (username) REFERENCES utente(username) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE anagrafica (
	username				VARCHAR(30),
	codice_fiscale			CHAR(16)				NOT NULL	UNIQUE,
	nome					VARCHAR(100)			NOT NULL,
	cognome					VARCHAR(100) 			NOT NULL,
	sesso					ENUM('uomo', 'donna')	NOT NULL,
	data_nascita    		DATE					NOT NULL,
	comune_nascita			VARCHAR(100)			NOT NULL,
	via_residenza			VARCHAR(100)			NOT NULL,
	civico_residenza		VARCHAR(100)			NOT NULL,
	cap_residenza			CHAR(5)					NOT NULL,
	via_fatturazione		VARCHAR(100),
	civico_fatturazione		VARCHAR(100),
	cap_fatturazione		CHAR(5),
	PRIMARY KEY (username),
	FOREIGN KEY (username) REFERENCES utente(username) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE recapito (
	valore			VARCHAR(60),
	tipo			ENUM('telefono', 'cellulare', 'email'),
	anagrafica		VARCHAR(30)									NOT NULL,
	PRIMARY KEY (valore, tipo),
	KEY (valore, tipo, anagrafica),
	FOREIGN KEY (anagrafica) REFERENCES anagrafica(username) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE recapito_preferito (
	anagrafica	VARCHAR(30),
	valore		VARCHAR(60),
	tipo		ENUM('telefono', 'cellulare', 'email'),
	PRIMARY KEY (anagrafica),
	FOREIGN KEY (valore, tipo, anagrafica) REFERENCES recapito(valore, tipo, anagrafica) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE messaggio_privato (
	mittente		VARCHAR(30),
	destinatario	VARCHAR(30),
	inviato			TIMESTAMP		DEFAULT CURRENT_TIMESTAMP,
	testo			VARCHAR(250)	NOT NULL,
	PRIMARY KEY(mittente, destinatario, inviato),
	FOREIGN KEY (mittente) REFERENCES utente(username) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (destinatario) REFERENCES utente(username) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE categoria (
	nome		VARCHAR(60),
	padre		VARCHAR(60),
	PRIMARY KEY (nome),
	FOREIGN KEY (padre) REFERENCES categoria(nome) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE annuncio (
	numero			INT UNSIGNED		AUTO_INCREMENT,
	descrizione		TEXT				NOT NULL,
	categoria		VARCHAR(60)			NOT NULL,
	inserzionista	VARCHAR(30)			NOT NULL,
	inserito		TIMESTAMP			NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (numero),
	FOREIGN KEY (inserzionista) REFERENCES utente(username) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (categoria) REFERENCES categoria(nome) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE annuncio_disponibile (
	annuncio	INT UNSIGNED,
	modificato	TIMESTAMP	DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (annuncio),
	FOREIGN KEY	(annuncio) REFERENCES annuncio(numero) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE annuncio_venduto (
	annuncio	INT UNSIGNED,
	venduto		TIMESTAMP		NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (annuncio),
	FOREIGN KEY	(annuncio) REFERENCES annuncio(numero) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE segue (
	utente		VARCHAR(30),
	annuncio	INT UNSIGNED,
	controllato	TIMESTAMP		DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (utente, annuncio),
	FOREIGN KEY (utente) REFERENCES utente(username) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (annuncio) REFERENCES annuncio(numero) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE commento (
	utente			VARCHAR(30),
	annuncio		INT UNSIGNED,
	scritto			TIMESTAMP			DEFAULT CURRENT_TIMESTAMP,
	testo			VARCHAR(250)		NOT NULL,
	PRIMARY KEY (utente, annuncio, scritto),
	FOREIGN KEY (utente) REFERENCES utente(username) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (annuncio) REFERENCES annuncio(numero) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;


-- GRANT PRIVILEGES -----------------------------------------------------------------------------------
-- 		Grant to base
GRANT UPDATE, SELECT ON utente TO 'base';
GRANT SELECT ON anagrafica TO 'base';
GRANT SELECT ON recapito TO 'base';
GRANT SELECT ON recapito_preferito TO 'base';
GRANT INSERT, SELECT ON messaggio_privato TO 'base';
GRANT SELECT ON categoria TO 'base';
GRANT INSERT, UPDATE, SELECT ON annuncio TO 'base';
GRANT INSERT, UPDATE, SELECT, DELETE ON annuncio_disponibile TO 'base';
GRANT INSERT, UPDATE, SELECT ON annuncio_venduto TO 'base';
GRANT INSERT, UPDATE, SELECT, DELETE ON segue TO base;
GRANT INSERT, SELECT ON commento TO 'base';

-- 		Grant to gestore
GRANT UPDATE, SELECT ON utente TO 'base';
GRANT SELECT ON anagrafica TO 'base';
GRANT SELECT ON recapito TO 'base';
GRANT SELECT ON recapito_preferito TO 'base';
GRANT INSERT, SELECT ON messaggio_privato TO 'base';
GRANT INSERT, SELECT ON categoria TO 'base';
GRANT INSERT, UPDATE, SELECT ON annuncio TO 'base';
GRANT INSERT, UPDATE, SELECT, DELETE ON annuncio_disponibile TO 'base';
GRANT INSERT, UPDATE, SELECT ON annuncio_venduto TO 'base';
GRANT INSERT, UPDATE, SELECT, DELETE ON segue TO base;
GRANT INSERT, SELECT ON commento TO 'base';

-- 		Grant to registratore
GRANT INSERT ON utente TO 'registratore';
GRANT INSERT,SELECT ON credenziali TO 'registratore';
GRANT INSERT ON anagrafica TO 'registratore';
GRANT INSERT ON recapito TO 'registratore';
GRANT INSERT ON recapito_preferito TO 'registratore';

-- (RE)CREATE INDEXES ------------------------------------------------------------------------------------
CREATE INDEX by_categoria
	ON annuncio(categoria);

CREATE INDEX by_inserzionista
	ON annuncio(inserzionista);

CREATE FULLTEXT INDEX by_descrizione
	ON annuncio(descrizione);

CREATE INDEX by_anagrafica
	ON recapito(anagrafica);

CREATE INDEX by_padre
	ON categoria(padre);

-- (RE)CREATE TRIGGERS ---------------------------------------------------------------------------------
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

-- (RE)CREATE PROCEDURES --------------------------------------------------------------------------------
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
	IN var_username VARCHAR(30), IN var_password VARCHAR(30), IN var_ruolo ENUM('base', 'gestore'),
	IN var_codice_fiscale CHAR(16), IN var_nome VARCHAR(100), IN var_cognome VARCHAR(100), IN var_sesso ENUM('donna', 'uomo'),
	IN var_data_nascita DATE, IN var_comune_nascita VARCHAR(100),
	IN var_via_residenza VARCHAR(100), IN var_civico_residenza VARCHAR(100), IN var_cap_residenza VARCHAR(100),
	IN var_via_fatturazione VARCHAR(100), IN var_civico_fatturazione VARCHAR(100), IN var_cap_fatturazione VARCHAR(100),
	IN var_valore_recapito_preferito VARCHAR(60), IN var_tipo_recapito_preferito ENUM('telefono', 'cellulare', 'email') )
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

	SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED; 
	START TRANSACTION;

		IF (NOT (var_via_fatturazione IS NOT NULL AND var_civico_fatturazione IS NOT NULL AND var_cap_fatturazione IS NOT NULL)
			AND
			NOT (var_via_fatturazione IS NULL AND var_civico_fatturazione IS NULL AND var_cap_fatturazione IS NULL)) THEN
			SIGNAL SQLSTATE  "45012" SET MESSAGE_TEXT="Informazioni registrazione non valide";
		END IF;

		INSERT INTO `utente` (`username`)
			VALUES (var_username);

		INSERT INTO `credenziali` (`username`, `password`, `ruolo`)
			VALUES (var_username, SHA1(var_password), var_ruolo);

		INSERT INTO `anagrafica` (`username`, `codice_fiscale`, `nome`, `cognome`, `sesso`, `data_nascita`, `comune_nascita`,
			`via_residenza`, `civico_residenza`, `cap_residenza`, `via_fatturazione`, `civico_fatturazione`, `cap_fatturazione`)
			VALUES (var_username ,var_codice_fiscale, var_nome, var_cognome, var_sesso, var_data_nascita, var_comune_nascita,
				var_via_residenza, var_civico_residenza, var_cap_residenza,
				var_via_fatturazione, var_civico_fatturazione, var_cap_fatturazione);

		INSERT INTO `recapito` (`valore`, `tipo`, `anagrafica`)
			VALUES (var_valore_recapito_preferito, var_tipo_recapito_preferito, var_username);

		INSERT `recapito_preferito` (`valore`, `tipo`, `anagrafica`)
			VALUES (var_valore_recapito_preferito, var_tipo_recapito_preferito, var_username);
	COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `registrazione_utente` TO `registratore`!

-- U0001
DROP PROCEDURE IF EXISTS `inserire_recapito`!
CREATE PROCEDURE `inserire_recapito` (IN var_valore VARCHAR(60), IN var_anagrafica VARCHAR(30), IN var_tipo ENUM('telefono', 'cellulare', 'email'))
BEGIN
	INSERT IGNORE INTO `recapito` (`valore`, `tipo`, `anagrafica`)
	VALUES (var_valore, var_tipo, var_anagrafica);
END!
GRANT EXECUTE ON PROCEDURE `inserire_recapito` TO `registratore`!

-- A0000
DROP PROCEDURE IF EXISTS `inserire_annuncio`!
CREATE PROCEDURE `inserire_annuncio` (IN var_inserzionista VARCHAR(30), IN var_descrizione TEXT, IN var_categoria VARCHAR(60), out var_numero INT UNSIGNED)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

	SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
	START TRANSACTION;

		INSERT INTO `annuncio` (`descrizione`, `categoria`, `inserzionista`) VALUES
			(var_descrizione, var_categoria, var_inserzionista);

		SET var_numero = LAST_INSERT_ID();

		INSERT INTO `annuncio_disponibile` (`annuncio`) VALUES
			(var_numero);

		UPDATE `utente`
			SET `annunci_inseriti`=`annunci_inseriti`+1
			WHERE `username`=var_inserzionista;

	COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `base`!
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `gestore`!

-- A0100
DROP PROCEDURE IF EXISTS `dettagli_annuncio`!
CREATE PROCEDURE `dettagli_annuncio` (IN var_utente VARCHAR(30), IN var_annuncio_id INT UNSIGNED)
BEGIN
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	START TRANSACTION;

		SELECT `annuncio`.`numero`, `annuncio`.`inserzionista`, `annuncio`.`descrizione`, `annuncio`.`categoria`, `annuncio`.`inserito`,
		`annuncio_disponibile`.`modIFicato`, `annuncio_venduto`.`venduto`,
		`commento`.`utente`, `commento`.`scritto`, `commento`.`testo`
		FROM `annuncio`
		LEFT JOIN `annuncio_disponibile` ON `annuncio`.`numero`=`annuncio_disponibile`.`annuncio`
		LEFT JOIN `annuncio_venduto` ON `annuncio`.`numero`=`annuncio_venduto`.`annuncio`
		LEFT JOIN `commento` ON `annuncio`.`numero`=`commento`.`annuncio`
		WHERE `annuncio`.`numero`=var_annuncio_id AND (`annuncio_disponibile`.`annuncio` OR `annuncio`.`inserzionista`=var_utente)
		ORDER BY `commento`.`scritto` ASC;

		UPDATE `segue`
		SET `controllato`=CURRENT_TIMESTAMP
		WHERE `utente`=var_utente AND `annuncio`=var_annuncio_id;

	COMMIT;

END!
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `base`!
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `gestore`!

-- A0200
DROP PROCEDURE IF EXISTS `select_annunci_categorie_figlie`!
CREATE PROCEDURE `select_annunci_categorie_figlie` (IN var_categoria_id VARCHAR(60))
BEGIN
	DECLARE counter INT;
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;
	SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
	START TRANSACTION;

		SELECT COUNT(*) INTO counter
		FROM `categoria`
		WHERE `nome`=var_categoria_id;

		IF (counter <> 1) THEN
				SIGNAL SQLSTATE "45003" SET MESSAGE_TEXT="Categoria non esistente";
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

		SELECT `a`.`numero`, `a`.`inserzionista`, `a`.`descrizione`, `a`.`categoria`, `a`.`inserito`, `ad`.`modIFicato`
		FROM `annuncio` AS `a`
		INNER JOIN `annuncio_disponibile` AS `ad` ON `a`.`numero`=`ad`.`annuncio`
		INNER JOIN `temp_categoria` AS `tc` ON `a`.`categoria`=`tc`.`nome`
		ORDER BY `numero` ASC;

		DROP TEMPORARY TABLE `temp_categoria`;
	COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_categorie_figlie` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_categorie_figlie` TO `gestore`!

-- A0202
DROP PROCEDURE IF EXISTS `select_annunci_by_inserzionista`!
CREATE PROCEDURE `select_annunci_by_inserzionista` (IN var_inserzionista_id VARCHAR(30))
BEGIN
		SELECT `numero`, `inserzionista`, `descrizione`, `categoria`, `inserito`, `modIFicato`
		FROM `annuncio` INNER JOIN `annuncio_disponibile` ON `annuncio`.`numero`=`annuncio_disponibile`.`annuncio`
		WHERE `inserzionista`=var_inserzionista_id;
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_inserzionista` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_inserzionista` TO `gestore`!

-- A0203
DROP PROCEDURE IF EXISTS `select_annunci_by_descrizione`!
CREATE PROCEDURE `select_annunci_by_descrizione` (IN var_descrizione TEXT)
BEGIN
		SELECT `numero`, `inserzionista`, `descrizione`, `categoria`, `inserito`, `modIFicato`
		FROM `annuncio` INNER JOIN `annuncio_disponibile` ON `annuncio`.`numero`=`annuncio_disponibile`.`annuncio`
		WHERE MATCH(`descrizione`) AGAINST (var_descrizione IN NATURAL LANGUAGE MODE);
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_descrizione` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_by_descrizione` TO `gestore`!

-- A0204
DROP PROCEDURE IF EXISTS `select_annunci_without_clauses`!
CREATE PROCEDURE `select_annunci_without_clauses` ()
BEGIN
	SELECT `numero`, `inserzionista`, `descrizione`, `categoria`, `inserito`, `modIFicato`
	FROM `annuncio` INNER JOIN `annuncio_disponibile` ON `annuncio`.`numero`=`annuncio_disponibile`.`annuncio`;
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_without_clauses` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_without_clauses` TO `gestore`!

-- A0205
DROP PROCEDURE IF EXISTS `select_annunci_inseriti`!
CREATE PROCEDURE `select_annunci_inseriti` (IN var_inserzionista_id VARCHAR(30))
BEGIN
		SELECT `numero`, `inserzionista`, `descrizione`, `categoria`, `inserito`, `modIFicato`, `venduto`
		FROM `annuncio`
		LEFT JOIN `annuncio_disponibile` ON `annuncio`.`numero`=`annuncio_disponibile`.`annuncio`
		LEFT JOIN `annuncio_venduto` ON `annuncio`.`numero`=`annuncio_venduto`.`annuncio`
		WHERE `inserzionista`=var_inserzionista_id;
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_inseriti` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_inseriti` TO `gestore`!

-- A0206
DROP PROCEDURE IF EXISTS `select_annunci_seguiti`!
CREATE PROCEDURE `select_annunci_seguiti` (IN var_utente_id VARCHAR (30))
BEGIN
	SELECT `a`.`numero`, `a`.`inserzionista`, `a`.`descrizione` , `a`.`categoria`, `a`.`inserito`, `ad`.`modIFicato`
	FROM `segue` AS `s`
		INNER JOIN `annuncio` `a` ON `s`.`annuncio`=`a`.`numero`
		INNER JOIN `annuncio_disponibile` AS `ad` ON `a`.`numero`=`ad`.`annuncio`
	WHERE `s`.`utente`=var_utente_id;
END!
GRANT EXECUTE ON PROCEDURE `select_annunci_seguiti` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_annunci_seguiti` TO `gestore`!

-- A0300
DROP PROCEDURE IF EXISTS `seguire_annuncio`!
CREATE PROCEDURE `seguire_annuncio` (IN var_utente_id VARCHAR (30), IN var_annuncio_id INT UNSIGNED)
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
		FROM `annuncio_disponibile`
		WHERE `annuncio`=var_annuncio_id
		INTO counter;

		IF (counter <> 1) THEN
			SIGNAL SQLSTATE '45011' SET MESSAGE_TEXT="Annuncio gia venduto o non esistente.";
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
CREATE PROCEDURE `controllare_annunci_seguiti` (IN var_utente_id VARCHAR (30))
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

	SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
	START TRANSACTION;

		SELECT `a`.`numero`, `a`.`inserzionista`, `a`.`descrizione` , `a`.`categoria`, `a`.`inserito`, `ad`.`modIFicato`, `av`.`venduto`
		FROM `segue` AS `s`
			INNER JOIN `annuncio` `a` ON `s`.`annuncio`=`a`.`numero`
			LEFT JOIN `annuncio_disponibile` AS `ad` ON `a`.`numero`=`ad`.`annuncio`
			LEFT JOIN `annuncio_venduto` AS `av` ON `a`.`numero`=`av`.`annuncio`
		WHERE `s`.`utente`=var_utente_id AND (`ad`.`modIFicato`>=`s`.`controllato` OR `av`.`venduto`>=`s`.`controllato`);

		DELETE `s`
		FROM `segue` AS `s`
		INNER JOIN `annuncio_venduto` AS `av` ON `s`.`annuncio`=`av`.`annuncio`
		WHERE `s`.`utente`=var_utente_id;

	COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `controllare_annunci_seguiti` TO `base`!
GRANT EXECUTE ON PROCEDURE `controllare_annunci_seguiti` TO `gestore`!

-- A0500
DROP PROCEDURE IF EXISTS `vendere_annuncio`!
CREATE PROCEDURE `vendere_annuncio` (IN var_annuncio_id INT UNSIGNED, IN var_utente_id VARCHAR(30))
BEGIN
	DECLARE counter_inserzionista INT;
	DECLARE counter_disponibile INT;

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

	SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
	START TRANSACTION;

	SELECT COUNT(*), COUNT(`ad`.`annuncio`)
	FROM `annuncio` AS `a`
	LEFT JOIN `annuncio_disponibile` AS `ad` ON `a`.`numero`=`ad`.`annuncio`
	WHERE `a`.`inserzionista`=var_utente_id AND `a`.`numero`=var_annuncio_id
	INTO counter_inserzionista, counter_disponibile;

	IF (counter_inserzionista <> 1) THEN
		SIGNAL SQLSTATE '45002' SET MESSAGE_TEXT="Utente non è inserzionista";
	END IF;

	IF (counter_disponibile <> 1) THEN
		SIGNAL SQLSTATE '45011' SET MESSAGE_TEXT="Annuncio già venduto o inesistente";
	END IF;

	INSERT INTO `annuncio_venduto` (`annuncio`) VALUES
		(var_annuncio_id);

	DELETE FROM `annuncio_disponibile`
	WHERE `annuncio`=var_annuncio_id;

	UPDATE `utente`
	SET `annunci_venduti`=`annunci_venduti`+1
	WHERE `username`=var_utente_id;

	COMMIT;
END!
GRANT EXECUTE ON PROCEDURE `vendere_annuncio` TO `base`!
GRANT EXECUTE ON PROCEDURE `vendere_annuncio` TO `gestore`!

-- N0001
DROP PROCEDURE IF EXISTS `dettagli_utente`!
CREATE PROCEDURE `dettagli_utente` (IN var_utente_id VARCHAR(30))
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

	SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
	START TRANSACTION;

		SELECT `codice_fiscale`, `nome`, `cognome`, `sesso`, `data_nascita`, `comune_nascita`, `via_residenza`, `civico_residenza`, `cap_residenza`, `via_fatturazione`, `civico_fatturazione`, `cap_fatturazione`,
			`recapito_preferito`.`valore` AS `valore_preferito`, `recapito_preferito`.`tipo` AS `tipo_preferito`
		FROM `anagrafica`
		LEFT JOIN `recapito_preferito` ON `anagrafica`.`username`=`recapito_preferito`.`anagrafica`
		WHERE `username`=var_utente_id;
  
		SELECT `valore`, `tipo`
		FROM `recapito`
		WHERE `anagrafica`=var_utente_id AND (`valore`, `tipo`) NOT IN (SELECT `valore`, `tipo` FROM `recapito_preferito` WHERE `anagrafica`=var_utente_id);

	COMMIT;
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
	SELECT `destinatario`
	FROM `messaggio_privato`
	WHERE `mittente`=var_target_utente
	UNION
	SELECT `mittente`
	FROM `messaggio_privato`
	WHERE `destinatario`=var_target_utente;
END!
GRANT EXECUTE ON PROCEDURE `select_utenti_con_messaggi` TO `base`!
GRANT EXECUTE ON PROCEDURE `select_utenti_con_messaggi` TO `gestore`!

-- C0000
DROP PROCEDURE IF EXISTS `scrivere_commento`!
CREATE PROCEDURE `scrivere_commento` (IN var_utente VARCHAR(30), IN var_annuncio INT UNSIGNED, IN var_testo VARCHAR(250))
BEGIN
	DECLARE counter INT;
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

	SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
	START TRANSACTION;

		SELECT count(`annuncio`) INTO counter
			FROM `annuncio_disponibile`
			WHERE `annuncio`=var_annuncio;

		IF(counter<>1) THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT="Annuncio già venduto";
		END IF;

		INSERT INTO `commento` (`utente`, `annuncio`, `testo`)
			VALUES (var_utente, var_annuncio, var_testo);

		UPDATE `annuncio_disponibile`
			SET `modIFicato`=CURRENT_TIMESTAMP
			WHERE `annuncio`=var_annuncio;
	COMMIT;
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
	ORDER BY `username` ASC;
	-- SELECT `username`, COALESCE(count(`venduto`) / count(*) * 100.0, 0) AS `percentuale` FROM `utente` LEFT JOIN `annuncio` ON `utente`.`username`=`annuncio`.`inserzionista`;
END!
GRANT EXECUTE ON PROCEDURE `generate_report` TO `gestore`!

DELIMITER ;

-- (RE)POPULATE DB --------------------------------------------------------------------------------
DELETE FROM `utente`;
DELETE FROM `credenziali`;
DELETE FROM `anagrafica`;

INSERT INTO `utente` (`username`, `annunci_inseriti`, `annunci_venduti`) VALUES 
	('user1', 2, 1),
	('user2', 2, 1),
	('userg', 1, 0),
	('GioAma', 2, 0),
	('MarBia', 0, 0),
	('MasFab', 0, 0);
INSERT INTO `credenziali` (`username`, `password`, `ruolo`) VALUES
	('user1',	SHA1('pass'),		'base'),
	('user2',	SHA1('pass'),		'base'),
	('userg',	SHA1('pass'),		'gestore'),
	('GioAma',	SHA1('gvnn'),		'base'),
	('MarBia',	SHA1('mr'),		'gestore'),
	('MasFab',	SHA1('mssm'),		'base');
INSERT INTO `anagrafica` (`username`, `codice_fiscale`, `nome`, `cognome`, `sesso`, `data_nascita`, `comune_nascita`, `via_residenza`, `civico_residenza`, `cap_residenza`, `via_fatturazione`, `civico_fatturazione`, `cap_fatturazione`) VALUES
	('user1', 'GNNGNC00A00A123D', 'Giancarlo', 'Giannini', 'uomo', '2000-01-01', 'Firenze', 'Via Bologna', '4', '00000', 'Viale Napoli', '15', '00001'),
	('user2', 'LZZNNA00A00A123D', 'Anna', 'Lazzari', 'donna','2001-02-02', 'Bologna', 'Piazza Venezia', '5', '00002', 'Piazza Firenze', '1', '00002'),
	('userg', 'MRNVLN00A00A123D', 'Valentina', 'Mariani', 'donna', '1985-06-16', 'Venezia', 'Viale Genova', '1', '00003', NULL, NULL, NULL),
	('GioAma', 'MTAGVN00A00A123D', 'Giovanni', 'Amato', 'uomo', '1960-01-01', 'Roma', 'Via Milano', '1', '00004', 'Via Trieste', '10', '00004'),
	('MarBia', 'BNCMRA00A00A123D', 'Maria', 'Bianchi', 'donna', '1965-02-04', 'Milano', 'Piazza Napoli', '2', '00005', 'Via Roma', '3', '00005'),
	('MasFab', 'FBRMSS00A00A123D', 'Massimo', 'Fabri', 'uomo', '1970-03-07', 'Napoli', 'Viale Firenze', '3', '00006', NULL, NULL, NULL);

DELETE FROM `recapito`;
DELETE FROM `recapito_preferito`;

INSERT INTO `recapito` (`valore`, `anagrafica`, `tipo`) VALUES
	('3471122334', 'user1', 'cellulare'),
	('05112233', 'user1', 'telefono'),
	('prova@email.com', 'user1', 'email'),
	('331598647', 'user1', 'cellulare'),
	('061122334', 'user2', 'telefono'),
	('04112568', 'user2', 'telefono'),
	('extra@mail.com', 'user2', 'email'),
	('b@mail.com', 'userg', 'email'),
	('3391234567', 'GioAma', 'cellulare'),
	('061234564', 'MarBia', 'telefono'),
	('a@mail.com', 'MasFab', 'email'),
	('finale@mail.com', 'MasFab', 'email');
INSERT INTO `recapito_preferito` (`anagrafica`, `valore`, `tipo`) VALUES
	('user1', '3471122334', 'cellulare'),
	('user2', '061122334', 'telefono'),
	('userg', 'b@mail.com', 'email'),
	('GioAma', '3391234567', 'cellulare'),
	('MarBia', '061234564', 'telefono'),
	('MasFab', 'a@mail.com', 'email');

DELETE FROM `messaggio_privato`;

INSERT INTO `messaggio_privato` (`mittente`, `destinatario`, `inviato`, `testo`) VALUES
	('user1', 'user2', '2023-01-01 00:00:01', 'Il primo messaggio tra 1 e 2.'),
	('user2', 'user1', '2023-01-01 00:01:00', 'Il secondo messaggio tra 1 e 2.'),
	('user1', 'user2', '2023-01-01 00:02:00', 'Il terzo messaggio tra 1 e 2.'),
	('user1', 'userg', '2023-01-02 12:00:01', 'Il primo messaggio tra 1 e g.'),
	('userg', 'user1', '2023-01-02 12:01:00', 'Il secondo messaggio tra 1 e g.'),
	('user1', 'userg', '2023-01-02 12:02:00', 'Il terzo messaggio tra 1 e g.');

DELETE FROM `categoria`;

INSERT INTO `categoria` (`nome`, `padre`) VALUES
	('Indumenti', NULL),
	('Pantaloni', 'Indumenti'),
	('Pantaloncini','Pantaloni'),
	('Magliette', 'Indumenti'),
	('Articoli da esterno', NULL),
	('Giardinaggio', 'Articoli da esterno');

DELETE FROM `annuncio`;
ALTER TABLE `annuncio` AUTO_INCREMENT = 1;

INSERT INTO `annuncio` (`inserzionista`, `descrizione`, `categoria`, `inserito`) VALUES
	('user1', 'Abito lungo verde.', 'Indumenti', '2023-01-01 12:00:00'),
	('user1', 'Jeans.', 'Pantaloni', '2023-01-01 12:10:00'),
	('user2', 'Cargo corti verdi.', 'Pantaloncini', '2023-01-01 09:00:00'),
	('user2', 'Cargo corti blu.', 'Pantaloncini', '2023-01-01 09:30:00'),
	('userg', 'Tshirt rossa.', 'Magliette', '2023-01-07 11:10:00'),
	('GioAma', 'Gazebo blu.', 'Articoli da esterno', '2023-01-07 11:10:00'),
	('GioAma', 'Paletta carina.', 'Giardinaggio', '2023-01-07 11:10:00');

INSERT INTO `annuncio_disponibile` (`annuncio`, `modificato`) VALUES
	(2, NULL),
	(3, '2023-01-07 12:00:00'),
	(5, NULL),
	(6, NULL),
	(7, NULL);

INSERT INTO `annuncio_venduto` (`annuncio`, `venduto`) VALUES
	(1, '2023-01-06 22:00:00'),
	(4, '2023-01-10 12:00:00');

DELETE FROM `commento`;

INSERT INTO `commento` (`utente`, `annuncio`, `scritto`,  `testo`) VALUES
	('user2', 1,  '2023-01-02 12:00:00', 'Veste bene?'),
	('user1', 1,  '2023-01-03 11:00:00', 'Si.'),
	('user2', 1,  '2023-01-03 13:15:00', 'Sono interessata.'),
	('user1', 1,  '2023-01-05 12:00:00', 'Scrivimi.'),
	('userg', 1,  '2023-01-06 12:00:00', 'Anche io sono interessata.'),
	('user1', 3,  '2023-01-02 11:05:00', 'Molto carini.'),
	('user1', 4,  '2023-01-02 11:10:00', 'Molto carini anche questi.'),
	('GioAma', 3, '2023-01-07 12:00:00', 'Non sembrano di buona qualità.');

DELETE FROM `segue`;
INSERT INTO `segue` (`utente`, `annuncio`, `controllato`) VALUES
	('user1', 2, '2023-01-02 12:10:00'),
	('user1', 3, '2023-01-02 09:00:00'),
	('GioAma', 1, '2023-01-02 12:00:00');

COMMIT;