package tables;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="books")
public class Books implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String title;
    private String author;
    private Date published_date;
    private String isbn;

    public Books(){
        Date date = new Date();
        Random random = new Random();
        this.title = "";
        this.author= "";
        this.published_date = date;
        this.isbn =String.valueOf(random.nextInt());
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublished_date() {
        return published_date;
    }

    public void setPublished_date(Date published_date) {
        this.published_date = published_date;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


    public String toString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(published_date);
        int year = calendar.get(Calendar.YEAR);
        return "Title: " + this.title + "\tAuthor: " + this.author + "\tISBN: " + this.isbn + "\tPublished: " + year + "\n";
    }

}
