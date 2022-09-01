package com.sparta.avengers.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class MyLikePage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mylikepage_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memeber_id", nullable = false)
    private Member member;

    @JoinColumn(name = "likePost_id", nullable = false)
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Boardlike> boardLikeList;

    @JoinColumn(name = "likeComment_id", nullable = false)
    @ManyToMany(fetch = FetchType.LAZY)
    private List<CommentLike> commentLikeList;
}
