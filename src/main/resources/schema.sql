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
    writer_id    bigint       not null,
    status VARCHAR(100) NOT NULL
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
    deleted_at datetime null,
    profile_image VARCHAR(512)
);

CREATE UNIQUE INDEX EMAIL_UQ ON user (email);

create table workspace
(
    id         bigint auto_increment
        primary key,
    name       varchar(15) not null,
    created_at datetime    not null,
    profile_image VARCHAR(512),
    deleted_at datetime null
);

create table workspace_member
(
    id bigint auto_increment primary key,
    user_id      bigint   not null,
    workspace_id bigint   not null,
    created_at   datetime not null,
    deleted_at   datetime null,
    role VARCHAR(255) NOT NULL,
    profile_image VARCHAR(512),
    name VARCHAR(50) NOT NULL
);

CREATE INDEX MEMBER_WORKSPACE_FK ON workspace_member (workspace_id);
CREATE INDEX MEMBER_USER_FK ON workspace_member (user_id);

create table workspace_invitation
(
    id           bigint primary key auto_increment,
    email        VARCHAR(30) not null,
    workspace_id bigint      not null,
    expire_at    datetime    not null,
    code         VARCHAR(20) not null,
    role VARCHAR(255) NOT NULL,
    UNIQUE (email, workspace_id)
);

CREATE INDEX INVITATION_WORKSPACE_FK ON workspace_invitation (workspace_id);

create table verification_code
(
    id         bigint auto_increment
        primary key,
    code       VARCHAR(15) NOT NULL,
    email      VARCHAR(30) NOT NULL,
    expire_at  DATETIME    NOT NULL,
    created_at datetime    not null DEFAULT NOW(),
    is_used    BIT         NOT NULL DEFAULT FALSE
);

CREATE INDEX EMAIL_INDEX ON verification_code (email);
CREATE INDEX CODE_INDEX ON verification_code (code);