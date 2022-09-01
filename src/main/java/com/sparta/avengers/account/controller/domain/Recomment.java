package com.sparta.avengers.account.controller.domain;


import com.example.intermediate.controller.request.CommentRequestDto;
import com.sparta.avengers.account.controller.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recomment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "comment_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private int likenum;
    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
    }
    public void pushLike()
    {
        this.likenum++;
    }
    public void pushDislike()
    {
        if(this.likenum>0)
        {
            this.likenum--;
        }
    }
    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
    public boolean validateComment(Comment comment){return !this.comment.equals(comment);}



}