CREATE TABLE IF NOT EXISTS books (id SERIAL PRIMARY KEY, title TEXT, author TEXT, year INTEGER);

CREATE OR REPLACE FUNCTION find_by_year(year_value INTEGER)
RETURNS TABLE(id INTEGER, title TEXT, author TEXT, year INTEGER) LANGUAGE plpgsql AS $func$
BEGIN
    RETURN query 
	SELECT books.id, books.title, books.author, books.year
    FROM books WHERE books.year = year_value;
END;

$func$;

CREATE OR REPLACE PROCEDURE delete_book_by_year (year_value integer) LANGUAGE plpgsql AS
$$
   BEGIN 
      DELETE FROM books WHERE books.year = year_value;
   END;
$$;

CREATE OR REPLACE PROCEDURE update_book(
    title   TEXT,
    author  TEXT,
    year integer,
    id  integer) LANGUAGE plpgsql  AS $$
BEGIN
    UPDATE books
       SET  title = update_book.title,
            author = update_book.author,
            year = update_book.year
     WHERE books.id = update_book.id;
END;
$$;

CREATE OR REPLACE PROCEDURE create_book(
	IN title_val text,
	IN author_val text,
	IN year_val integer)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    INSERT INTO books (title, author, year)
	VALUES (title_val, author_val, year_val);
END;
$BODY$;