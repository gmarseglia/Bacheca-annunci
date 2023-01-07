USE `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `vendere_annuncio`;

DELIMITER !


CREATE PROCEDURE `vendere_annuncio` (in var_annuncio_id INT UNSIGNED)
BEGIN
	declare counter INT;
	declare exit handler for sqlexception
	begin
		rollback;
		resignal;
	end;

	start transaction;

	select count(`numero`) into counter 
		from `annunci` 
		where `numero`=var_annuncio_id and `venduto` is not null;

	if(counter=1) then signal sqlstate '45001' set message_text="Annuncio già venduto";
	end if;

	update `annuncio`
		set `venduto`=CURRENT_TIMESTAMP
		where `numero`=var_annuncio_id;

	commit;
END!

DELIMITER ;