USE `bacheca_annunci`;

DROP PROCEDURE IF EXISTS `login`;

DELIMITER !

CREATE PROCEDURE `login` (IN var_username VARCHAR(30), IN var_password VARCHAR(30))
BEGIN

    SELECT `ruolo`
    FROM `credenziali`
	WHERE `username`=var_username AND `password`=SHA1(var_password);

END !

DELIMITER ;

GRANT EXECUTE ON PROCEDURE `login` TO `registratore`;
GRANT EXECUTE ON PROCEDURE `login` TO `gestore`;
GRANT EXECUTE ON PROCEDURE `login` TO `base`;