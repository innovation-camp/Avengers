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
public class MyPage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mypage_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private List<Board> boardList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private List<Comment> commentList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "nestedcomment_id", nullable = false)
    private List<NestedComment> nestedCommentList;




}
