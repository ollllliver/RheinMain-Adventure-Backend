INSERT INTO benutzer (benutzer_id, benutzername, passwort)
VALUES (10, 'wubbo', 'test');

INSERT INTO level (level_id, name, min_spieler, max_spieler, bewertung, benutzer_id)
VALUES (1, 'Wubbos wahnwitzige Wunderwelt', 2, 4, 17, 10);

INSERT INTO raum (raum_id, folge_im_level, level_level_id)
VALUES (20, 0, 1);

INSERT INTO mobiliar (mobiliar_id, modelluri, name)
VALUES (30, 'gltf/texturedBox_embedded/Box_regular.gltf', 'Normale Box');

INSERT INTO mobiliar (mobiliar_id, modelluri, name)
VALUES (31, 'gltf/duck_embedded_texture/Duck.gltf', 'Ente');

INSERT INTO mobiliar (mobiliar_id, modelluri, name)
VALUES (32, 'gltf/texturedBox_embedded/BoxInterleaved.gltf', 'Eingangs-Box');

INSERT INTO ausgang (mobiliar_id)
VALUES (31);

INSERT INTO startposition (mobiliar_id)
VALUES (32);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (40, 5, 5, 30, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (41, 6, 6, 30, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (42, 7, 7, 30, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (43, 8, 8, 31, 20);

INSERT INTO raum_mobiliar (raum_mobiliar_id, positionx, positiony, mobiliar_id, raum_id)
VALUES (44, 0, 6, 32, 20);