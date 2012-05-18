-- Run this script against your database that must be called CS247

--
-- Table structure for ip_whitelist
--

CREATE TABLE `ip_whitelist` (
    `ip_address` varchar(16) NOT NULL PRIMARY KEY COMMENT 'IP address of a trusted client'
); 
INSERT INTO `ip_whitelist` (`ip_address`) VALUES ("127.0.0.1");
--
-- Table structure for android_alerts
--

CREATE TABLE `android_alerts` (
    `alert_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique ID for this alert',
    `title` VARCHAR( 500 ) NOT NULL COMMENT 'The headline for this alert as it will appear on the phone',
    `link` VARCHAR( 500 ) NOT NULL COMMENT 'A link to the source of information for this alert. E.g. a BBC news article',
    `description` TEXT( 10000 ) NOT NULL COMMENT 'The main body of the alert. Details about the story.',
    `suggestions` TEXT( 10000 ) NOT NULL COMMENT 'Our suggestions on what to do because of this alert.',
    `importance` TINYINT NOT NULL COMMENT 'An indicator of the importance of this alert (optional)',
    `time_stamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The time this alert was added to the database'
);

CREATE TABLE `android_devices` (
    `registration_id` VARCHAR( 500 ) NOT NULL COMMENT 'The ID provided by android cloud to device messaging (C2DM)',
);