use `bacheca_annunci`;

-- Grant to base
GRANT INSERT, SELECT ON `messaggio_privato` TO 'base';
GRANT UPDATE, SELECT ON `utente` TO 'base';
GRANT SELECT ON `anagrafica` TO 'base';
GRANT SELECT ON `recapito` TO 'base';
GRANT SELECT ON `recapito_preferito` TO 'base';
GRANT INSERT, SELECT ON `commento` TO 'base';
GRANT INSERT, SELECT, DELETE ON `commento` TO 'base';
GRANT INSERT, UPDATE, SELECT ON `annuncio` TO 'base';
GRANT SELECT ON `categoria` TO 'base';

-- Grant to gestore
GRANT INSERT, SELECT ON `messaggio_privato` TO 'gestore';
GRANT UPDATE, SELECT ON `utente` TO 'gestore';
GRANT SELECT ON `anagrafica` TO 'gestore';
GRANT SELECT ON `recapito` TO 'gestore';
GRANT SELECT ON `recapito_preferito` TO 'gestore';
GRANT INSERT, SELECT ON `commento` TO 'gestore';
GRANT INSERT, SELECT, DELETE ON `commento` TO 'gestore';
GRANT INSERT, UPDATE, SELECT ON `annuncio` TO 'gestore';
GRANT SELECT ON `categoria` TO 'gestore';
GRANT SELECT ON `utente` TO 'gestore';
GRANT SELECT, INSERT ON `categoria` TO 'gestore';

-- Grant to registratore
GRANT INSERT ON `utente` TO 'registratore';
GRANT INSERT,SELECT ON `credenziali` TO 'registratore';
GRANT INSERT ON `anagrafica` TO 'registratore';
GRANT INSERT ON `recapito` TO 'registratore';
GRANT INSERT ON `recapito_preferito` TO 'registratore';