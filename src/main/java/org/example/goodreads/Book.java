package org.example.goodreads;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long book_id;
    @Column(unique=true)
    private String ISBN;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @`JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    private String Description;
    private String purchaseLink;
    private String photoUrl;
}
