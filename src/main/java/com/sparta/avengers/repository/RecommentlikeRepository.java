//package com.sparta.avengers.account.repository;
//
//
//import com.sparta.avengers.entity.CommentLike;
//import com.sparta.avengers.entity.NestedComment;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.lang.reflect.Member;
//import java.util.List;
//
//public interface RecommentlikeRepository extends JpaRepository<CommentLike, Long> {
//    List<CommentLike> findAllByMember(Member member);
//    List<CommentLike> findAllByNestedComment(NestedComment recomment);
//
//}
//이거 쓰임새가 없어서 주석 처리했습니당