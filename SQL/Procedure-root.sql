-- Procedure DEVELOP

USE `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `registrazione_utente`;

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

DELIMITER ;

-- GRANT SU PROCEDURE ------------------------------------------------------------------------------------------------------

GRANT EXECUTE ON PROCEDURE `registrazione_utente` TO `registratore`;