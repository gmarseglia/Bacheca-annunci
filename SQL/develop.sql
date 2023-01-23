use `bacheca_annunci`;

DELETE IGNORE FROM `utente` WHERE `username`='crypt';
DELETE IGNORE FROM `credenziali` WHERE `username`='crypt';

INSERT INTO `utente` (`username`) VALUES ('crypt');
INSERT INTO `credenziali` (`username`, `password`, `ruolo`) VALUES
	('crypt', PASSWORD('pass'), 'base');