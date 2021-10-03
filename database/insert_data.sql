/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
INSERT INTO `cards` (`card_id`, `card_number`, `expiration_date`, `card_holder`, `cvv`, `user_id`, `deleted`) VALUES
	(3, '1234123412341234', '2024-12-31', 'KAMEN GEORGIEV', '123', 2, 0),
	(4, '5786847585968485', '2023-03-31', 'KRISTIYAN DIMITROV', '245', 1, 1),
	(7, '1234899945558777', '2024-12-31', 'JOHN DOE', '323', 3, 0),
	(10, '5786847585968485', '2024-08-31', 'KRISTIYAN DIMITROV', '234', 1, 0);
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;

/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` (`category_id`, `name`, `user_id`) VALUES
	(1, 'Food', 3),
	(2, 'Beer', 3),
	(3, 'Food Spendings', 1),
	(4, 'Car Spendings', 1);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;

/*!40000 ALTER TABLE `category_transactions` DISABLE KEYS */;
INSERT INTO `category_transactions` (`transaction_id`, `category_id`) VALUES
	(12, 4),
	(13, 4),
	(15, 3),
	(16, 3);
/*!40000 ALTER TABLE `category_transactions` ENABLE KEYS */;

/*!40000 ALTER TABLE `confirmation_tokens` DISABLE KEYS */;
INSERT INTO `confirmation_tokens` (`token_id`, `confirmation_token`, `created_date`, `user_id`) VALUES
	(1, '170cd81a-8380-4bc3-8bab-30fd1bb473dc', '2021-10-03 20:12:03', 1),
	(2, '03e036b8-f811-48f9-bd12-bb1fdd70cd65', '2021-10-03 20:12:52', 2),
	(3, '49220184-a343-4656-9cdc-f0fc616ebf30', '2021-10-03 20:23:16', 3),
	(4, '03a99b29-8095-4f27-8612-451e1f9ac50c', '2021-10-03 21:32:15', 4);
/*!40000 ALTER TABLE `confirmation_tokens` ENABLE KEYS */;

/*!40000 ALTER TABLE `contact_list` DISABLE KEYS */;
INSERT INTO `contact_list` (`user_id`, `contact_id`) VALUES
	(1, 2),
	(3, 1),
	(1, 3);
/*!40000 ALTER TABLE `contact_list` ENABLE KEYS */;

/*!40000 ALTER TABLE `invitation_tokens` DISABLE KEYS */;
INSERT INTO `invitation_tokens` (`token_id`, `invitation_token`, `expiration_date`, `inviting_user_id`, `invited_email`, `used`) VALUES
	(1, 'ffe1e5d1-eb3a-46af-b3a7-b5293511e9a5', '2021-10-04 21:30:28', 1, 'krintax@gmail.com', 1);
/*!40000 ALTER TABLE `invitation_tokens` ENABLE KEYS */;

/*!40000 ALTER TABLE `payment_methods` DISABLE KEYS */;
INSERT INTO `payment_methods` (`id`, `type`) VALUES
	(1, 'WALLET'),
	(2, 'WALLET'),
	(3, 'CARD'),
	(4, 'CARD'),
	(5, 'WALLET'),
	(6, 'WALLET'),
	(7, 'CARD'),
	(8, 'WALLET'),
	(9, 'CARD'),
	(10, 'CARD'),
	(11, 'WALLET');
/*!40000 ALTER TABLE `payment_methods` ENABLE KEYS */;

/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` (`role_id`, `name`) VALUES
	(1, 'Customer'),
	(2, 'Employee');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;

/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` (`transaction_id`, `timestamp`, `sender_id`, `recipient_id`, `amount`, `sender_payment_method_id`, `recipient_payment_method_id`, `description`, `transaction_type`) VALUES
	(1, '2021-10-03 20:18:35', 1, 1, 2500.00, 4, 1, 'First Deposit', 'CARD_TO_WALLET'),
	(2, '2021-10-03 20:19:20', 1, 1, 500.00, 1, 4, 'First Withdraw', 'WALLET_TO_CARD'),
	(3, '2021-10-03 20:19:52', 2, 2, 13123244232.00, 3, 2, 'First Deposit', 'CARD_TO_WALLET'),
	(4, '2021-10-03 20:21:43', 1, 2, 30.00, 1, 2, 'Monthly Scholarship', 'SMALL_TRANSACTION'),
	(5, '2021-10-03 20:23:22', 1, 1, 1000.00, 1, 5, 'Transfered to Savings', 'WALLET_TO_WALLET'),
	(6, '2021-10-03 20:25:32', 3, 3, 503202.00, 7, 6, 'First Deposit', 'CARD_TO_WALLET'),
	(7, '2021-10-03 20:26:39', 3, 1, 350.00, 6, 1, 'For Bootsrap theme', 'SMALL_TRANSACTION'),
	(8, '2021-10-03 20:27:45', 3, 2, 50321.00, 6, 2, 'Insurance', 'LARGE_UNVERIFIED'),
	(9, '2021-10-03 20:27:54', 3, 2, 5032.00, 6, 2, 'Insurance', 'SMALL_TRANSACTION'),
	(10, '2021-10-03 20:31:35', 3, 1, 4500.00, 6, 1, 'Telerik Academy Tax', 'SMALL_TRANSACTION'),
	(11, '2021-10-03 20:33:36', 3, 1, 410.70, 6, 1, 'Money for the beer', 'SMALL_TRANSACTION'),
	(12, '2021-10-03 20:36:09', 1, 2, 300.00, 1, 2, 'Oil Change', 'SMALL_TRANSACTION'),
	(13, '2021-10-03 20:36:37', 1, 2, 250.00, 1, 2, 'Brake Pads Change', 'SMALL_TRANSACTION'),
	(14, '2021-10-03 20:38:06', 1, 3, 1350.25, 1, 6, 'Cyber Security Testing', 'SMALL_TRANSACTION'),
	(15, '2021-10-03 20:38:35', 1, 3, 49.99, 1, 6, 'LIDL Shopping', 'SMALL_TRANSACTION'),
	(16, '2021-10-03 20:39:00', 1, 2, 23.47, 1, 2, 'Kaufland Shopping', 'SMALL_TRANSACTION'),
	(17, '2021-10-03 20:41:34', 1, 1, 4256.99, 1, 5, 'Balance Transfer', 'WALLET_TO_WALLET'),
	(18, '2021-10-03 20:53:27', 1, 1, 250.00, 5, 8, 'Wallet Balance Transfer', 'WALLET_TO_WALLET'),
	(19, '2021-10-03 20:53:41', 1, 2, 50.00, 8, 2, 'Test For Deletion', 'SMALL_TRANSACTION'),
	(20, '2021-10-03 21:07:50', 2, 1, 520.00, 2, 5, 'Bike parts', 'SMALL_TRANSACTION');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;

/*!40000 ALTER TABLE `transaction_verification_tokens` DISABLE KEYS */;
INSERT INTO `transaction_verification_tokens` (`token_id`, `verification_token`, `expiration_date`, `transaction_id`) VALUES
	(1, '0f35def8-738e-48b3-9983-133da898ad99', '2021-10-04 20:27:45', 8);
/*!40000 ALTER TABLE `transaction_verification_tokens` ENABLE KEYS */;

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`user_id`, `username`, `password`, `email`, `phone_number`, `first_name`, `last_name`, `blocked`, `email_verified`, `id_verified`, `user_photo`, `id_photo`, `selfie_photo`, `default_wallet_id`, `deleted`, `invited_users`) VALUES
	(1, 'kristiyanpd', '123456Aa+', 'kristiyanpd02@gmail.com', '0887329692', 'Kristiyan', 'Dimitrov', 0, 1, 0, NULL, NULL, NULL, 8, 0, 1),
	(2, 'kamen', 'Kamen123456*', 'georgiev.kameng@abv.bg', '0886454428', 'Kamen', 'Georgiev', 0, 1, 1, NULL, NULL, NULL, 2, 0, 0),
	(3, 'John', 'Kamen123456*', 'johndoe@gmail.com', '0888885575', 'John', 'Doe', 0, 1, 1, NULL, NULL, NULL, 6, 0, 0),
	(4, 'krintax', '123456Aa+', 'krintax@gmail.com', '0887986785', 'Peter', 'Stoyanov', 0, 1, 1, NULL, '00100lrPORTRAIT_00100_BURST20200702195854536_COVER.jpg', '92017593_531761261077161_3249743732722892800_o.jpg', 11, 0, 0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40000 ALTER TABLE `users_roles` DISABLE KEYS */;
INSERT INTO `users_roles` (`user_id`, `role_id`) VALUES
	(1, 1),
	(2, 1),
	(3, 1),
	(1, 2),
	(4, 1);
/*!40000 ALTER TABLE `users_roles` ENABLE KEYS */;

/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
INSERT INTO `wallets` (`wallet_id`, `name`, `balance`, `user_id`, `deleted`) VALUES
	(1, 'Default Wallet', 0.00, 1, 1),
	(2, 'Default Wallet', 13123249397.47, 2, 0),
	(5, 'Savings for New House', 5526.99, 1, 0),
	(6, 'Default Wallet', 494309.54, 3, 0),
	(8, 'Default Wallet', 220.00, 1, 0),
	(11, 'Default Wallet', 20.00, 4, 0);
/*!40000 ALTER TABLE `wallets` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
