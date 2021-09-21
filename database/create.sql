-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.5.11-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for virtual_wallet
CREATE
DATABASE IF NOT EXISTS `virtual_wallet` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE
`virtual_wallet`;

-- Dumping structure for table virtual_wallet.cards
CREATE TABLE IF NOT EXISTS `cards`
(
    `card_id` int
(
    11
) NOT NULL AUTO_INCREMENT,
    `card_number` varchar
(
    16
) DEFAULT NULL,
    `expiration_date` date NOT NULL,
    `card_holder` varchar
(
    30
) NOT NULL,
    `cvv` varchar
(
    3
) NOT NULL DEFAULT '',
    `user_id` int
(
    11
) NOT NULL,
    `deleted` tinyint
(
    1
) DEFAULT 0,
    PRIMARY KEY
(
    `card_id`
),
    KEY `cards_users_fk`
(
    `user_id`
),
    CONSTRAINT `cards_payment_methods_fk` FOREIGN KEY
(
    `card_id`
) REFERENCES `payment_methods`
(
    `id`
),
    CONSTRAINT `cards_users_fk` FOREIGN KEY
(
    `user_id`
) REFERENCES `users`
(
    `user_id`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.categories
CREATE TABLE IF NOT EXISTS `categories`
(
    `category_id` int
(
    11
) NOT NULL AUTO_INCREMENT,
    `name` varchar
(
    16
) NOT NULL,
    `user_id` int
(
    11
) NOT NULL,
    PRIMARY KEY
(
    `category_id`
),
    KEY `Categories_users_id_fk`
(
    `user_id`
),
    CONSTRAINT `Categories_users_id_fk` FOREIGN KEY
(
    `user_id`
) REFERENCES `users`
(
    `user_id`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.category_transactions
CREATE TABLE IF NOT EXISTS `category_transactions`
(
    `transaction_id` int
(
    11
) DEFAULT NULL,
    `category_id` int
(
    11
) DEFAULT NULL,
    KEY `category_transactions_id_fk`
(
    `transaction_id`
),
    KEY `category_transactions__categories_fk`
(
    `category_id`
),
    CONSTRAINT `category_transactions__categories_fk` FOREIGN KEY
(
    `category_id`
) REFERENCES `categories`
(
    `category_id`
),
    CONSTRAINT `category_transactions_id_fk` FOREIGN KEY
(
    `transaction_id`
) REFERENCES `transactions`
(
    `transaction_id`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.confirmation_tokens
CREATE TABLE IF NOT EXISTS `confirmation_tokens`
(
    `token_id` bigint
(
    20
) NOT NULL AUTO_INCREMENT,
    `confirmation_token` varchar
(
    255
) NOT NULL,
    `created_date` timestamp NOT NULL DEFAULT current_timestamp
(
) ON UPDATE current_timestamp
(
),
    `user_id` int
(
    11
) NOT NULL,
    PRIMARY KEY
(
    `token_id`
),
    KEY `confirmation_tokens_users_fk`
(
    `user_id`
),
    CONSTRAINT `confirmation_tokens_users_fk` FOREIGN KEY
(
    `user_id`
) REFERENCES `users`
(
    `user_id`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.contact_list
CREATE TABLE IF NOT EXISTS `contact_list`
(
    `user_id` int
(
    11
) NOT NULL,
    `contact_id` int
(
    11
) NOT NULL,
    KEY `contact_list_user_id_fk`
(
    `contact_id`
),
    KEY `contact_list__users_fk`
(
    `user_id`
),
    CONSTRAINT `contact_list__users_fk` FOREIGN KEY
(
    `user_id`
) REFERENCES `users`
(
    `user_id`
),
    CONSTRAINT `contact_list_user_id_fk` FOREIGN KEY
(
    `contact_id`
) REFERENCES `users`
(
    `user_id`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.payment_methods
CREATE TABLE IF NOT EXISTS `payment_methods`
(
    `id` int
(
    11
) NOT NULL AUTO_INCREMENT,
    `type` enum
(
    'CARD',
    'WALLET'
) NOT NULL,
    PRIMARY KEY
(
    `id`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.roles
CREATE TABLE IF NOT EXISTS `roles`
(
    `role_id` int
(
    11
) NOT NULL AUTO_INCREMENT,
    `name` varchar
(
    50
) NOT NULL,
    PRIMARY KEY
(
    `role_id`
),
    UNIQUE KEY `roles_role_name_uindex`
(
    `name`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.transactions
CREATE TABLE IF NOT EXISTS `transactions`
(
    `transaction_id` int
(
    11
) NOT NULL AUTO_INCREMENT,
    `timestamp` timestamp NOT NULL DEFAULT current_timestamp
(
) ON UPDATE current_timestamp
(
),
    `sender_id` int
(
    11
) NOT NULL,
    `recipient_id` int
(
    11
) NOT NULL,
    `amount` decimal
(
    10,
    0
) NOT NULL,
    `sender_payment_method_id` int
(
    11
) NOT NULL,
    `recipient_payment_method_id` int
(
    11
) NOT NULL,
    `description` varchar
(
    50
) NOT NULL,
    `transaction_type` enum
(
    'CARD_TO_WALLET',
    'WALLET_TO_CARD',
    'SMALL_TRANSACTION',
    'LARGE_TRANSACTION'
) DEFAULT NULL,
    PRIMARY KEY
(
    `transaction_id`
),
    KEY `transactions_payment_method_fk`
(
    `sender_payment_method_id`
),
    KEY `transactions_recipient_payment_method_id_fk`
(
    `recipient_payment_method_id`
),
    KEY `transactions_users_recipient_fk`
(
    `recipient_id`
),
    KEY `transactions_users_sender_fk`
(
    `sender_id`
),
    CONSTRAINT `transactions_payment_method_fk` FOREIGN KEY
(
    `sender_payment_method_id`
) REFERENCES `payment_methods`
(
    `id`
),
    CONSTRAINT `transactions_recipient_payment_method_id_fk` FOREIGN KEY
(
    `recipient_payment_method_id`
) REFERENCES `payment_methods`
(
    `id`
),
    CONSTRAINT `transactions_users_recipient_fk` FOREIGN KEY
(
    `recipient_id`
) REFERENCES `users`
(
    `user_id`
),
    CONSTRAINT `transactions_users_sender_fk` FOREIGN KEY
(
    `sender_id`
) REFERENCES `users`
(
    `user_id`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.users
CREATE TABLE IF NOT EXISTS `users`
(
    `user_id` int
(
    11
) NOT NULL AUTO_INCREMENT,
    `username` varchar
(
    20
) NOT NULL,
    `first_name` varchar
(
    50
) NOT NULL,
    `last_name` varchar
(
    50
) NOT NULL,
    `phone_number` varchar
(
    10
) NOT NULL,
    `blocked` tinyint
(
    1
) NOT NULL DEFAULT 0,
    `email_verified` tinyint
(
    1
) NOT NULL DEFAULT 0,
    `id_verified` tinyint
(
    1
) NOT NULL DEFAULT 0,
    `user_photo` varchar
(
    500
) DEFAULT NULL,
    `default_wallet_id` int
(
    11
) DEFAULT NULL,
    `password` varchar
(
    30
) NOT NULL,
    `email` varchar
(
    50
) NOT NULL,
    PRIMARY KEY
(
    `user_id`
),
    UNIQUE KEY `users_email_uindex`
(
    `email`
),
    UNIQUE KEY `users_phone_number_uindex`
(
    `phone_number`
),
    UNIQUE KEY `users_username_uindex`
(
    `username`
),
    KEY `users_wallets_fk`
(
    `default_wallet_id`
),
    CONSTRAINT `users_wallets_fk` FOREIGN KEY
(
    `default_wallet_id`
) REFERENCES `wallets`
(
    `wallet_id`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.users_roles
CREATE TABLE IF NOT EXISTS `users_roles`
(
    `user_id` int
(
    11
) NOT NULL,
    `role_id` int
(
    11
) NOT NULL,
    KEY `users_roles_roles_fk`
(
    `role_id`
),
    KEY `users_roles_users_fk`
(
    `user_id`
),
    CONSTRAINT `users_roles_roles_fk` FOREIGN KEY
(
    `role_id`
) REFERENCES `roles`
(
    `role_id`
),
    CONSTRAINT `users_roles_users_fk` FOREIGN KEY
(
    `user_id`
) REFERENCES `users`
(
    `user_id`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table virtual_wallet.wallets
CREATE TABLE IF NOT EXISTS `wallets`
(
    `wallet_id` int
(
    11
) NOT NULL AUTO_INCREMENT,
    `name` varchar
(
    100
) NOT NULL,
    `balance` decimal
(
    10,
    0
) NOT NULL,
    `user_id` int
(
    11
) DEFAULT NULL,
    `deleted` tinyint
(
    1
) DEFAULT 0,
    PRIMARY KEY
(
    `wallet_id`
),
    KEY `wallets_users_fk`
(
    `user_id`
),
    CONSTRAINT `wallets_payment_methods_fk` FOREIGN KEY
(
    `wallet_id`
) REFERENCES `payment_methods`
(
    `id`
),
    CONSTRAINT `wallets_users_fk` FOREIGN KEY
(
    `user_id`
) REFERENCES `users`
(
    `user_id`
)
    ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
