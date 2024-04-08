package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
}
