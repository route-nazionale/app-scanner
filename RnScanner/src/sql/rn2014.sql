
-- Table: ruolo
CREATE TABLE ruolo ( 
    ruolo       VARCHAR PRIMARY KEY,
    descrizione VARCHAR 
);


-- Table: eventi
CREATE TABLE eventi ( 
    idEvento      VARCHAR PRIMARY KEY
                          UNIQUE,
    nome          VARCHAR,
    stradaCorggio VARCHAR,
    quartiere     VARCHAR,
    contrade      VARCHAR,
    tipoEvento    INT 
);


-- Table: statisticheScansioni
CREATE TABLE statisticheScansioni ( 
    idScansione   INTEGER PRIMARY KEY AUTOINCREMENT,
    codiceUnivoco VARCHAR,
    codiceRisampa VARCHAR,
    time          VARCHAR,
    operatore     VARCHAR,
    slot          INTEGER,
    imei          VARCHAR,
    errore        BOOLEAN,
    entrata       BOOLEAN,
    sync          BOOLEAN,
    idEvento      VARCHAR REFERENCES eventi ( idEvento ) 
);


-- Table: persone
CREATE TABLE persone ( 
    codiceUnivoco VARCHAR PRIMARY KEY,
    ristampaBadge NUMERIC,
    nome          VARCHAR,
    cognome       VARCHAR,
    idGruppo      VARCHAR,
    ruolo         VARCHAR REFERENCES ruolo,
    codiceAgesci  VARCHAR,
    idUnita       VARCHAR,
    quartiere     VARCHAR,
    contrada      VARCHAR 
);


-- Table: assegnamenti
CREATE TABLE assegnamenti ( 
    idAssegnamenti INTEGER PRIMARY KEY AUTOINCREMENT,
    codiceUnivoco  VARCHAR REFERENCES persone ( codiceUnivoco ),
    slot           NUMERIC,
    ruolo          VARCHAR REFERENCES ruolo ( ruolo ),
    staffEvento    BOOLEAN DEFAULT ( '' ),
    idEvento       VARCHAR REFERENCES eventi ( idEvento ) 
);

