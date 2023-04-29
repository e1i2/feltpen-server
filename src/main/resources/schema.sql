create table folder
(
    id               bigint       not null auto_increment primary key,
    name             varchar(100) not null,
    created_at       datetime     not null,
    updated_at       datetime     not null,
    deleted_at       datetime,
    parent_folder_id bigint,
    writer_id        bigint       not null
);

create index created_at_index on folder (created_at);
create index parent_folder_id_index on folder (parent_folder_id);

create table post
(
    id         bigint       not null primary key auto_increment,
    created_at datetime     not null,
    folder_id  bigint       not null,
    status     varchar(100) not null
);

CREATE INDEX FOLDER_FK ON folder_post (folder_id);

create table post_data
(
    id             bigint       not null auto_increment
        primary key,
    folder_post_id bigint       not null,
    title          varchar(100) not null,
    content        json         not null,
    created_at     datetime     not null,
    deleted_at     datetime null
);

CREATE INDEX USER_FK ON post_data (folder_post_id);

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
    name VARCHAR(50) NOT NULL,
    UNIQUE (user_id, workspace_id)
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