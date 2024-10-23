create table public.user_settings
(
    id         bigserial    not null
        constraint user_settings_pk
            primary key,
    user_id    bigint       not null
        constraint user_settings_user_id_fk
            references users
            on update cascade on delete cascade,
    group_name varchar(100) not null,
    name       varchar(100) not null,
    value      varchar(100) not null
);
