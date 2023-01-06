-- Procedure DEVELOP

USE `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `registrazione_utente`;
DROP PROCEDURE IF EXISTS `inserire_annuncio`;
DROP PROCEDURE IF EXISTS `dettagli_annuncio`;
DROP PROCEDURE IF EXISTS `scrivere_commento`;

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

		set var_numero = last_insert_id();
	commit;
END!

CREATE PROCEDURE `scrivere_commento` (
	in var_utente VARCHAR(30), in var_annuncio INT UNSIGNED, in var_testo VARCHAR(250))
BEGIN
	declare exit handler for sqlexception
    begin
    	rollback;
    	resignal;
    end;

	set transaction isolation level read uncommitted; 
	start transaction;
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

DELIMITER ;

-- GRANT SU PROCEDURE ------------------------------------------------------------------------------------------------------

GRANT EXECUTE ON PROCEDURE `registrazione_utente` TO `registratore`;
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `base`;
GRANT EXECUTE ON PROCEDURE `inserire_annuncio` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `base`;
GRANT EXECUTE ON PROCEDURE `dettagli_annuncio` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `scrivere_commento` TO `base`;
GRANT EXECUTE ON PROCEDURE `scrivere_commento` TO `gestore`;
