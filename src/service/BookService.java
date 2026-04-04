package service;

import dao.BookDAO;

public class BookService {

    BookDAO dao = new BookDAO();

    public void addBook(String id, String title, String author, String publisher, String availability, int quantity) {

        dao.addBook(id, title, author, publisher, availability, quantity);
    }
}