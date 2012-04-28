-- Run this script against your database that must be called CS247

--
-- Table structure for ip_whitelist
--

CREATE TABLE ip_whitelist (
  ip_address varchar(16) NOT NULL COMMENT 'IP address of a trusted client',
  PRIMARY KEY (ip_address)
) 
