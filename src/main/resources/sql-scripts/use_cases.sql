drop trigger populate_stopovers;

select * from airport_names;
select * from city_names;

select * from airport1_users;
select * from companies;
select * from feedback;
select * from airplanes;
select * from flights;
select * from stopovers;
select * from airport1_tickets;
select * from airport1_flights_staff;
select * from airport1_flights_cache;

insert into airport1_users values (101, 'Dave', 'Miller', to_date('25/01/1985', 'dd/mm/yyyy'), 'Ohio', '0794781254', 'dave101@user.com', '8GMLWPO90S', 'admin', 0);



/* USE CASE 1 */
/* register a new user and log him in */
insert into airport1_users values(100, 'John', 'White', to_date('16/03/1990', 'dd/mm/yyyy'), 'New York', '0789438629', 'john100@user.com', '8DHW6LC20O', 'user', 0);
select id from airport1_users where email = 'john1@user.com' and password = '8DHW6LC20O';
update airport1_users set logged = 1 where id = 100;
commit;

/* USE CASE 2 */
/* log out user */
update airport1_users set logged = 0 where id = 100;

/* USE CASE 3 */
/* post feedback to a company following a flight */
select id from airport1_users where email = 'jorge50@user.com' and password = 'RQBVW5FKBA';
update airport1_users set logged = 1 where id = 50;
select id from companies where name = 'SWISS';
insert into feedback values(50, 19, 'Horrible flight! Dirty seats and uneducated flight attendants!');
update airport1_users set logged = 0 where id = 50;
commit;

/* USE CASE 4 */
/* analyze feedback by company */
select id from companies where name = 'SWISS';
select * from feedback where company_id = 19;
commit;

/* USE CASE 5 */
/* register a new flight into the database - done by an admin */
select id from airport1_users where email = 'dave101@user.com' and password = '8GMLWPO90S' and type = 'admin';
update airport1_users set logged = 1 where id = 11;
insert into flights values(200, date '2021-01-14', 180, 0, 2500, 1, 'Dallas/Fort Worth', 15, 75, 5, 450, 800);
commit;
/
begin
    dbms_snapshot.refresh('c##airport1.flight_details@dblink', 'f');
end;
/
insert into stopovers values (1, 200, 'Copenhagen Airport', 90, 225, 400, date '2021-01-14');
insert into airport1_flights_staff values(200, 1, 'pilot');
insert into airport1_flights_staff values(200, 4, 'fa');
insert into airport1_flights_staff values(200, 5, 'fa');
insert into airport1_flights_staff values(200, 6, 'fa');
update airport1_users set logged = 0 where id = 11;
commit;


/* USE CASE 6 */
/* remove a successfully completed flight - done by an admin */
select id from airport1_users where email = 'dave101@user.com' and password = '8GMLWPO90S' and type = 'admin';
update airport1_users set logged = 1 where id = 11;
select * from flights where id = 200;
delete from stopovers where flight_id = 200;
delete from airport1_flights_staff where flight_id = 200;
insert into airport1_flights_cache values(200, date '2021-01-14', 180, 0, 2500, 1, 'Dallas/Fort Worth', 15, 75, 5, 450, 800, 'success', null);
delete from flights where id = 200;
update airport1_users set logged = 0 where id = 11;
commit;

/* USE CASE 7 */
/* reschedule a flight - done by an admin */
select id from airport1_users where email = 'dave101@user.com' and password = '8GMLWPO90S' and type = 'admin';
update airport1_users set logged = 1 where id = 11;
select * from flights where id = 75;
insert into airport1_flights_cache values(75, date '2021-10-10', 242, 0, 4230, 2, 'Houston IAH', 9, 53, 19, 382, 559, 'bad weather', date '2021-10-30');
update flights set departure_date = date '2021-10-30' where id = 11;
update airport1_users set logged = 0 where id = 11;
commit;

/* USE CASE 8 */
/* buy a ticket  */
select id from airport1_users where email = 'john1@user.com' and password = '8DHW6LC20O';
update airport1_users set logged = 1 where id = 100;
select * from flights where id = 60;
insert into airport1_tickets values(300, 252, 1, 100, 60, 0);
update airport1_users set type = 'passenger' where id = 100;
update flights set first_class_seats = first_class_seats - 1 where id = 60;
update airport1_users set logged = 0 where id = 100;
commit;

/* USE CASE 9 */
/* retract a ticket */
select id from airport1_users where email = 'john1@user.com' and password = '8DHW6LC20O';
update airport1_users set logged = 1 where id = 100;
delete from airport1_tickets where passenger_id = 100 and flight_id = 60;
update airport1_users set type = 'user' where id = 100;
update flights set first_class_seats = first_class_seats + 1 where id = 60;
update airport1_users set logged = 0 where id = 100;
commit;

/* USE CASE 10 */
/* check the total price per flight - done by a manager */
select id from airport1_users where email = 'dave101@user.com' and password = '8GMLWPO90S' and type = 'admin';
update airport1_users set logged = 1 where id = 11;
select * from flights;
select * from stopovers;
update airport1_users set logged = 0 where id = 11;
commit;