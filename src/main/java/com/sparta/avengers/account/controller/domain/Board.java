package com.sparta.avengers.account.controller.domain;

import com.sparta.avengers.account.controller.request.BoardRequestDto;
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
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int likenum;

    @Column(nullable = false)
    private String Url;
    //board에 속한 comment들
    //fetch는 읽어오기 전략, LAZY는 필요할 때(사용하는 구간에 트랜젝션), EAGER은 항상
    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(BoardRequestDto boardRequestDto) {
        this.title = boardRequestDto.getTitle();
        this.content = boardRequestDto.getContent();
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