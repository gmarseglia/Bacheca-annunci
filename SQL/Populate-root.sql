USE `bacheca_annunci`;

START TRANSACTION;

-- (RE)INSERIMENTO DI UTENTI, CREDENZIALI e ANAGRAFICA
DELETE FROM `utente`;
DELETE FROM `credenziali`;
DELETE FROM `anagrafica`;

INSERT INTO `utente` (`username`, `contr_seguiti`) VALUES 
	('user1', '2023-01-05 12:00:00');
INSERT INTO `utente` (`username`) VALUES 
	('user2'),
	('userg'),
	('GioAma'),
	('MarBia'),
	('MasFab');
INSERT INTO `credenziali` (`username`, `password`, `ruolo`) VALUES
	('user1',	SHA1('pass'),		'base'),
	('user2',	SHA1('pass'),		'base'),
	('userg',	SHA1('pass'),		'gestore'),
	('GioAma',	SHA1('gvnn'),		'base'),
	('MarBia',	SHA1('mr'),		'gestore'),
	('MasFab',	SHA1('mssm'),		'base');
INSERT INTO `anagrafica` (`codice_fiscale`, `nome`, `cognome`, `sesso`, `data_nascita`, `comune_nascita`, `indirizzo_residenza`, `indirizzo_fatturazione`, `utente`) VALUES
	('GNNGNC00A00A123D', 'Giancarlo', 'Giannini', 'uomo', '2000-01-01', 'Firenze', 'Via Bologna, 4', 'Viale Napoli, 15', 'user1'),
	('LZZNNA00A00A123D', 'Anna', 'Lazzari', 'donna','2001-02-02', 'Bologna', 'Piazza Venezia, 5', 'Piazza Firenze', 'user2'),
	('MRNVLN00A00A123D', 'Valentina', 'Mariani', 'donna', '1985-06-16', 'Venezia', 'Viale Genova, 1', NULL, 'userg'),
	('MTAGVN00A00A123D', 'Giovanni', 'Amato', 'uomo', '1960-01-01', 'Roma', 'Via Milano, 1', 'Via Trieste, 10', 'GioAma'),
	('BNCMRA00A00A123D', 'Maria', 'Bianchi', 'donna', '1965-02-04', 'Milano', 'Piazza Napoli, 2', 'Via Roma, 3', 'MarBia'),
	('FBRMSS00A00A123D', 'Massimo', 'Fabri', 'uomo', '1970-03-07', 'Napoli', 'Viale Firenze, 3', NULL, 'MasFab');


-- (RE)INSERIMENTO DEI RECAPITI E DEI RECAPITI PREFERITI
DELETE FROM `recapito`;
DELETE FROM `recapito_preferito`;

INSERT INTO `recapito` (`valore`, `anagrafica`, `tipo`) VALUES
	('3471122334', 'GNNGNC00A00A123D', 'cellulare'),
	('05112233', 'GNNGNC00A00A123D', 'telefono'),
	('prova@email.com', 'GNNGNC00A00A123D', 'email'),
	('331598647', 'GNNGNC00A00A123D', 'cellulare'),
	('061122334', 'LZZNNA00A00A123D', 'telefono'),
	('04112568', 'LZZNNA00A00A123D', 'telefono'),
	('extra@mail.com', 'LZZNNA00A00A123D', 'email'),
	('b@mail.com', 'MRNVLN00A00A123D', 'email'),
	('3391234567', 'MTAGVN00A00A123D', 'cellulare'),
	('061234564', 'BNCMRA00A00A123D', 'telefono'),
	('a@mail.com', 'FBRMSS00A00A123D', 'email'),
	('finale@mail.com', 'FBRMSS00A00A123D', 'email');
INSERT INTO `recapito_preferito` (`anagrafica`, `recapito`) VALUES
	('GNNGNC00A00A123D', '3471122334'),
	('LZZNNA00A00A123D', '061122334'),
	('MRNVLN00A00A123D', 'b@mail.com'),
	('MTAGVN00A00A123D', '3391234567'),
	('BNCMRA00A00A123D', '061234564'),
	('FBRMSS00A00A123D', 'a@mail.com');


-- (RE)INSERIMENTO DEI MESSAGGI PRIVATI
DELETE FROM `messaggio_privato`;

INSERT INTO `messaggio_privato` (`mittente`, `destinatario`, `inviato`, `testo`) VALUES
	('user1', 'user2', '2023-01-01 00:00:01', 'Il primo messaggio tra 1 e 2.'),
	('user2', 'user1', '2023-01-01 00:01:00', 'Il secondo messaggio tra 1 e 2.'),
	('user1', 'user2', '2023-01-01 00:02:00', 'Il terzo messaggio tra 1 e 2.'),
	('user1', 'userg', '2023-01-02 12:00:01', 'Il primo messaggio tra 1 e g.'),
	('userg', 'user1', '2023-01-02 12:01:00', 'Il secondo messaggio tra 1 e g.'),
	('user1', 'userg', '2023-01-02 12:02:00', 'Il terzo messaggio tra 1 e g.');


-- (RE)INSERIMENTO DELLE CATEGORIE
DELETE FROM `categoria`;

INSERT INTO `categoria` (`nome`, `padre`) VALUES
	('Indumenti', NULL),
	('Pantaloni', 'Indumenti'),
	('Pantaloncini','Pantaloni'),
	('Magliette', 'Indumenti'),
	('Articoli da esterno', NULL),
	('Giardinaggio', 'Articoli da esterno');


-- (RE)INSERIMENTO DEGLI ANNUNCI
DELETE FROM `annuncio`;
ALTER TABLE `annuncio` AUTO_INCREMENT = 1;

INSERT INTO `annuncio` (`inserzionista`, `descrizione`, `categoria`, `inserito`, `modificato`) VALUES
	('user1', 'Abito lungo verde.', 'Indumenti', '2023-01-01 12:00:00', '2023-01-06 22:00:00'),
	('user1', 'Jeans.', 'Pantaloni', '2023-01-01 12:10:00', '2023-01-01 12:10:00'),
	('user2', 'Cargo corti verdi.', 'Pantaloncini', '2023-01-01 09:00:00', '2023-01-07 12:00:00'),
	('user2', 'Cargo corti blu.', 'Pantaloncini', '2023-01-01 09:30:00', '2023-01-10 12:00:00'),
	('userg', 'Tshirt rossa.', 'Magliette', '2023-01-07 11:10:00', '2023-01-07 11:10:00'),
	('GioAma', 'Gazebo blu.', 'Articoli da esterno', '2023-01-07 11:10:00', '2023-01-07 11:10:00'),
	('GioAma', 'Paletta carina.', 'Giardinaggio', '2023-01-07 11:10:00', '2023-01-07 11:10:00');


-- (RE)INSERIMENTO DEI COMMENTI
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


-- (RI)VENDITA DI ANNUNCI
UPDATE `annuncio` SET `venduto` = '2023-01-06 22:00:00' WHERE `numero` = 1;
UPDATE `annuncio` SET `venduto` = '2023-01-10 12:00:00' WHERE `numero` = 4;

-- INSERIMENTO DEI SEGUE
DELETE FROM `segue`;
INSERT INTO `segue` (`utente`, `annuncio`) VALUES
	('user1', 2),
	('user1', 3),
	('GioAma', 1);

COMMIT;
