create table post
(
    id           bigint       not null auto_increment
        primary key,
    title        varchar(100) not null,
    content      json         not null,
    created_at   datetime     not null,
    updated_at   datetime     not null,
    deleted_at   datetime null,
    workspace_id bigint       not null,
    writer_id    bigint       not null
);

CREATE INDEX USER_FK ON post (writer_id);
CREATE INDEX WORKSPACE_FK ON post (workspace_id);

create table user
(
    id         bigint auto_increment
        primary key,
    email      varchar(30) not null,
    name       varchar(30) not null,
    created_at datetime    not null,
    deleted_at datetime null
);

CREATE UNIQUE INDEX EMAIL_UQ ON user (email);

create table workspace
(
    id         bigint auto_increment
        primary key,
    name       varchar(15) not null,
    created_at datetime    not null,
    deleted_at datetime null
);

create table workspace_member
(
    user_id      bigint   not null,
    workspace_id bigint   not null,
    created_at   datetime not null,
    deleted_at   datetime null,
    primary key (user_id, workspace_id)
);

create table verification_code
(
    id         bigint auto_increment
        primary key,
    code       VARCHAR(15) NOT NULL,
    email      VARCHAR(30) NOT NULL,
    expire_at  DATETIME    NOT NULL,
    created_at datetime    not null DEFAULT NOW()
);

CREATE INDEX EMAIL_INDEX ON verification_code (email);
CREATE INDEX CODE_INDEX ON verification_code (code);