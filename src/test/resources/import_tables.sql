INSERT INTO users ( name, email)
VALUES ( 'name', 'email@mail.ru'),
( 'name2', 'email2@mail.ru'),
( 'name3', 'email3@mail.ru');

INSERT INTO requests ( description, requestor_id, created)
VALUES ( 'ItemDescription1', 2, '2023-08-01 11:30:00'),
( 'ItemDescription2', 2, '2023-08-10 11:30:00'),
( 'ItemDescription3', 3, '2023-07-01 11:30:00');

INSERT INTO items ( name, description, is_available, owner_id, request_id)
VALUES ( 'name', 'Description', TRUE, 1,1),
( 'name2', 'description2', FALSE, 1,1),
( 'name3', 'dEscription3', TRUE, 1,2),
( 'name4', 'someText4', TRUE, 2,null),
( 'name5', 'someText5', FALSE, 2,null),
( 'name6', 'someText6', TRUE, 3,null);


INSERT INTO booking (start_date, end_date, item_id, booker_id, status)
VALUES ('2023-08-09 12:00:00','2023-08-10 15:00:00',4,1,'WAITING'),
('2023-08-07 12:00:00','2023-08-08 12:00:00',5,1,'WAITING'),
('2023-08-11 12:00:00','2023-08-12 12:00:00',6,1,'WAITING'),
('2023-08-12 12:00:00','2023-08-13 12:00:00',4,3,'WAITING');

INSERT INTO comments (text, item_id, author_id, created)
VALUES ('text1', 1, 2, '2023-08-08 12:00:00'),
('text2', 2, 2, '2023-08-06 12:00:00'),
('text3', 1, 2, '2023-08-09 12:00:00');