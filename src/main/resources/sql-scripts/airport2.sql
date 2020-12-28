/* DATABASE MANAGING AN AIRPORT - PART 2 */
set serveroutput on;

create database link dblink
    connect to c##airport1 identified by c##airport1
    using 
        '(DESCRIPTION =
            (ADDRESS = (PROTOCOL = TCP)(HOST = 127.0.0.1)(PORT = 1521))
            (CONNECT_DATA =
            (SERVER = DEDICATED)
            (SERVICE_NAME = XE)
            )
        )';
        
drop synonym airport1_users;
create synonym airport1_users for c##airport1.users@dblink;
drop synonym airport1_tickets;
create synonym airport1_tickets for c##airport1.tickets@dblink;
drop synonym airport1_flights_staff;
create synonym airport1_flights_staff for c##airport1.flights_staff@dblink;
drop synonym airport1_flights_deposit;
create synonym airport1_flights_deposit for c##airport1.flights_deposit@dblink;

drop materialized view log on flights;
drop table stopovers cascade constraints;
drop table flights cascade constraints;
drop table airplanes cascade constraints;
drop table companies cascade constraints;
drop table feedback cascade constraints;



/* COMPANIES */
create table companies (
    id number(10,0) primary key,
    name varchar(100) not null,
    address varchar(100),
 	city varchar(100) not null,
    phone_number varchar(10) unique,
    email varchar(100) not null unique
) tablespace airport;

insert into companies values(1, 'United Airlines', 'Inc. 233 S. Wacker Drive Chicago, IL 60606 United States', 'Chicago', '0265475435', 'united_airlines@office.com');
insert into companies values(2, 'Delta Air Lines', 'Inc.1030 Delta Boulevard Atlanta, GA 30354-1989', 'Atlanta', '4047152600', 'delta_air@office.com');
insert into companies values(4, 'Emirates', 'Avenue des Arts, 27 1040 Brussels Belgium', 'Brussels', '3227007007', 'emirates@office.com');
insert into companies values(5, 'American Airlines', '6201 15th Avenue Operations Center Brooklyn, NY 11219', 'NewYork', '8179631234', 'american_airlines@office.com');
insert into companies values(6, 'Southwest Airlines', 'Southwest Airlines P.O. 36647-1CR Dallas, Texas 75235', 'Dallas', '1800987154', 'southwest_airlines@office.com');
insert into companies values(7, 'Lufthansa', 'Friedrichshafen 88046 Am Flugplatz 64', 'Friedrichshafen', '1983457135', 'lufthansa@office.com');
insert into companies values(8, 'British Airways', 'Waterside PO Box 365 Harmondsworth, UB7 0GB United Kingdom', 'GB', '5315476846', 'british_airways@office.com');
insert into companies values(9, 'Air France', '45 Rue de Paris, 95747 Roissy CDG Cedex, France', 'Paris', '6472136487', 'air_france@office.com');
insert into companies values(10, 'China Southern', 'Aksu Outlet, 11 South Avenue, Aksu, Xinjiang', 'Aksu', '9972152777', 'china_southern@office.com');
insert into companies values(11, 'Cathay Pacific', '8 Scenic Road, Hong Kong International Airport, Lantau, Hong Kong', 'Lantau', '1657324951', 'cathay_pacific@office.com');
insert into companies values(12, 'Air China', 'Room 101C, F1, Fairmont Tower, No.33 Guangshun North Street, Chaoyang District, Beijing, 100102', 'Beijing', '9657412358', 'air_china@office.com');
insert into companies values(13, 'US Airways', '100 W Rio Salado Pkwy Tempe, AZ 85281', 'Tempe', '3657491225', 'us_irways@office.com');
insert into companies values(14, 'China Eastern', 'Block B, Guomen Buliding, No.12 Jingan Dongli, Chaoyang District,  Beijing', 'Chaoyang ', '6332400789', 'china_eastern@office.com');
insert into companies values(15, 'Turkish Airlines', 'Yeºilköy Bilet Sat?º Ofisi Binas?, Atatürk Havaliman? 34149 ?STANBUL', '?STANBUL', '0930042287', 'turkish_airlines@office.com');
insert into companies values(16, 'Qantas Airways', 'Mascot, Noul Wales de Sud 2020, Australia', 'Sydney', '3621403581', 'qantas_airways@office.com');
insert into companies values(17, 'Qatar Airways', 'Porto Arabia, Parcel 1, Unit 16, Pearl Qatar, West Bay Area', 'Doha', '7000254794', 'qatar_airways@office.com');
insert into companies values(18, 'KLM', 'Amsterdamseweg 55 1182 GP Amstelveen The Netherlands', 'Amstelveen', '9648311450', 'klm@office.com');
insert into companies values(19, 'SWISS', 'Swiss International Air Lines Ltd. 29-31, route de l’Aéroport CP 191 1215 Geneva 15', 'Geneva', '0830265413', 'swiss@office.com');
insert into companies values(20, 'Thai Airways', '89 Vibhavadi Rangsit Road Bangkok 10900, Thailand', 'Bangkok', '3001245785', 'thai_airways@office.com');
insert into companies values(21, 'Air Canada', '7373 Boulevard de la Côte-Vertu Saint Laurent', 'Montreal', '0254133870', 'air_canada@office.com');
insert into companies values(22, 'All Nippon Airways', 'Shiodome-City Center, 1-5-2, Higashi-Shimbashi, Minato-ku, Tokyo 105-7140, Japan', 'Tokyo', '7651246301', 'all_nippon_airways@office.com');
insert into companies values(23, 'TAM', 'Av Jurandir, 856 - Lote 4 - (Jardim Ceci), 04072-000 Sao Paulo, Brazil', 'Sao Paulo', '7430012354', 'tam@office.com');
insert into companies values(24, 'Ryanair', 'Airside Business Park Swords Co. Dublin Ireland', 'Dublin', '6347500123', 'ryanair@office.com');
insert into companies values(25, 'Japan Airlines', 'Nomura Real Estate Bldg., 2-4-11 Higashi-Shinagawa, Shinagawa-ku, Tokyo', 'Tokyo', '0012478950', 'japan_airlines@office.com');

commit;
--select * from companies;



drop materialized view user_ids;
create materialized view user_ids as select id from airport1_users;
/
begin
    dbms_snapshot.refresh('user_ids', 'f');
end;
/
select * from user_ids;

/* FEEDBACK */
create table feedback(
  	user_id number(10, 0),
  	company_id number(10, 0),
--    foreign key (company_id) references companies(id),
--    foreign key (user_id) references user_ids(id) on delete set null,
  	message varchar(500) not null
) tablespace airport;

insert into feedback values (1, 1, 'Very good services! I''ll definitely fly again with you Sir!');

commit;
--select * from feedback;



/* AIRPLANES */
create table airplanes (
    id number(10, 0) primary key,
    first_class_seats number(10, 0) not null,
    second_class_seats number(10,0) not null,
    available number(1) default(0),
    company_id number(10, 0),
    foreign key (company_id) references companies(id) on delete cascade validate,
    constraint check_available check (available = 0 or available = 1)
) tablespace airport;

declare
    V_NR_AIRPLANES integer := 20;
    
    v_counter integer := 1;
    v_nr_companies number(10);
    v_random_company_index number(10);
    v_company_id companies.id%type;
begin
    select count(*) into v_nr_companies from companies;
    for v_counter in 1..V_NR_AIRPLANES loop
        v_random_company_index := trunc(sys.dbms_random.value(1, v_nr_companies));
        select id into v_company_id from (select * from (select * from (select * from companies order by id) where rownum <= v_random_company_index) order by id desc) where rownum = 1;
        insert into airplanes values (v_counter, trunc(dbms_random.value(50, 101)), trunc(dbms_random.value(10, 21)), 0, v_company_id);
    end loop;
end;
/

commit;
--select * from airplanes;



/* FLIGHTS + STOPOVERS */
create table flights (
  	id number(10, 0) primary key,
  	departure_date date not null,
  	duration number(10, 0) not null,
  	delay number(10, 0),
  	distance number(10, 0) not null,
  	stopovers number(10, 0),
  	airport_name varchar(100) not null,
  	airplane_id number(10, 0) not null,
    first_class_seats number(10, 0) not null,
    second_class_seats number(10, 0) not null,
    first_class_price number(10, 0) not null,
    second_class_price number(10, 0) not null,
    foreign key (airplane_id) references airplanes(id) on delete cascade validate
) tablespace airport;

create materialized view log on flights pctfree 5 tablespace airport storage (initial 10K next 10K); 

create table stopovers(
  	stop_number number(10,0) not null,
  	flight_id number(10,0),
  	airport_name varchar(100),
  	time number(10,0) not null,
    price_first_class number(10,0) not null,
    price_second_class number(10,0) not null,
  	departure_date date not null,
  	foreign key (flight_id) references flights(id) on delete cascade validate
) tablespace airport;

drop trigger populate_stopovers;
create trigger populate_stopovers
after insert on flights
for each row
declare
    v_counter number(10);
    v_random_airport number(10); -- random airport from the list of airport names
    v_nr_airports number(10); -- the total number of destination airports
    v_airport_name varchar(100); -- the name of a randomly chosen airport
    v_time number(10); -- the time spent during a stopover
    v_arrived_date date; -- the date the airplane reached that intermediary destination
    v_duration number(10); -- the total duration of the flight
    v_decrement_duration number(10); -- after each stopover, how much time is there left until the final destination
    v_increment_date date; -- departure date from each intermediary location
    v_random_part_of_duration number(10); -- random number representing how much time the flight took until reaching a certain intermediary location
    v_price_first_class number(10); -- number representing the price of a first class seat to that specific stopover
    v_price_second_class number(10); -- number representing the price of a second class seat to that specific stopover
begin
    select count(*) into v_nr_airports from airport_names;
    v_decrement_duration := :new.duration;
    v_increment_date := :new.departure_date;
    if(:new.stopovers <> 0) then
        for v_counter in 1..:new.stopovers loop
            v_random_airport := trunc(sys.dbms_random.value(1, v_nr_airports + 1));
            select name into v_airport_name from (select * from (select * from (select * from airport_names order by id) where rownum <= v_random_airport) order by id desc) where rownum = 1;
            
            v_time := sys.dbms_random.value(10, 31);
            v_random_part_of_duration := trunc(sys.dbms_random.value(1, ((:new.duration / :new.stopovers) - v_time) + 1));
            v_arrived_date := v_increment_date + v_random_part_of_duration / 24 / 60;
            v_decrement_duration := v_decrement_duration - v_random_part_of_duration;
            v_increment_date :=  v_arrived_date + v_time / 24 / 60;
            
            v_price_first_class := (:new.first_class_price / (:new.stopovers + 1)) * v_counter;
            v_price_second_class := (:new.second_class_price / (:new.stopovers + 1)) * v_counter;
            
            insert into stopovers values (v_counter, :new.id, v_airport_name, v_time, v_price_first_class, v_price_second_class, v_increment_date);
        end loop;
    end if;
end;
/

declare
    V_NR_FLIGHTS integer := 100;
    
    v_id number(10, 0);
    v_departure_date date;
    v_duration number(10, 0);
    v_delay number(10, 0);
    v_distance number(10, 0);
    v_stopovers number(10, 0);
    v_airport_name varchar(100);
    v_airplane_id varchar(100);
    v_first_class_seats number(10, 0);
    v_second_class_seats number(10, 0);
    v_first_class_price number(10, 0);
    v_second_class_price number(10, 0);
    
    v_counter integer := 1;
    
    v_nr_airplanes number(10);
    v_random_index number(10);
    v_nr_airports number(10);
    v_random_day number(10);
    v_random_month number(10);
begin
    select count(*) into v_nr_airports from airport_names;
        
    for v_counter in 1..V_NR_FLIGHTS loop
        /* ID */
        v_id := v_counter;
        
        /* DEPARTURE DATE */
        v_random_month := trunc(sys.dbms_random.value(1, 13));
        if(v_random_month = 1 or v_random_month = 3 or v_random_month = 5 or v_random_month = 7 or v_random_month = 8 or v_random_month = 10 or v_random_month = 12) then
            v_random_day := trunc(sys.dbms_random.value(1, 32));
        end if;
        if(v_random_month = 4 or v_random_month = 6 or v_random_month = 9 or v_random_month = 11) then
            v_random_day := trunc(sys.dbms_random.value(1, 31));
        end if;
        if(v_random_month = 2) then
            v_random_day := trunc(sys.dbms_random.value(1, 29));
        end if;
        v_departure_date := to_date('' || v_random_day || '/' || v_random_month || '/' || '2021' || '', 'dd/mm/yyyy');
        
        /* DURATION */
        v_duration := trunc(sys.dbms_random.value(60, 250));
        
        /* DELAY */
        v_delay := null;
        
        /* DISTANCE */
        v_distance := trunc(sys.dbms_random.value(300, 10000));
        
        /* STOPOVERS */
        v_stopovers := trunc(sys.dbms_random.value(0, 4));
        
        /* AIRPORT NAME */
        v_random_index := trunc(sys.dbms_random.value(1, v_nr_airports));
        select name into v_airport_name from (select * from (select * from (select * from airport_names order by id) where rownum <= v_random_index) order by id desc) where rownum = 1;
        
        /* AIRPLANE COMPANY + FIRST CLASS SEATS + SECOND CLASS SEATS */
        select count(*) into v_nr_airplanes from airplanes;
        v_random_index := trunc(sys.dbms_random.value(1, v_nr_airplanes));
        select id, first_class_seats, second_class_seats into v_airplane_id, v_first_class_seats, v_second_class_seats from (select * from (select * from (select * from airplanes order by id) where rownum <= v_random_index) order by id desc) where rownum = 1;
        
        /* FIRST CLASS PRICE */
        v_first_class_price := trunc(dbms_random.value(100, 500));
        
        /* SECOND CLASS PRICE */
        v_second_class_price := trunc(dbms_random.value(400, 1000));
    
        insert into flights values (v_id, v_departure_date, v_duration, v_delay, v_distance, v_stopovers, v_airport_name, v_airplane_id, v_first_class_seats, v_second_class_seats, v_first_class_price, v_second_class_price);
    end loop;
end;
/

insert into flights values (101, date '2021-01-14', 180, 0, 2500, 3, 'Beijing Capital International Airport', 1, 150, 80, 450, 800);
insert into flights values (102, date '2021-02-09', 240, 0, 3000, 2, 'Los Angeles International Airport', 2, 120, 50, 300, 600);
commit;
--select * from flights;
--select * from stopovers;