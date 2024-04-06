package com.gachonoj.memeberservice.repository;

import com.gachonoj.memeberservice.domain.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
}
