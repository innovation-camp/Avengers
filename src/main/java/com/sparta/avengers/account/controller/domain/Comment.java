package com.sparta.avengers.account.controller.domain;

import com.example.intermediate.controller.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private com.example.intermediate.domain.Post post;

    @OneToMany(mappedBy = "comment",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.example.intermediate.domain.Recomment> recomments;
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
}