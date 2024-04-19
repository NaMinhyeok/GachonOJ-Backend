package com.gachonoj.submissionservice.controller;

import com.gachonoj.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

}
