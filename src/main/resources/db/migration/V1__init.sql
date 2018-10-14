CREATE SEQUENCE hibernate_sequence
  START 1;

CREATE TABLE request (
  id BIGINT PRIMARY KEY,
  state VARCHAR(128),
  request_type VARCHAR(128)
);

CREATE TABLE sell_request (
  id BIGINT PRIMARY KEY,
  sell_amount BIGINT check (sell_amount > 0)
);

CREATE TABLE buy_request (
  id BIGINT PRIMARY KEY,
  buy_amount BIGINT check (buy_amount > 0),
  stock_name VARCHAR(128) NOT NULL
);