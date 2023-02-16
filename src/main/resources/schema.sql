CREATE TABLE `user`
(
    `id`         BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `email`      VARCHAR(30) NOT NULL,
    `name`       VARCHAR(10) NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT NOW(),
    `deleted_at` DATETIME NULL
);

CREATE UNIQUE INDEX UQ_USER_EMAIL ON `user`(`email`);
