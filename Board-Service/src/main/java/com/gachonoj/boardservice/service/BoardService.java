package com.gachonoj.boardservice.service;

import com.gachonoj.boardservice.domain.dto.request.InquiryRequestDto;
import com.gachonoj.boardservice.domain.dto.request.NoticeRequestDto;
import com.gachonoj.boardservice.domain.dto.request.ReplyRequestDto;
import com.gachonoj.boardservice.domain.dto.response.NoticeMainResponseDto;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.domain.entity.Notice;
import com.gachonoj.boardservice.domain.entity.Reply;
import com.gachonoj.boardservice.repository.InquiryRepository;
import com.gachonoj.boardservice.repository.NoticeRepository;
import com.gachonoj.boardservice.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final NoticeRepository noticeRepository;
    private final InquiryRepository inquiryRepository;
    private final ReplyRepository replyRepository;
    private final MemberServiceFeignClient memberServiceFeignClient;

    // 공지사항 작성
    @Transactional
    public void createNotice(NoticeRequestDto noticeRequestDto,Long memberId) {
        Notice notice = new Notice(noticeRequestDto.getNoticeTitle(), noticeRequestDto.getNoticeContents(),memberId);
        noticeRepository.save(notice);
    }
    // 공지사항 수정
    @Transactional
    public void updateNotice(Long noticeId, NoticeRequestDto noticeRequestDto, Long memberId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));
        notice.updateNotice(noticeRequestDto.getNoticeTitle(), noticeRequestDto.getNoticeContents(),memberId);
    }
    // 공지사항 삭제
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));
        noticeRepository.delete(notice);
    }
    // 문의사항 작성
    @Transactional
    public void createInquiry(InquiryRequestDto inquiryRequestDto, Long memberId) {
        Inquiry inquiry = new Inquiry(inquiryRequestDto.getInquiryTitle(), inquiryRequestDto.getInquiryContents(), memberId);
        inquiryRepository.save(inquiry);
    }
    // 문의사항 수정
    @Transactional
    public void updateInquiry(Long inquiryId, InquiryRequestDto inquiryRequestDto, Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("해당 문의사항이 존재하지 않습니다."));
        if (inquiry.getMemberId().equals(memberId)) {
            inquiry.updateInquiry(inquiryRequestDto.getInquiryTitle(), inquiryRequestDto.getInquiryContents());
        }
    }
    // 문의사항 삭제 회원
    @Transactional
    public void deleteInquiryByMember(Long inquiryId,Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("해당 문의사항이 존재하지 않습니다."));
        if (inquiry.getMemberId().equals(memberId)) {
            inquiryRepository.delete(inquiry);
        }
    }
    // 문의사항 삭제 관리자
    @Transactional
    public void deleteInquiryByAdmin(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("해당 문의사항이 존재하지 않습니다."));
        inquiryRepository.delete(inquiry);
    }
    // 문의사항 답변 작성
    @Transactional
    public void createReply(Long inquiryId, ReplyRequestDto replyRequestDto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("해당 문의사항이 존재하지 않습니다."));
        Reply reply = new Reply(inquiry, replyRequestDto.getReplyContents());
        replyRepository.save(reply);
    }
    // 메인 대시보드 공지사항 목록 조회 최대 5개
    public List<NoticeMainResponseDto> getMainNoticeList() {
        List<Notice> noticeList = noticeRepository.findTop5ByOrderByNoticeCreatedDateDesc();
        List<NoticeMainResponseDto> noticeMainResponseDtos = new ArrayList<>();
        for (Notice notice : noticeList) {
            String memberNickname = memberServiceFeignClient.getNicknames(notice.getMemberId());
            NoticeMainResponseDto responseDto = new NoticeMainResponseDto(notice,memberNickname);
            noticeMainResponseDtos.add(responseDto);
        }
        return noticeMainResponseDtos;
    }
}
