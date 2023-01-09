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

--		Creo utente
CREATE TABLE utente (
	username 			VARCHAR(30) 	NOT NULL,
	annunci_inseriti 	INT UNSIGNED 	NOT NULL 	DEFAULT 0,
	annunci_venduti 	INT UNSIGNED 	NOT NULL 	DEFAULT 0,
	contr_seguiti 		TIMESTAMP 					DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (username)
) ENGINE = InnoDB;

--		Creo credenziali
CREATE TABLE credenziali (
	username		VARCHAR(30)					NOT NULL,
	password		VARCHAR(30)					NOT NULL,
	ruolo			ENUM('base', 'gestore')		NOT NULL,
	PRIMARY KEY (username),
	FOREIGN KEY (username) REFERENCES utente(username)
		ON DELETE CASCADE
		ON UPDATE CASCADE
) ENGINE = InnoDB;

--		Creo anagrafica
CREATE TABLE anagrafica (
	codice_fiscale			CHAR(16)				NOT NULL,
	nome					VARCHAR(100)			NOT NULL,
	cognome					VARCHAR(100) 			NOT NULL,
	sesso					ENUM('Uomo', 'Donna')	NOT NULL,
	data_nascita    		DATE					NOT NULL,
	comune_nascita			VARCHAR(100)			NOT NULL,
	indirizzo_residenza		VARCHAR(100)			NOT NULL,
	indirizzo_fatturazione	VARCHAR(100),
	utente					VARCHAR(30)				NOT NULL,
	PRIMARY KEY (codice_fiscale),
	FOREIGN KEY (utente) REFERENCES utente(username)
		ON DELETE CASCADE
		ON UPDATE CASCADE
) ENGINE = InnoDB;

--		Creo recapito
CREATE TABLE recapito (
	valore			VARCHAR(60)									NOT NULL,
	anagrafica		CHAR(16)									NOT NULL,
	tipo			ENUM('telefono', 'cellulare', 'email')		NOT NULL,
	PRIMARY KEY (valore),
	KEY (valore, anagrafica),
	FOREIGN KEY (anagrafica) REFERENCES anagrafica(codice_fiscale)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE
) ENGINE = InnoDB;

--		Creo recapito_preferito
CREATE TABLE recapito_preferito (
	anagrafica		CHAR(16),
	recapito		VARCHAR(60) NOT NULL UNIQUE,
	PRIMARY KEY (anagrafica),
	FOREIGN KEY (recapito, anagrafica) REFERENCES recapito(valore, anagrafica)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE
) ENGINE = InnoDB;

--		Creo messaggio_privato
CREATE TABLE messaggio_privato (
	mittente		VARCHAR(30)		NOT NULL,
	destinatario	VARCHAR(30)		NOT NULL,
	inviato			TIMESTAMP		NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	testo			VARCHAR(250)	NOT NULL,
	CONSTRAINT PK_messaggio_privato PRIMARY KEY(mittente, destinatario, inviato),
	FOREIGN KEY (mittente) REFERENCES utente(username) 
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (destinatario) REFERENCES utente(username) 
		ON UPDATE CASCADE
		ON DELETE CASCADE
) ENGINE = InnoDB;

--		Creo categoria
CREATE TABLE categoria (
	nome		VARCHAR(60)		NOT NULL,
	padre		VARCHAR(60),
	PRIMARY KEY (nome),
	FOREIGN KEY (padre) REFERENCES categoria(nome)
	    ON UPDATE CASCADE
	    ON DELETE SET NULL
) ENGINE = InnoDB;

--		Creo annuncio
CREATE TABLE annuncio (
	numero			BIGINT UNSIGNED		NOT NULL	AUTO_INCREMENT,
	inserzionista	VARCHAR(30)			NOT NULL,
	descrizione		TEXT				NOT NULL,
	prezzo			NUMERIC(7, 2),
	categoria		VARCHAR(60)			NOT NULL,
	inserito		TIMESTAMP			NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	modificato		TIMESTAMP			NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	venduto			TIMESTAMP,
	PRIMARY KEY (numero),
	FOREIGN KEY (inserzionista) REFERENCES utente(username)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE,
	FOREIGN KEY (categoria) REFERENCES categoria(nome)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE = InnoDB;

--		Creo segue
CREATE TABLE segue (
	utente		VARCHAR(30)			NOT NULL,
	annuncio	BIGINT UNSIGNED		NOT NULL,
	CONSTRAINT PK_segue PRIMARY KEY (utente, annuncio),
	FOREIGN KEY (utente) REFERENCES utente(username)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
	FOREIGN KEY (annuncio) REFERENCES annuncio(numero)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE
) ENGINE = InnoDB;

--		Creo commento
CREATE TABLE commento (
	utente			VARCHAR(30)			NOT NULL,
	annuncio		BIGINT UNSIGNED		NOT NULL,
	scritto			TIMESTAMP			NOT NULL    DEFAULT CURRENT_TIMESTAMP,
	testo			VARCHAR(250)		NOT NULL,
	CONSTRAINT PK_commento PRIMARY KEY (utente, annuncio, scritto),
	FOREIGN KEY (utente) REFERENCES utente(username)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE,
	FOREIGN KEY (annuncio) REFERENCES annuncio(numero)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE
) ENGINE = InnoDB;



-- RECREATE INDEXES ------------------------------------------------------------------------------------

-- Aggiungo gli indici
CREATE INDEX By_categoria
	ON annuncio(categoria);

CREATE INDEX By_inserzionista
	ON annuncio(inserzionista);

-- CREATE FULLTEXT INDEX By_descrizione
-- 	ON annuncio(descrizione);

CREATE UNIQUE INDEX By_utente
	ON anagrafica(utente);

CREATE INDEX By_anagrafica
	ON recapito(anagrafica);


-- GRANT PRIVILEGES -----------------------------------------------------------------------------------

-- Grant to base
GRANT INSERT, SELECT ON `messaggio_privato` TO 'base';
GRANT UPDATE, SELECT ON `utente` TO 'base';
GRANT SELECT ON `anagrafica` TO 'base';
GRANT SELECT ON `recapito` TO 'base';
GRANT SELECT ON `recapito_preferito` TO 'base';
GRANT INSERT, SELECT ON `commento` TO 'base';
GRANT INSERT, SELECT, DELETE ON `commento` TO 'base';
GRANT INSERT, UPDATE, SELECT ON `annuncio` TO 'base';
GRANT SELECT ON `categoria` TO 'base';

-- Grant to gestore
GRANT SELECT ON `utente` TO 'gestore';
GRANT INSERT ON `categoria` TO 'gestore';

-- Grant to registratore
GRANT INSERT ON `utente` TO 'registratore';
GRANT INSERT,SELECT ON `credenziali` TO 'registratore';
GRANT INSERT ON `anagrafica` TO 'registratore';
GRANT INSERT ON `recapito` TO 'registratore';
GRANT INSERT ON `recapito_preferito` TO 'registratore';


-- Show result
SHOW TABLES;