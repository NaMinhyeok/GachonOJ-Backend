package com.gachonoj.memberservice.repository;

import com.gachonoj.memberservice.domain.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
}
