CREATE TABLE public.battle_history
(
    id             BIGSERIAL NOT NULL
        CONSTRAINT battle_history_pk
            PRIMARY KEY,
    begin_time     TIMESTAMP WITHOUT TIME ZONE,
    battle_type_id SMALLINT
);

CREATE TABLE public.user_battle_history
(
    battle_history_id      BIGINT NOT NULL
        CONSTRAINT user_battle_history_battle_history_fk
            REFERENCES "battle_history"
            ON UPDATE CASCADE ON DELETE CASCADE,
    user_id                BIGINT NOT NULL
        CONSTRAINT user_battle_history_users_fk
            REFERENCES "users"
            ON UPDATE CASCADE ON DELETE CASCADE,
    vehicle_name           VARCHAR(100),
    caused_damage          FLOAT,
    made_shots             INTEGER,
    caused_direct_hits     INTEGER,
    caused_indirect_hits   INTEGER,
    caused_track_breaks    INTEGER,
    destroyed_vehicles     INTEGER,
    received_damage        FLOAT,
    received_direct_hits   INTEGER,
    received_indirect_hits INTEGER,
    received_track_breaks  INTEGER,
    survived               BOOLEAN
);
