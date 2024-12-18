package org.example.goodreads;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@ToString(includeFieldNames = true)
@Table(name= "book")
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    private String Description;
    private String purchaseLink;
    private String photoUrl;

    @Transient // Pole nie będzie zapisywane w bazie
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ManyToMany(mappedBy = "genres")
    private List<Genre> genres = new ArrayList<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getBooks().add(this);
    }



}
