
-- Table: eventi
CREATE TABLE eventi ( 
    idEvento      VARCHAR PRIMARY KEY
                          UNIQUE,
    nome          VARCHAR,
    stradaCoraggio VARCHAR,
    quartiere     VARCHAR,
    contrade      VARCHAR,
    tipoEvento    VARCHAR, 
);

-- Table: persone
CREATE TABLE persone ( 
    codiceUnivoco VARCHAR PRIMARY KEY,
    ristampaBadge NUMERIC,
    nome          VARCHAR,
    cognome       VARCHAR,
    idGruppo      VARCHAR,
    codiceAgesci  VARCHAR,
    idUnita       VARCHAR,
    quartiere     VARCHAR,
    contrada      VARCHAR 
);


-- Table: assegnamenti
CREATE TABLE assegnamenti ( 
    idAssegnamenti INTEGER PRIMARY KEY AUTOINCREMENT,
    codiceUnivoco  VARCHAR REFERENCES persone ( codiceUnivoco ),
    idEvento       VARCHAR REFERENCES eventi ( idEvento ),
    slot           NUMERIC,
    staffEvento    BOOLEAN DEFAULT ( '' )
);

