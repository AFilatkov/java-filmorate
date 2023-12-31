DROP TABLE IF EXISTS film_genre, film_likes, rating, genre, friends, films, users;
CREATE TABLE IF NOT EXISTS rating (
	rating_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(10),
	CONSTRAINT check_name CHECK (name IN ('G', 'PG', 'PG-13', 'R', 'NC-17'))
);

CREATE TABLE IF NOT EXISTS genre (
	genre_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(50) NOT NULL
);

INSERT INTO genre(name)
    VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');
INSERT INTO rating(name)
    VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

CREATE TABLE IF NOT EXISTS films (
	film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(50) NOT NULL,
	description varchar(200),
	release_date date,
	duration integer,
	rating_id varchar(10) REFERENCES rating(rating_id),
	CONSTRAINT check_release_date CHECK (release_date > '1895-12-28'),
	CONSTRAINT check_duration CHECK (duration > 0)
);

CREATE TABLE IF NOT EXISTS film_genre (
	film_id integer REFERENCES films(film_id) ON DELETE CASCADE,
	genre_id integer REFERENCES genre(genre_id) ON DELETE CASCADE
);
ALTER TABLE film_genre ADD CONSTRAINT uq_film_genre UNIQUE(film_id, genre_id);

CREATE TABLE IF NOT EXISTS users (
	user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(100) NOT NULL,
	login varchar(100) NOT NULL,
	email varchar(100) NOT NULL,
	birthday date
);

CREATE TABLE IF NOT EXISTS film_likes (
	film_id integer REFERENCES films(film_id),
	user_id integer REFERENCES users(user_id)
);
ALTER TABLE film_likes ADD CONSTRAINT uq_film_likes UNIQUE(film_id, user_id);

CREATE TABLE IF NOT EXISTS friends (
	user_id integer REFERENCES users(user_id) ON DELETE CASCADE,
	friend_id integer REFERENCES users(user_id) ON DELETE CASCADE,
	status bit
);
ALTER TABLE friends ADD CONSTRAINT uq_friends UNIQUE(user_id, friend_id);