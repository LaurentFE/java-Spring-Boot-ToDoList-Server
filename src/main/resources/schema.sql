DROP TABLE IF EXISTS `list_names`;
DROP TABLE IF EXISTS `list_items`;
DROP TABLE IF EXISTS `items`;
DROP TABLE IF EXISTS `lists`;
DROP TABLE IF EXISTS `users`;

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(45) NOT NULL DEFAULT 'user name',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name_UNIQUE` (`user_name`)
);

--
-- Table structure for table `lists`
--

CREATE TABLE `lists` (
  `list_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  PRIMARY KEY (`list_id`),
  KEY `fk_lists_user_id_idx` (`user_id`),
  CONSTRAINT `fk_lists_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `label` varchar(45) NOT NULL DEFAULT 'to do list item',
  `is_checked` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
);

--
-- Table structure for table `list_items`
--

CREATE TABLE `list_items` (
  `list_id` int NOT NULL,
  `item_id` int NOT NULL,
  PRIMARY KEY (`list_id`,`item_id`),
  KEY `fk_list_items_item_id_idx` (`item_id`),
  CONSTRAINT `fk_list_items_item_id` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`),
  CONSTRAINT `fk_list_items_list_id` FOREIGN KEY (`list_id`) REFERENCES `lists` (`list_id`)
);

--
-- Table structure for table `list_names`
--

CREATE TABLE `list_names` (
  `list_id` int NOT NULL AUTO_INCREMENT,
  `label` varchar(45) NOT NULL DEFAULT 'to do list name',
  PRIMARY KEY (`list_id`),
  CONSTRAINT `fk_list_names_list_id` FOREIGN KEY (`list_id`) REFERENCES `lists` (`list_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);
