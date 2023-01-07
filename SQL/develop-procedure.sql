USE `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `dettagli_utente`;

DELIMITER !

CREATE PROCEDURE `dettagli_utente` (in var_utente_id VARCHAR(30))
BEGIN
	declare exit handler for sqlexception
    begin
    	rollback;
    	resignal;
    end;

	set transaction isolation level read committed;
	start transaction;

		select `u`.`username`, `u`.`annunci_inseriti`, `u`.`annunci_venduti`,
			`a`.`codice_fiscale`, `a`.`nome` ,`a`.`cognome`, `a`.`sesso`, `a`.`data_nascita`, `a`.`indirizzo_residenza`, `a`.`indirizzo_fatturazione`,
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

DELIMITER ;