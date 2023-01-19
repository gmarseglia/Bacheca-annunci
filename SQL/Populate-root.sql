USE `bacheca_annunci`;

-- (RE)INSERIMENTO DI UTENTI E DI RECAPITI EXTRA
DELETE FROM `utente`;
DELETE FROM `credenziali`;
DELETE FROM `anagrafica`;
DELETE FROM `recapito`;
DELETE FROM `recapito_preferito`;


INSERT INTO `utente` (`username`) VALUES 
	('user1'),
	('user2'),
	('userg'),
	('GioAma'),
	('MarBia'),
	('MasFab');

INSERT INTO `credenziali` (`username`, `password`, `ruolo`) VALUES
	('user1',	'pass',		'base'),
	('user2',	'pass',		'base'),
	('userg',	'gest',		'gestore'),
	('GioAma',	'gvnn',		'base'),
	('MarBia',	'mr',		'gestore'),
	('MasFab',	'mssm',		'base');


-- CALL `registrazione_utente` ('user1',	'pass',		'base',		'CGNNME00A00A123D',	'Nome',			'Cognome',		'uomo',		'2000-01-01',	'Comune',	'Indirizzo di residenza',	'Indirizzo di fatturazione',	'1234567890',	'telefono');
-- CALL `registrazione_utente` ('user2',	'pass',		'base',		'CGNNME01A00A123D',	'Nome',			'Cognome',		'donna',	'2001-02-02',	'Comune',	'Indirizzo di residenza',	'Indirizzo di fatturazione',	'1234567891',	'cellulare');
-- CALL `registrazione_utente` ('userg',	'gest',		'gestore',	'CGNNME02A00A123D',	'Nome',			'Cognome',		'donna',	'2002-03-03',	'Comune',	'Indirizzo di residenza',	'Indirizzo di fatturazione',	'e@mail.com',	'email');
-- CALL `registrazione_utente` ('GioAma',	'gvnn',		'base',		'MTAGVN00A00A123D',	'Giovanni',		'Amato',		'uomo',		'1960-01-01',	'Roma',		'Via Milano, 1',			'Via Trieste, 10',				'3391234567',	'cellulare');
-- CALL `registrazione_utente` ('MarBia',	'mr',		'gestore',	'BNCMRA00A00A123D',	'Maria',		'Bianchi',		'donna',	'1965-02-04',	'Milano',	'Piazza Napoli, 2',			'Via Roma, 3',					'061234564',	'telefono');
-- CALL `registrazione_utente` ('MasFab',	'mssm',		'base',		'FBRMSS00A00A123D',	'Massimo',		'Fabri',		'uomo',		'1970-03-07',	'Napoli',	'Viale Firenze, 3',			'Piazza Milano, 11',			'a@mail.com',	'email');

-- CALL `registrazione_utente` ('GiaGia',	'gncrl',	'gestore',	'GNNGNC00A00A123D',	'Giancarlo',	'Giannini',		'uomo',		'1975-04-10',	'Firenze',	'Via Bologna, 4',			'Viale Napoli, 15',				'3471122334',	'cellulare');
-- CALL `registrazione_utente` ('AnnLazz',	'nn',		'base',		'LZZNNA00A00A123D',	'Anna',			'Lazzari',		'donna',	'1980-05-13',	'Bologna',	'Piazza Venezia, 5',		'Piazza Firenze',				'061122334',	'telefono');
-- CALL `registrazione_utente` ('ValMar',	'vlntn',	'gestore',	'MRNVLN00A00A123D',	'Valentina',	'Mariani',		'donna',	'1985-06-16',	'Venezia',	'Viale Genova, 1',			NULL,							'b@mail.com',	'email');
-- CALL `registrazione_utente` ('ClaMor',	'cld',		'base',		'MROCLD00A00A123D',	'Claudia',		'Mori',			'donna',	'1990-07-19',	'Genova',	'Via Palermo, 2',			NULL,							'3317788994',	'cellulare');
-- CALL `registrazione_utente` ('PaoPan',	'pl',		'gestore',	'PNTPLA00A00A123D',	'Paolo',		'Pantalo',		'uomo',		'1995-08-21',	'Palermo',	'Piazza Torino, 3',			NULL,							'065544464',	'telefono');
-- CALL `registrazione_utente` ('FabPod',	'fb',		'base',		'PDSFBA00A00A123D',	'Fabio',		'Podesta',		'uomo',		'2000-09-24',	'Torino',	'Viale Trieste, 4',			NULL,							'c@mail.com',	'email');
-- CALL `registrazione_utente` ('TomSci',	'tmms',		'gestore',	'SCCTMM00A00A123D',	'Tommaso',		'Sciacca',		'uomo',		'2005-10-27',	'Trieste',	'Via Roma, 1',				NULL,							'1122334455',	'telefono');

-- INSERT INTO `recapito` (`valore`, `anagrafica`, `tipo`) VALUES
-- 	('07112233', 'CGNNME00A00A123D', 'telefono'),
-- 	('user@email.com', 'CGNNME00A00A123D', 'email'),
-- 	('05112233', 'MTAGVN00A00A123D', 'telefono'),
-- 	('prova@email.com', 'MTAGVN00A00A123D', 'email'),
-- 	('331598647', 'MTAGVN00A00A123D', 'cellulare'),
-- 	('04112568', 'BNCMRA00A00A123D', 'telefono'),
-- 	('extra@mail.com', 'BNCMRA00A00A123D', 'email'),
-- 	('finale@mail.com', 'FBRMSS00A00A123D', 'email');


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
CALL `inserire_annuncio` ('user', 'Abito lungo.', 100.00, 'Indumenti');
CALL `inserire_annuncio` ('user', 'Jeans.', 25.00, 'Pantaloni');
CALL `inserire_annuncio` ('user2', 'Cargo corti verdi.', 15.99, 'Pantaloncini');
CALL `inserire_annuncio` ('user2', 'Cargo corti blu.', 15.99, 'Pantaloncini');
CALL `inserire_annuncio` ('userg', 'Tshirt rossa.', 5.99, 'Magliette');
CALL `inserire_annuncio` ('GioAma', 'Gazebo.', 320.50, 'Articoli da esterno');
CALL `inserire_annuncio` ('GioAma', 'Paletta carina.', 1.99, 'Giardinaggio');


-- INSERIMENTO DEI COMMENTI


-- INSERIMENTO DEI SEGUE


-- VENDITA DI ANNUNCI
