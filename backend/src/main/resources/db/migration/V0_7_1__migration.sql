create table public.user_vehicle_configs
(
    id           bigserial    not null
        constraint user_vehicle_config_pk
            primary key,
    user_id      bigint       not null
        constraint user_vehicle_config_user_id_fk
            references users
            on update cascade on delete cascade,
    vehicle_name varchar(100) not null,
    name         varchar(100) not null,
    value        varchar(100) not null,
    amount       integer
);
