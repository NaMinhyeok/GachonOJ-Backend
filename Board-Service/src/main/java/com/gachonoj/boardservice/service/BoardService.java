package com.gachonoj.boardservice.service;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
import com.gachonoj.boardservice.domain.dto.request.InquiryRequestDto;
import com.gachonoj.boardservice.domain.dto.request.NoticeRequestDto;
import com.gachonoj.boardservice.domain.dto.request.ReplyRequestDto;
import com.gachonoj.boardservice.domain.dto.response.*;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.domain.entity.Notice;
import com.gachonoj.boardservice.domain.entity.Reply;
import com.gachonoj.boardservice.feign.client.MemberServiceFeignClient;
import com.gachonoj.boardservice.feign.dto.MemberNicknamesDto;
import com.gachonoj.boardservice.repository.InquiryRepository;
import com.gachonoj.boardservice.repository.NoticeRepository;
import com.gachonoj.boardservice.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final NoticeRepository noticeRepository;
    private final InquiryRepository inquiryRepository;
    private final ReplyRepository replyRepository;
    private final MemberServiceFeignClient memberServiceFeignClient;

    private static final int PAGE_SIZE = 10;


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
        } else {
            throw new IllegalArgumentException("해당 문의사항에 대한 권한이 없습니다.");
        }
    }
    // 문의사항 삭제 회원
    @Transactional
    public void deleteInquiryByMember(Long inquiryId,Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("해당 문의사항이 존재하지 않습니다."));
        if (inquiry.getMemberId().equals(memberId)) {
            inquiryRepository.delete(inquiry);
        } else {
            throw new IllegalArgumentException("해당 문의사항에 대한 권한이 없습니다.");
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
        inquiry.updateInquiryStatus(InquiryStatus.COMPLETED);
    }
    // 메인 대시보드 공지사항 목록 조회 최대 5개
    @Transactional(readOnly = true)
    public List<NoticeMainResponseDto> getMainNoticeList() {
        List<Notice> noticeList = noticeRepository.findTop5ByOrderByNoticeCreatedDateDesc();
        List<NoticeMainResponseDto> noticeMainResponseDtos = new ArrayList<>();
        List<Long> memberIds = noticeList.stream().map(Notice::getMemberId).toList();
        List<MemberNicknamesDto> memberNicknamesDtos = memberServiceFeignClient.getNicknames(memberIds);
        for (Notice notice : noticeList) {
            String memberNickname = memberNicknamesDtos.stream()
                    .filter(memberNicknamesDto -> memberNicknamesDto.getMemberId().equals(notice.getMemberId()))
                    .findFirst()
                    .map(MemberNicknamesDto::getMemberNickname)
                    .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
            String createdDate = dateFormatter(notice.getNoticeUpdatedDate());
            noticeMainResponseDtos.add(new NoticeMainResponseDto(notice,createdDate,memberNickname));
        }
        return noticeMainResponseDtos;
    }
    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public Page<NoticeListResponseDto> getNoticeList(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1, PAGE_SIZE);
        Page<Notice> noticePage = noticeRepository.findAllByOrderByNoticeCreatedDateDesc(pageable);
        List<MemberNicknamesDto> memberNicknames = memberServiceFeignClient.getNicknames(noticePage.stream().map(Notice::getMemberId).toList());
        return noticePage.map(notice -> {
            String createdDate = dateFormatter(notice.getNoticeUpdatedDate());
            String memberNickname = memberNicknames.stream()
                    .filter(memberNicknamesDto -> memberNicknamesDto.getMemberId().equals(notice.getMemberId()))
                    .findFirst()
                    .map(MemberNicknamesDto::getMemberNickname)
                    .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
            return new NoticeListResponseDto(notice,createdDate,memberNickname);
        });
    }
    // 공지사항 상세 조회
    @Transactional(readOnly = true)
    public NoticeDetailResponseDto getNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));
        String createdDate = dateFormatter(notice.getNoticeUpdatedDate());
        String memberNickname = memberServiceFeignClient.getNicknames(notice.getMemberId());
        return new NoticeDetailResponseDto(notice,createdDate,memberNickname);
    }
    // 문의사항 목록 조회 관리자
    @Transactional(readOnly = true)
    public Page<InquiryAdminListResponseDto> getInquiryListAdmin(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1, PAGE_SIZE);
        Page<Inquiry> inquiryPage = inquiryRepository.findAllByOrderByInquiryCreatedDateDesc(pageable);
        List<Long> memberIds = inquiryPage.stream().map(Inquiry::getMemberId).toList();
        List<MemberNicknamesDto> memberNicknames = memberServiceFeignClient.getNicknames(memberIds);
        return inquiryPage.map(inquiry -> {
            String createdDate = dateFormatter(inquiry.getInquiryCreatedDate());
            String memberNickname = memberNicknames.stream()
                    .filter(memberNicknamesDto -> memberNicknamesDto.getMemberId().equals(inquiry.getMemberId()))
                    .findFirst()
                    .map(MemberNicknamesDto::getMemberNickname)
                    .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
            if(inquiry.getInquiryStatus()== InquiryStatus.COMPLETED && inquiry.getReply() != null){
                String replyUpdateDate = dateFormatter(inquiry.getReply().getReplyUpdatedDate());
                return new InquiryAdminListResponseDto(inquiry,memberNickname,createdDate,replyUpdateDate);
            } else {
                return new InquiryAdminListResponseDto(inquiry,memberNickname,createdDate);
            }
        });
    }
    // 문의사항 목록 조회 사용자
    @Transactional(readOnly = true)
    public Page<InquiryListResponseDto> getInquiryList(Long memberId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1, PAGE_SIZE);
        Page<Inquiry> inquiryPage = inquiryRepository.findByMemberIdOrderByInquiryCreatedDateDesc(memberId,pageable);
        return inquiryPage.map(inquiry -> {
            String createdDate = dateFormatter(inquiry.getInquiryCreatedDate());
            return new InquiryListResponseDto(inquiry,createdDate);
        });
    }
    // 문의사항 상세 조회 사용자
    @Transactional(readOnly = true)
    public InquiryDetailResponseDto getInquiryDetail(Long inquiryId,Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("해당 문의사항이 존재하지 않습니다."));
        String inquiryCreatedDate = dateFormatter(inquiry.getInquiryCreatedDate());
        if(!inquiry.getMemberId().equals(memberId)){
            throw new IllegalArgumentException("해당 문의사항에 대한 권한이 없습니다.");
        }
        if(inquiry.getInquiryStatus() == InquiryStatus.COMPLETED && inquiry.getReply() != null){
            return new InquiryDetailResponseDto(inquiry,inquiryCreatedDate, inquiry.getReply());
        }
        return new InquiryDetailResponseDto(inquiry,inquiryCreatedDate);
    }
    // 문의사항 상세 조회 관리자
    @Transactional(readOnly = true)
    public InquiryDetailAdminResponseDto getInquiryDetailAdmin(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("해당 문의사항이 존재하지 않습니다."));
        String memberNickname = memberServiceFeignClient.getNicknames(inquiry.getMemberId());
        String inquiryCreatedDate = dateFormatter(inquiry.getInquiryCreatedDate());
        if (inquiry.getInquiryStatus() == InquiryStatus.COMPLETED && inquiry.getReply() != null) {
            return new InquiryDetailAdminResponseDto(inquiry, memberNickname, inquiryCreatedDate, inquiry.getReply());
        }
        return new InquiryDetailAdminResponseDto(inquiry, memberNickname,inquiryCreatedDate);
    }
    // 관리자 대시보드 최근 답변되지않은 문의사항 목록 조회
    @Transactional(readOnly = true)
    public List<InquiryAdminListResponseDto> getRecentInquiryList() {
        List<Inquiry> inquiries = inquiryRepository.findTop5ByInquiryStatusOrderByInquiryCreatedDateDesc(InquiryStatus.NONE);
        List<InquiryAdminListResponseDto> inquiryAdminListResponseDtos = new ArrayList<>();
        List<Long> memberIds = inquiries.stream().map(Inquiry::getMemberId).toList();
        List<MemberNicknamesDto> memberNicknames = memberServiceFeignClient.getNicknames(memberIds);
        for (Inquiry inquiry : inquiries) {
            String memberNickname = memberNicknames.stream()
                    .filter(memberNicknamesDto -> memberNicknamesDto.getMemberId().equals(inquiry.getMemberId()))
                    .findFirst()
                    .map(MemberNicknamesDto::getMemberNickname)
                    .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
            String createdDate = dateFormatter(inquiry.getInquiryCreatedDate());
            InquiryAdminListResponseDto responseDto = new InquiryAdminListResponseDto(inquiry, memberNickname, createdDate, InquiryStatus.NONE);
            inquiryAdminListResponseDtos.add(responseDto);
        }
        return inquiryAdminListResponseDtos;
    }
    // 시간 포맷 변경
    private String dateFormatter(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return time.format(formatter);
    }
}
