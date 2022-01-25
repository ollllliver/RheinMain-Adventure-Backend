INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (10, 'wubbo', 'test');

INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (11, 'Marvin', 'Marvin');

INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (12, 'Friedrich', 'Friedrich');

INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (17, 'Oliver', 'Oliver');

INSERT INTO level (level_id, name, beschreibung, bewertung, benutzer_id, ist_freigegeben)
VALUES (1, 'Wubbos wahnwitzige Wunderwelt', 'Abenteuer in den Niederlanden', 17, 10, true);

INSERT INTO raum (raum_id, raum_index, level_level_id)
VALUES (20, 0, 1);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (0, 'gltf/models_embedded/dirt.gltf', 'Wand', null);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (1, '', 'Weg', null);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (2, '', 'Start', 0);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (3, 'gltf/duck_embedded/Duck.gltf', 'Ziel', 1);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (4, 'gltf/models_embedded/key.gltf', 'Schluessel', 2);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (5, 'gltf/models_embedded/Box_regular.gltf', 'NPC', 3);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (6, 'gltf/models_embedded/doorHorizontal.gltf', 'Tuer-Horizontal', 4);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (7, 'gltf/models_embedded/doorVertikal.gltf', 'Tuer-Vertikal', 4);



INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-40, 14, 22, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-41, 3, 0, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-42, 5, 0, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-43, 5, 1, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-44, 5, 2, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-45, 4, 3, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-46, 3, 3, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-47, 2, 3, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-48, 1, 3, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-49, 1, 1, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-50, 2, 1, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-51, 3, 1, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-52, 1, 4, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-54, 0, 5, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-53, 2, 0, 2, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-57, 0, 4, 3, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-55, 4, 0, 4, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_mobiliar_id, raum_raum_id)
VALUES (-56, 0, 3, 7, 20);
