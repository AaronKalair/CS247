-- Run this script against your database that must be called CS247

--
-- Table structure for ip_whitelist
--

CREATE TABLE ip_whitelist (
  id int(11) NOT NULL AUTO_INCREMENT,
  ip_address varchar(16) NOT NULL COMMENT 'IP address of a trusted client',
  PRIMARY KEY (id)
) 
