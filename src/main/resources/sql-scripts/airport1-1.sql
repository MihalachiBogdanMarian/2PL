/* DATABASE MANAGING AN AIRPORT - PART 1 */
set serveroutput on;

create database link dblink
    connect to c##airport2 identified by c##airport2
    using 
        '(DESCRIPTION =
            (ADDRESS = (PROTOCOL = TCP)(HOST = 127.0.0.1)(PORT = 1521))
            (CONNECT_DATA =
            (SERVER = DEDICATED)
            (SERVICE_NAME = XE)
            )
        )';

drop synonym airport2_flights;
create synonym airport2_flights for c##airport2.flights@dblink;

drop materialized view log on users;
drop table users cascade constraints;
drop table tickets cascade constraints;
drop table flights_staff cascade constraints;
drop table flights_cache cascade constraints;



/* USERS */
create table users (
  	id number(10, 0) primary key,
  	first_name varchar(50) not null,
  	last_name varchar(50) not null,
	birthday date not null,
  	address varchar(100),
  	phone_number varchar(10) unique,
  	email varchar(100) not null unique,
  	password varchar(20) not null,
  	type varchar(50) not null,
	logged number(1) default(0),
  	constraint check_type check (type in ('pilot', 'fa', 'user', 'passenger', 'admin'))
    deferrable initially immediate,
	constraint check_logged check (logged = 0 or logged = 1)
    deferrable initially immediate
) tablespace airport;

create materialized view log on users pctfree 5 tablespace airport storage (initial 10K next 10K); 

declare
    V_NR_PILOTS integer := 3;
    V_NR_FAS integer := 7;
    V_NR_ADMINS integer := 2;
    V_NR_USERS_PASSENGERS integer := 50;
    
    v_id number(10, 0);
    v_first_name varchar(50);
    v_last_name varchar(50);
    v_birthday date;
    v_address varchar(100);
    v_phone_number varchar(10);
    v_email varchar(100);
    v_password varchar(10);
    v_type varchar(50) := '';
    v_logged number(1);
    
    v_counter integer := 1;
    
    type t_names is table of varchar(50);
    v_first_names t_names := t_names('Wade', 'Dave', 'Seth', 'Ivan', 'Riley', 'Gilbert', 'Jorge', 'Dan', 'Brian', 'Roberto', 'Ramon', 'Miles', 'Liam', 
            'Nathaniel', 'Ethan', 'Lewis', 'Milton', 'Claude', 'Joshua', 'Glen', 'Harvey', 'Blake', 'Antonio', 'Connor', 'Julian', 'Daisy', 'Deborah', 
            'Isabel', 'Stella', 'Debra', 'Beverly', 'Vera', 'Angela', 'Lucy', 'Lauren', 'Janet', 'Loretta', 'Tracey', 'Beatrice', 'Sabrina', 'Melody', 
            'Chrysta', 'Christina', 'Vicki', 'Molly', 'Alison', 'Miranda', 'Stephanie', 'Leona');
    v_last_names t_names := t_names('Williams', 'Harris', 'Thomas', 'Robinson', 'Walker', 'Scott', 'Nelson', 'Mitchell', 'Morgan', 'Cooper', 'Howard', 
            'Davis', 'Miller', 'Martin', 'Smith', 'Anderson', 'White', 'Perry', 'Clark', 'Richards', 'Wheeler', 'Warburton', 'Stanley', 'Holland', 
            'Terry', 'Shelton', 'Miles', 'Lucas', 'Fletcher', 'Parks', 'Norris', 'Guzman', 'Daniel', 'Newton', 'Potter', 'Francis', 'Erickson', 'Norman', 
            'Moody', 'Lindsey', 'Gross', 'Sherman', 'Simon', 'Jones', 'Brown', 'Garcia', 'Rodriguez', 'Lee', 'Young', 'Hall', 'Iliott', 'Zabawa');
    
    v_random_day number(10);
    v_random_month number(10);
    v_nr_cities number(10);
    v_random_city_index number(10);
    v_random_name_index number(10);
    v_nr_first_names number(10);
    v_nr_last_names number(10);
    v_user_nr_first_names number(10);
    v_user_or_passanger number(10) := trunc(sys.dbms_random.value(1, 4));
begin
    v_nr_first_names := v_first_names.count;
    v_nr_last_names := v_last_names.count;
    select count(*) into v_nr_cities from city_names;
    
    for v_counter in 1..V_NR_PILOTS + V_NR_FAS + V_NR_ADMINS + V_NR_USERS_PASSENGERS loop
        /* ID */
        v_id := v_counter;
    
        /* FIRST NAME */
        v_random_name_index := trunc(sys.dbms_random.value(1, v_nr_first_names + 1));
        v_first_name := v_first_names(v_random_name_index);
        v_user_nr_first_names := trunc(sys.dbms_random.value(1, 3));
        if (v_user_nr_first_names = 2) then
            v_random_name_index := trunc(sys.dbms_random.value(1, v_nr_first_names + 1));
            v_first_name := v_first_name || '-' || v_first_names(v_random_name_index);
        end if;
        
        /* LAST NAME */
        v_random_name_index := trunc(sys.dbms_random.value(1, v_nr_last_names + 1));
        v_last_name := v_last_names(v_random_name_index);
        
        /* BIRTHDAY */
        v_random_month := trunc(sys.dbms_random.value(1, 13));
        if (v_random_month = 1 or v_random_month = 3 or v_random_month = 5 or v_random_month = 7 or v_random_month = 8 or v_random_month = 10 or v_random_month = 12) then
            v_random_day := trunc(sys.dbms_random.value(1, 32));
        end if;
        if (v_random_month = 4 or v_random_month = 6 or v_random_month = 9 or v_random_month = 11) then
            v_random_day := trunc(sys.dbms_random.value(1, 31));
        end if;
        if (v_random_month = 2) then
            v_random_day := trunc(sys.dbms_random.value(1, 29));
        end if;
        v_birthday := to_date('' || v_random_day || '/' || v_random_month || '/' || trunc(sys.dbms_random.value(1970, 2010)) || '', 'dd/mm/yyyy');
        
        /* ADDRESS */
        v_random_city_index := trunc(sys.dbms_random.value(1, v_nr_cities));
        select name into v_address from (select * from (select * from (select * from city_names order by id) where rownum <= v_random_city_index) order by id desc) where rownum = 1;
        
        /* PHONE NUMBER */
        v_phone_number := '07' || to_char(trunc(sys.dbms_random.value(0, 10))) || to_char(trunc(sys.dbms_random.value(0, 10))) || to_char(trunc(sys.dbms_random.value(0, 10)))
            || to_char(trunc(sys.dbms_random.value(0, 10))) || to_char(trunc(sys.dbms_random.value(0, 10))) || to_char(trunc(sys.dbms_random.value(0, 10)))
            || to_char(trunc(sys.dbms_random.value(0, 10))) || to_char(trunc(sys.dbms_random.value(0, 10)));
            
        /* TYPE */
        if (v_counter >= 1 and v_counter <= V_NR_PILOTS) then
            v_type := 'pilot';
        end if;
        if (v_counter >= V_NR_PILOTS + 1 and v_counter <= V_NR_PILOTS + V_NR_FAS) then
            v_type := 'fa';
        end if;
        if (v_counter >= V_NR_PILOTS + V_NR_FAS + 1 and v_counter <= V_NR_PILOTS + V_NR_FAS + V_NR_ADMINS) then
            v_type := 'admin';
        end if;
        if (v_counter >= V_NR_PILOTS + V_NR_FAS + V_NR_ADMINS + 1 and v_counter <= V_NR_PILOTS + V_NR_FAS + V_NR_ADMINS + V_NR_USERS_PASSENGERS) then
            v_user_or_passanger := trunc(sys.dbms_random.value(1, 4));
            if (v_user_or_passanger < 3) then
                v_type := 'user';
            else 
                v_type := 'passenger';
            end if;
--            v_type := 'user';
        end if;
        
        /* EMAIL */
        if (v_type = 'pilot') then
            v_email := lower(v_first_name) || v_counter || '@pilot.com';
        elsif (v_type = 'fa') then
            v_email := lower(v_first_name) || v_counter || '@fa.com';
        elsif (v_type = 'admin') then
            v_email := lower(v_first_name) || v_counter || '@admin.com';
        else
            v_email := lower(v_first_name) || v_counter || '@user.com';
        end if;
        
        /* PASSWORD */
        v_password := dbms_random.string('x', 10);
        
        /* LOGGED */
        v_logged := 0;
        
        insert into users values (v_id, v_first_name, v_last_name, v_birthday, v_address, v_phone_number, v_email, v_password, v_type, v_logged);
    end loop;
end;
/

commit;
select * from users;