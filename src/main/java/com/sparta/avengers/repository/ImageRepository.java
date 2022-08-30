package com.sparta.avengers.repository;


import com.sparta.avengers.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {

    Optional<Image> findByImgURL(String imgUrl);
}
