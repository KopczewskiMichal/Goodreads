package org.example.goodreads;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long opinion_id;

    private String text;

    @Column(nullable = false)
    private short stars;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    @Transient
    private static final ObjectMapper mapper = new ObjectMapper();
}
