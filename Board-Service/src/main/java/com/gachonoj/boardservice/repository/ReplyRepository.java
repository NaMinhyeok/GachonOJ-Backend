package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply,Long> {
}
