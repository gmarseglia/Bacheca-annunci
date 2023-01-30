USE `bacheca_annunci`;

START TRANSACTION;

-- (RE)INSERIMENTO DI UTENTI, CREDENZIALI e ANAGRAFICA
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


-- (RE)INSERIMENTO DEI RECAPITI E DEI RECAPITI PREFERITI
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

-- INSERIMENTO DEI SEGUE
DELETE FROM `segue`;
INSERT INTO `segue` (`utente`, `annuncio`, `controllato`) VALUES
	('user1', 2, '2023-01-02 12:10:00'),
	('user1', 3, '2023-01-02 09:00:00'),
	('GioAma', 1, '2023-01-02 12:00:00');

COMMIT;
