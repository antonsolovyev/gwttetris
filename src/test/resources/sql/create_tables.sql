create table high_score (id SERIAL, name text, score int, date timestamp);

insert into high_score (name, score, date) values ('user1', 100, now());
insert into high_score (name, score, date) values ('user2', 101, now());
insert into high_score (name, score, date) values ('user3', 120, now());
insert into high_score (name, score, date) values ('user4', 400, now());
