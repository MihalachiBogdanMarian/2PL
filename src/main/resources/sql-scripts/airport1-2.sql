drop materialized view flight_details;
create materialized view flight_details as select id, first_class_price, second_class_price, stopovers from airport2_flights;
/
begin
    dbms_snapshot.refresh('flight_details', 'f');
end;
/
select * from flight_details;

/* TICKETS */
create table tickets (
  	code number(10, 0) primary key,
	price number(10, 0) not null,
	class number(1) not null,
  	passenger_id number(10, 0) not null,
  	flight_id number (10, 0) not null,
    stopover number (10, 0) not null,
  	foreign key (passenger_id) references users(id) on delete cascade validate,
  	foreign key (flight_id) references flight_details(id) on delete cascade validate,
--    constraint ids_pair_unique unique(passenger_id, flight_id),
    constraint check_class check (class = 1 or class = 2)
) tablespace airport;

declare
    V_MAX_NR_TICKETS_PER_PASSENGER integer := 3;
    
    v_code number(10, 0);
    v_price number(10, 0);
    v_class number(1);
    v_passenger_id number(10, 0);
    v_flight_id flight_details.id%type;
    v_stopover number(10, 0);

    v_counter integer := 1;
    
    v_nr_flights number(10);
    v_random_flight_index number(10);
    v_first_class_price number(10);
    v_second_class_price number(10);
    v_first_or_second_class number(10) := trunc(sys.dbms_random.value(1, 3));
    v_nr_tickets number(10);
    v_stopovers number(10, 0);
    
    cursor passengers_cursor is select id from users where type = 'passenger';
    x passengers_cursor%rowtype;
begin
    select count(*) into v_nr_flights from flight_details;
    
    for x in passengers_cursor loop
        v_nr_tickets := trunc(sys.dbms_random.value(1, V_MAX_NR_TICKETS_PER_PASSENGER + 1));
        
        for i in 0..v_nr_tickets - 1 loop
            /* FLIGHT ID */
            v_random_flight_index := trunc(sys.dbms_random.value(1, v_nr_flights));
            select id, first_class_price, second_class_price, stopovers into v_flight_id, v_first_class_price, v_second_class_price, v_stopovers 
                from (select * from (select * from (select * from flight_details order by id) where rownum <= v_random_flight_index) order by id desc) where rownum = 1;
            
            /* CODE */
            v_code := v_counter;
            v_counter := v_counter + 1;
            
            /* PRICE */
            v_first_or_second_class := trunc(sys.dbms_random.value(1, 3));
            if (v_first_or_second_class = 1) then
                v_price := v_first_class_price;
            else
                v_price := v_second_class_price;
            end if;
            
            /* CLASS */
            v_class := v_first_or_second_class;
            
            /* PASSENGER ID */
            v_passenger_id := x.id;
            
            /* STOPOVER */
            if (v_stopovers = 0) then
                v_stopover := 0;
            else
                v_stopover := trunc(sys.dbms_random.value(0, v_stopovers + 1));
            end if;
            
            insert into tickets values(v_code, v_price, v_class, v_passenger_id, v_flight_id, v_stopover);
        end loop;
    end loop;
end;
/

commit;
--select * from tickets;


/* FLIGHTS_STAFF */
create table flights_staff(
    flight_id number(10, 0),
    user_id number(10, 0),
    user_type varchar(50) not null,
    constraint check_type_2 check (user_type in ('pilot', 'fa')),
    constraint ids_pair_unique_2 unique(flight_id, user_id),
    foreign key (flight_id) references flight_details(id),
    foreign key (user_id) references users(id)
) tablespace airport;

declare
    V_MIN_NR_PILOTS_PER_FLIGHT integer := 1;
    V_MAX_NR_PILOTS_PER_FLIGHT integer := 2;
    V_MIN_NR_FAS_PER_FLIGHT integer := 3;
    V_MAX_NR_FAS_PER_FLIGHT integer := 5;
    
    v_i integer;

    v_nr_pilots number(10); -- the total number of pilots which are working at the monitorized airport
    v_nr_pilots_for_flight number(10); -- the number of pilots which will fly the airplane for a certain flight
    v_random_pilot_index number(10); -- random position in the users table for users which are pilots
    v_pilot_id number(10);
    
    v_nr_fas number(10); -- the total number of flight attendants which are working at the monitorized airport
    v_nr_fas_for_flight number(10); -- the number of pilots which will assist in the airplane for a certain flight
    v_random_fa_index number(10); -- random position in the users table for users which are flight attendants
    v_fa_id number(10);

    cursor flights_cursor is select id from flight_details;
    x flights_cursor%rowtype;
begin
    select count(*) into v_nr_pilots from users where type = 'pilot';
    select count(*) into v_nr_fas from users where type = 'fa';
    for x in flights_cursor loop

        -- pilots
        v_nr_pilots_for_flight := trunc(sys.dbms_random.value(V_MIN_NR_PILOTS_PER_FLIGHT, V_MAX_NR_PILOTS_PER_FLIGHT + 1));
        
        for v_i in (select * from (select * from users where type = 'pilot' order by dbms_random.random) where rownum < v_nr_pilots_for_flight + 1) loop
            insert into flights_staff values(x.id, v_i.id, 'pilot');
        end loop;
        
        -- flight attendants
        v_nr_fas_for_flight := trunc(sys.dbms_random.value(V_MIN_NR_FAS_PER_FLIGHT, V_MAX_NR_FAS_PER_FLIGHT + 1));
        
        for v_i in (select * from (select * from users where type = 'fa' order by dbms_random.random) where rownum < v_nr_fas_for_flight + 1) loop
            insert into flights_staff values(x.id, v_i.id, 'fa');
        end loop;
        
    end loop;
end;
/

commit;
--select * from flights_staff;



/* FLIGHTS DEPOSIT */
create table flights_deposit (
  	flight_id number(10, 0) primary key,
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
 	reason varchar(100),
  	rescheduled date
) tablespace airport;