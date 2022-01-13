INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (10, 'wubbo', 'test');

INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (11, 'Marvin', 'Marvin');

INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (12, 'Friedrich', 'Friedrich');

INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (17, 'Oliver', 'Oliver');

INSERT INTO level (level_id, name, beschreibung, bewertung, benutzer_id)
VALUES (1, 'Wubbos wahnwitzige Wunderwelt', 'Abenteuer in den Niederlanden', 17, 10);

INSERT INTO raum (raum_id, raum_index, level_level_id)
VALUES (20, 0, 1);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (0, 'gltf/texturedBox_embedded/Box_regular.gltf', 'Wand', null);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (1, '', 'Weg', null);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (2, 'gltf/texturedBox_embedded/Box_regular.gltf', 'Start', 0);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (3, 'gltf/duck_embedded/Duck.gltf', 'Ziel', 1);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (4, 'gltf/key_embedded/Key.gltf', 'Schluessel', 2);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (5, 'gltf/texturedBox_embedded/Box_regular.gltf', 'NPC', 3);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (6, 'gltf/door_embedded/Door.gltf', 'Tuer-Horizontal', 4);

INSERT INTO mobiliar (mobiliar_id, modelluri, name, mobiliartyp)
VALUES (7, 'gltf/texturedBox_embedded/Box_regular.gltf', 'Tuer-Vertikal', 4);


INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (40, 5, 0, 2, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (41, 5, 15, 3, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (42, 5, 7, 4, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (43, 5, 10, 6, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (44, 4, 0, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (45, 4, 1, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (46, 4, 2, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (47, 4, 3, 0, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (48, 4, 4, 0, 20);
