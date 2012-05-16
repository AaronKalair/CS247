-- Run this script with the following command:
-- sqlite3 bin/cs247.db < scripts/database.sql
--
-- Table structure for ip_whitelist
--

CREATE TABLE `ip_whitelist` (
	`ip_id` INTEGER PRIMARY KEY,
    `ip_address` varchar(16) NOT NULL
); 

--
-- Table structure for android_alerts
--

CREATE TABLE `android_alerts` (
    `alert_id` INTEGER PRIMARY KEY,
    `title` VARCHAR( 500 ) NOT NULL,
    `link` VARCHAR( 500 ) NOT NULL,
    `description` TEXT( 10000 ) NOT NULL,
    `suggestions` TEXT( 10000 ) NOT NULL,
    `importance` TINYINT NOT NULL,
    `time_stamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
