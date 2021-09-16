create table payment_methods
(
    id   int auto_increment
        primary key,
    type enum ('CARD', 'WALLET') not null
);

create table roles
(
    role_id int auto_increment
        primary key,
    name    varchar(50) not null,
    constraint roles_role_name_uindex
        unique (name)
);

create table users
(
    user_id           int auto_increment
        primary key,
    username          varchar(20)          not null,
    password          varchar(30)          not null,
    email             varchar(50)          not null,
    phone_number      varchar(10)          not null,
    blocked           tinyint(1) default 0 not null,
    email_verified    tinyint(1) default 0 not null,
    id_verified       tinyint(1) default 0 not null,
    user_photo        varchar(500)         null,
    default_wallet_id int                  null,
    constraint users_email_uindex
        unique (email),
    constraint users_phone_number_uindex
        unique (phone_number),
    constraint users_username_uindex
        unique (username)
);

create table cards
(
    card_id         int auto_increment
        primary key,
    card_number     varchar(16) default '' not null,
    expiration_date date                   not null,
    card_holder     varchar(30)            not null,
    cvv             varchar(3)  default '' not null,
    user_id         int                    not null,
    constraint cards_number_uindex
        unique (card_number),
    constraint cards_payment_methods_fk
        foreign key (card_id) references payment_methods (id),
    constraint cards_users_fk
        foreign key (user_id) references users (user_id)
);

create table confirmation_tokens
(
    token_id           bigint auto_increment
        primary key,
    confirmation_token varchar(255)                          not null,
    created_date       timestamp default current_timestamp() not null on update current_timestamp(),
    user_id            int                                   not null,
    constraint confirmation_tokens_users_fk
        foreign key (user_id) references users (user_id)
);

create table transactions
(
    transaction_id              int auto_increment
        primary key,
    timestamp                   timestamp default current_timestamp() not null on update current_timestamp(),
    sender_id                   int                                   not null,
    recipient_id                int                                   not null,
    amount                      decimal                               not null,
    sender_payment_method_id    int                                   not null,
    recipient_payment_method_id int                                   not null,
    constraint transactions_payment_method_fk
        foreign key (sender_payment_method_id) references payment_methods (id),
    constraint transactions_recipient_payment_method_id_fk
        foreign key (recipient_payment_method_id) references payment_methods (id),
    constraint transactions_users_recipient_fk
        foreign key (recipient_id) references users (user_id),
    constraint transactions_users_sender_fk
        foreign key (sender_id) references users (user_id)
);

create table users_roles
(
    user_id int not null,
    role_id int not null,
    constraint users_roles_roles_fk
        foreign key (role_id) references roles (role_id),
    constraint users_roles_users_fk
        foreign key (user_id) references users (user_id)
);

create table wallets
(
    wallet_id int auto_increment
        primary key,
    name      varchar(100) not null,
    balance   decimal      not null,
    user_id   int          null,
    constraint wallets_payment_methods_fk
        foreign key (wallet_id) references payment_methods (id),
    constraint wallets_users_fk
        foreign key (user_id) references users (user_id)
);

create table categories
(
    category_id int auto_increment
        primary key,
    name        varchar(16) not null,
    user_id     int         not null,
    constraint categories_users_id_fk
        foreign key (user_id) references users (user_id)
);

create table category_transactions
(
    transaction_id int null,
    category_id    int null,
    constraint category_transactions_categories_fk
        foreign key (category_id) references categories (category_id),
    constraint category_transactions_id_fk
        foreign key (transaction_id) references transactions (transaction_id)
);

alter table users
    add constraint users_wallets_fk
        foreign key (default_wallet_id) references wallets (wallet_id);

