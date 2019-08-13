# --- !Ups


create table users (
  id       uuid primary key not null,

  email    varchar(255) not null,
  password varchar(255) not null
);

create table roles (

  user_id uuid references users (id),

  name varchar(255) not null
);

create table customers (

  id uuid primary key not null ,

  name varchar(255) not null
);

create table contacts (

  id uuid primary key not null,

  name varchar(255) not null,

  customer_id uuid null references customers(id)
);


# --- !Downs

drop table users;
drop table roles;
drop table customers;
drop table contacts;