create table public.users
(
    id          bigserial                           not null
        constraint users_pk
            primary key,
    email       varchar(100)                        not null unique,
    nickname    varchar(100)                        not null unique,
    password    varchar(100)                        not null,
    create_time timestamp default CURRENT_TIMESTAMP not null
);
