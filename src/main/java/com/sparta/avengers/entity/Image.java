package com.sparta.avengers.entity;

import com.sparta.avengers.validator.URLvalidator;
import com.sparta.avengers.validator.ValidateObject;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boardImage")
public class Image extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String imgURL;

    public Image(Board board, String imageUrl) {
        URLvalidator.isValidURL(imageUrl);
        ValidateObject.postValidate(board);
        this.board = board;
        this.imgURL = imageUrl;
    }
}
