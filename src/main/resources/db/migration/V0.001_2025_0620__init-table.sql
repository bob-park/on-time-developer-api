-- users_authors
create table users_authors
(
    id                 bigserial               not null primary key,
    user_unique_id     bigint                  not null,
    author             varchar(100)            not null unique,
    created_date       timestamp default now() not null,
    created_by         varchar(100)            not null,
    last_modified_date timestamp,
    last_modified_by   varchar(100)
);

-- commits
create table commits
(
    id                 bigserial               not null primary key,
    commit_id          varchar(500)            not null,
    repo               varchar(100)            not null,
    branch             varchar(100)            not null,
    author             varchar(100)            not null,
    user_unique_id     bigint                  not null,
    commit_message     text                    not null,
    commit_date        timestamp               not null,
    created_date       timestamp default now() not null,
    created_by         varchar(100)            not null,
    last_modified_date timestamp,
    last_modified_by   varchar(100)
);
