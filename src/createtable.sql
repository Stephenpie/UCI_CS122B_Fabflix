create table movies(
	id varchar(10) primary key not null,
	title varchar(100) not null, 
	year integer not null,
	director varchar(100) not null 
);

create table stars(
	id varchar(10) primary key,
	name varchar(100) not null,
	birthYear integer
);

create table stars_in_movies(
	starId varchar(10) not null,
	movieId varchar(10) not null,
	FOREIGN KEY (starId) REFERENCES stars(id),
	FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table genres(
	id integer primary key not null AUTO_INCREMENT,
	name varchar(32) not null 
);

create table genres_in_movies(
	genreId integer not null,
	movieId varchar(10) not null,
	FOREIGN KEY (genreId) REFERENCES genres(id),
	FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table creditcards(
	id varchar(20) primary key not null,
	firstName varchar(50) not null,
	lastName varchar(50) not null,
	expiration date not null
);

create table ratings(
	movieId varchar(10) not null,
	rating float not null,
	numVotes integer not null
);

create table customers(
	id integer primary key not null AUTO_INCREMENT,
	firstName varchar(50) not null,
	lastName varchar(50) not null,
	ccId varchar(20) not null,
	address varchar(200) not null,
	email varchar(50) not null,
	password varchar(20) not null,
	FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

create table sales(
	id integer primary key not null AUTO_INCREMENT,
	customerId integer not null,
	movieId varchar(10) not null,
	saleDate date,
	FOREIGN KEY (customerId) REFERENCES customers(id),
	FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table employees(
	email varchar(50) primary key not null,
	password varchar(20) not null,
	fullname varchar(100)
);

create table ft (
	id INT AUTO_INCREMENT,
	movieId VARCHAR(10),
	title text,
	year INT,
	director VARCHAR(100),
	PRIMARY KEY (id),
	FULLTEXT (title)
);
