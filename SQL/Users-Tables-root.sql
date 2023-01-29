-- RECREATE USERS -----------------------------------------------------------------------------------------

-- Drop users
DROP USER IF EXISTS 'base';
DROP USER IF EXISTS 'gestore';
DROP USER IF EXISTS 'registratore';

-- Create users
CREATE USER 'base'@'%' IDENTIFIED WITH mysql_native_password BY 'base';
CREATE USER 'gestore'@'%' IDENTIFIED WITH mysql_native_password BY 'gestore';
CREATE USER 'registratore'@'%' IDENTIFIED WITH mysql_native_password BY 'registratore';


-- RECREATE SCHEMA --------------------------------------------------------------------------------------

-- Drop schema
DROP SCHEMA IF EXISTS bacheca_annunci;

-- Create schema
CREATE SCHEMA bacheca_annunci;
USE bacheca_annunci;


-- RECREATE TABLES ----------------------------------------------------------------------------------------

-- Create tables
USE bacheca_annunci;

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
	FOREIGN KEY (padre) REFERENCES categoria(nome) ON UPDATE CASCADE ON DELETE RESTRICT
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

-- Grant to base
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

-- Grant to gestore
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

-- Grant to registratore
GRANT INSERT ON utente TO 'registratore';
GRANT INSERT,SELECT ON credenziali TO 'registratore';
GRANT INSERT ON anagrafica TO 'registratore';
GRANT INSERT ON recapito TO 'registratore';
GRANT INSERT ON recapito_preferito TO 'registratore';


-- Show result
SHOW TABLES;


-- RECREATE INDEXES ------------------------------------------------------------------------------------

CREATE INDEX by_categoria
	ON annuncio(categoria);

CREATE INDEX by_inserzionista
	ON annuncio(inserzionista);

CREATE FULLTEXT INDEX by_descrizione
	ON annuncio(descrizione);

CREATE INDEX by_anagrafica
	ON recapito(anagrafica);
