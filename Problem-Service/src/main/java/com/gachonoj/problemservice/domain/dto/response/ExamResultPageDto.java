    package com.gachonoj.problemservice.domain.dto.response;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.data.domain.Page;

    import java.util.List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ExamResultPageDto {
        private String examTitle;
        private String examMemo;
        private int submissionTotal;
        private List<ExamResultListDto> results;
    }