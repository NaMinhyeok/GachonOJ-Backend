package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
}
