USE `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `dettagli_utente`;

DELIMITER !

CREATE PROCEDURE `dettagli_utente` (in var_utente_id VARCHAR(30))
BEGIN
	declare exit handler for exception
	begin
		rollback;
		resignal;
	end;

	set transaction isolation level read committed;
	start transaction;

		select 

	commit;
END!

DELIMITER ;