CREATE TABLE benutzer
(
    id           INTEGER PRIMARY KEY,
    benutzername VARCHAR(64) NOT NULL UNIQUE,
    passwort     varchar(64) NOT NULL
);
