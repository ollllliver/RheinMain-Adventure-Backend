INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (10, 'wubbo', 'test');

INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (17, 'Oliver', 'Oliver');


INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (30, 'gltf/texturedBox_embedded/Box_regular.gltf', 'Normale Box', null);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (31, 'gltf/duck_embedded/Duck.gltf', 'Ente', 1);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (32, 'gltf/texturedBox_embedded/BoxInterleaved.gltf', 'Eingangs-Box', 0);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (33, 'gltf/duck_embedded/Duck.gltf', 'Eingangs-Ente', 0);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (40, 5, 5, 30, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (41, 6, 6, 30, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (42, 7, 7, 30, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (43, 8, 8, 31, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (44, 0, 6, 33, 20);
