package tables;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

@Entity
@Table(name="books_reviews")
public class BooksReviews implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private int book_id;
    private int user_id;
    private String review_content;
    private int rating;
    private Date published_date;


    public BooksReviews(){
        Random random = new Random();
        Date date = new Date();
        this.review_content = "";
        this.user_id = 3;
        this.book_id = 1;
        this.rating = random.nextInt();
        this.published_date = date;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getPublished_date() {
        return published_date;
    }

    public void setPublished_date(Date published_date) {
        this.published_date = published_date;
    }

    public String toString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(published_date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = month + "/" + day + "/" + year;
        return "Book ID: " + this.book_id + "\tUser ID: " + this.user_id + "\tReview: " + this.review_content + "\tRating: " + this.rating + "\tPublished: " + date + "\n";
    }

}
