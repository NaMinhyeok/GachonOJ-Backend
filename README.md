

<div align="center">
<a href="http://cecloud.gachon.ac.kr:65001/">
<img width="100%" src="https://github.com/gcu-LastDance/GachonOJ-Backend/assets/112960401/975fc37e-ed23-4b15-968a-d06ca072ab94" alt="GachonOJ 이동하기"/>
</a>



[![](https://img.shields.io/badge/-gachonoj-important?style=flat&logo=airplayvideo&logoColor=white&labelColor=black&color=%233145FF)](http://cecloud.gachon.ac.kr:65001/)
[![](https://img.shields.io/badge/back_release-v1.0.0-critical?style=flat&logo=github&logoColor=balck&labelColor=black&color=white)
](https://github.com/gcu-LastDance/GachonOJ-Backend/releases)

## 👻 Member

<table>
<tr>
<td align="center">BE<strong>(PM)</strong></td>
<td align="center">BE</td>
<td align="center">FE</td>
<td align="center">FE</td>

</tr>
  <tr>
    <td align="center" width="120px">
      <a href="https://github.com/naminhyeok" target="_blank">
        <img src="" alt="나민혁 프로필" />
      </a>
    </td>
    <td align="center" width="120px">
      <a href="https://github.com/chogh824" target="_blank">
        <img src="" alt="조기헌 프로필" />
      </a>
    </td>
    <td align="center" width="120px">
      <a href="https://github.com/11chyeonjin" target="_blank">
        <img src="" alt="정현진 프로필" />
      </a>
    </td>
    <td align="center" width="120px">
      <a href="https://github.com/ehs208" target="_blank">
        <img src="" alt="은현수 프로필" />
      </a>
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/naminhyeok" target="_blank">
        나민혁 🐥
      </a>
    </td>
     <td align="center">
      <a href="https://github.com/chogh824" target="_blank">
       조기헌
      </a>
    </td> 
     <td align="center">
      <a href="https://github.com/11chyeonjin" target="_blank">
       정현진
      </a>
       <td align="center">
      <a href="https://github.com/ehs208" target="_blank">
        은현수
      </a>
  </tr>
</table>

# <img src="https://github.com/gcu-LastDance/GachonOJ-Backend/assets/112960401/38316365-a949-4c98-8863-b9f70e82d572" align=left width=90>
> GachonOJ 가천대학교 학생들을 위한 저지 서비스 입니다.
>
> 개발 기간: 2024.03 - 2024.05

# GachonOJ

### ✨GachonOJ 가천대학교 학생들을 위한 저지 서비스 ✨

</div>

## 🔍 About GachonOJ

GachonOJ는 가천대학교 학생들을 위해 개발되어진 온라인 저지 플랫폼입니다.


## 🖥️ Service

| 웹IDE & 채점 서비스 |  AI 피드백 구현   |
|:-------------:|:------------:|
| <img src=''>  | <img src=''> |

## 📦 주요 기능
### USER SIDE
⚡️ 웹 IDE

⚡️ 코드 실행 및 채점하기

⚡️ AI 피드백을 통한 코드 개선

⚡️ 손코딩을 대체할 시험 제출

⚡️ 사용자 프로필 관리

### ADMIN SIDE

⚡️ 사용자 관리

⚡️ 문제/시험/대회 관리

⚡️ 공지사항/문의사항 관리

## 🛠️ 주요 의존성 패키지 버전

- **Spring Boot**: 3.2.4
- **Spring Dependency Management Plugin**: 1.1.4

#### Spring Boot Dependencies
- **spring-boot-starter-web**
- **spring-boot-starter-data-jpa**
- **spring-boot-starter-security**
- **spring-boot-starter-validation**
- **spring-boot-starter-mail**
- **spring-boot-starter-data-redis**
- **spring-boot-starter-actuator**

#### Spring Cloud Dependencies
- **spring-cloud-starter-aws**: 2.2.6.RELEASE
- **spring-cloud-starter-netflix-eureka-client**
- **spring-cloud-starter-netflix-eureka-server**
- **spring-cloud-starter-openfeign**
#### JWT Dependencies
- **jjwt-api**: 0.12.3 by io.jsonwebtoken
- **jjwt-impl**: 0.12.3 by io.jsonwebtoken
- **jjwt-jackson**: 0.12.3 by io.jsonwebtoken

#### Test Dependencies
- **spring-boot-starter-test**
- **spring-security-test**

#### Other Plugins
- **springdoc-openapi-starter-webmvc-ui**: 2.2.0
- **org.projectlombok:lombok** (compileOnly)
- **org.mariadb.jdbc:mariadb-java-client** (runtimeOnly)
- **org.springframework.boot:spring-boot-devtools** (developmentOnly)
- **io.micrometer:micrometer-registry-prometheus** (runtimeOnly)
## 📁 Project Structure

```bash
├── AI-Service
│   ├── Dockerfile
│   ├── HELP.md
│   ├── build
│   ├── build.gradle
│   ├── gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── gachonoj
│       │   │           └── aiservice
│       │   │               ├── AiServiceApplication.java
│       │   │               ├── common
│       │   │               │   ├── codes
│       │   │               │   │   ├── ErrorCode.java
│       │   │               │   │   ├── ResponseCode.java
│       │   │               │   │   └── SuccessCode.java
│       │   │               │   └── response
│       │   │               │       └── CommonResponseDto.java
│       │   │               ├── config
│       │   │               │   ├── OpenAiConfig.java
│       │   │               │   └── exception
│       │   │               │       └── GlobalExceptionHandler.java
│       │   │               ├── controller
│       │   │               │   └── AiController.java
│       │   │               ├── domain
│       │   │               │   ├── dto
│       │   │               │   │   ├── request
│       │   │               │   │   │   ├── ChatGPTRequest.java
│       │   │               │   │   │   └── FeedbackRequestDto.java
│       │   │               │   │   └── response
│       │   │               │   │       ├── AiFeedbackResponseDto.java
│       │   │               │   │       ├── ChatGPTResponse.java
│       │   │               │   │       ├── Message.java
│       │   │               │   │       └── TokenUsageResponseDto.java
│       │   │               │   └── entity
│       │   │               │       └── Feedback.java
│       │   │               ├── feign
│       │   │               │   ├── client
│       │   │               │   │   ├── MemberServiceFeignClient.java
│       │   │               │   │   ├── ProblemServiceFeignClient.java
│       │   │               │   │   └── SubmissionServiceFeignClient.java
│       │   │               │   ├── controller
│       │   │               │   │   └── AiFeignController.java
│       │   │               │   ├── dto
│       │   │               │   │   └── response
│       │   │               │   │       └── SubmissionCodeInfoResponseDto.java
│       │   │               │   └── service
│       │   │               │       └── AiFeignService.java
│       │   │               ├── repository
│       │   │               │   └── FeedbackRepository.java
│       │   │               └── service
│       │   │                   └── AiService.java
│       │   └── resources
│       │       ├── application-dev.properties
│       │       ├── application.properties
│       │       ├── static
│       │       └── templates
│       └── test
├── APIGateway
│   ├── Dockerfile
│   ├── HELP.md
│   ├── build
│   ├── build.gradle
│   ├── gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── gachonoj
│       │   │           └── apigateway
│       │   │               ├── ApiGatewayApplication.java
│       │   │               ├── auth
│       │   │               │   ├── AuthController.java
│       │   │               │   ├── AuthService.java
│       │   │               │   ├── AuthorizationHeaderFilter.java
│       │   │               │   ├── JwtUtil.java
│       │   │               │   ├── LoggingGlobalFilter.java
│       │   │               │   ├── RedisService.java
│       │   │               │   └── RefreshRequestDto.java
│       │   │               └── config
│       │   │                   ├── RedisConfig.java
│       │   │                   └── SecurityConfig.java
│       │   └── resources
│       │       ├── application-dev.properties
│       │       └── application.properties
│       └── test
├── Board-Service
│   ├── Dockerfile
│   ├── HELP.md
│   ├── build
│   ├── build.gradle
│   ├── gradle
│   │   └── wrapper
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── gachonoj
│       │   │           └── boardservice
│       │   │               ├── BoardServiceApplication.java
│       │   │               ├── common
│       │   │               │   ├── codes
│       │   │               │   │   ├── ErrorCode.java
│       │   │               │   │   ├── ResponseCode.java
│       │   │               │   │   └── SuccessCode.java
│       │   │               │   └── response
│       │   │               │       └── CommonResponseDto.java
│       │   │               ├── config
│       │   │               │   └── exception
│       │   │               │       └── GlobalExceptionHandler.java
│       │   │               ├── controller
│       │   │               │   └── BoardController.java
│       │   │               ├── domain
│       │   │               │   ├── constant
│       │   │               │   │   └── InquiryStatus.java
│       │   │               │   ├── dto
│       │   │               │   │   ├── request
│       │   │               │   │   │   ├── InquiryRequestDto.java
│       │   │               │   │   │   ├── NoticeRequestDto.java
│       │   │               │   │   │   └── ReplyRequestDto.java
│       │   │               │   │   └── response
│       │   │               │   │       ├── InquiryAdminListResponseDto.java
│       │   │               │   │       ├── InquiryDetailAdminResponseDto.java
│       │   │               │   │       ├── InquiryDetailResponseDto.java
│       │   │               │   │       ├── InquiryListResponseDto.java
│       │   │               │   │       ├── NoticeDetailResponseDto.java
│       │   │               │   │       ├── NoticeListResponseDto.java
│       │   │               │   │       ├── NoticeMainResponseDto.java
│       │   │               │   │       └── ReplyResponseDto.java
│       │   │               │   └── entity
│       │   │               │       ├── Inquiry.java
│       │   │               │       ├── Notice.java
│       │   │               │       └── Reply.java
│       │   │               ├── feign
│       │   │               │   └── client
│       │   │               │       └── MemberServiceFeignClient.java
│       │   │               ├── repository
│       │   │               │   ├── InquiryRepository.java
│       │   │               │   ├── NoticeRepository.java
│       │   │               │   └── ReplyRepository.java
│       │   │               └── service
│       │   │                   └── BoardService.java
│       │   └── resources
│       │       ├── application-dev.properties
│       │       └── application.properties
│       └── test
├── EurekaServer
│   ├── Dockerfile
│   ├── HELP.md
│   ├── build
│   ├── build.gradle
│   ├── gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── gachonoj
│       │   │           └── eurekaserver
│       │   │               └── EurekaServerApplication.java
│       │   └── resources
│       │       ├── application-dev.properties
│       │       └── application.properties
│       └── test
│           └── java
│               └── com
│                   └── gachonoj
│                       └── eurekaserver
│                           └── EurekaServerApplicationTests.java
├── GachonOJ-Backend.iml
├── Member-Service
│   ├── Dockerfile
│   ├── HELP.md
│   ├── build
│   ├── build.gradle
│   ├── gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── gachonoj
│       │   │           └── memberservice
│       │   │               ├── MemberServiceApplication.java
│       │   │               ├── common
│       │   │               │   ├── codes
│       │   │               │   │   ├── ErrorCode.java
│       │   │               │   │   ├── ResponseCode.java
│       │   │               │   │   └── SuccessCode.java
│       │   │               │   └── response
│       │   │               │       └── CommonResponseDto.java
│       │   │               ├── config
│       │   │               │   ├── EmailConfig.java
│       │   │               │   ├── RedisConfig.java
│       │   │               │   ├── S3Config.java
│       │   │               │   ├── SecurityConfig.java
│       │   │               │   └── exception
│       │   │               │       └── GlobalExceptionHandler.java
│       │   │               ├── controller
│       │   │               │   └── MemberController.java
│       │   │               ├── domain
│       │   │               │   ├── constant
│       │   │               │   │   ├── MemberLang.java
│       │   │               │   │   └── Role.java
│       │   │               │   ├── dto
│       │   │               │   │   ├── request
│       │   │               │   │   │   ├── CreateMemberRequestDto.java
│       │   │               │   │   │   ├── EmailRequestDto.java
│       │   │               │   │   │   ├── EmailVerificationRequestDto.java
│       │   │               │   │   │   ├── LoginRequestDto.java
│       │   │               │   │   │   ├── MemberInfoRequestDto.java
│       │   │               │   │   │   ├── MemberLangRequestDto.java
│       │   │               │   │   │   ├── MemberNicknameRequestDto.java
│       │   │               │   │   │   ├── SignUpRequestDto.java
│       │   │               │   │   │   ├── UpdateMemberRequestDto.java
│       │   │               │   │   │   └── UpdatePasswordRequestDto.java
│       │   │               │   │   └── response
│       │   │               │   │       ├── HoverResponseDto.java
│       │   │               │   │       ├── LoginResponseDto.java
│       │   │               │   │       ├── MemberInfoByAdminResponseDto.java
│       │   │               │   │       ├── MemberInfoExamResponseDto.java
│       │   │               │   │       ├── MemberInfoProblemResponseDto.java
│       │   │               │   │       ├── MemberInfoRankingResponseDto.java
│       │   │               │   │       ├── MemberInfoResponseDto.java
│       │   │               │   │       ├── MemberInfoTestResponseDto.java
│       │   │               │   │       ├── MemberLangCountResponseDto.java
│       │   │               │   │       ├── MemberLangResponseDto.java
│       │   │               │   │       ├── MemberListResponseDto.java
│       │   │               │   │       ├── MemberRankingResponseDto.java
│       │   │               │   │       └── NicknameVerificationResponseDto.java
│       │   │               │   └── entity
│       │   │               │       └── Member.java
│       │   │               ├── feign
│       │   │               │   ├── client
│       │   │               │   │   ├── ProblemServiceFeignClient.java
│       │   │               │   │   └── SubmissionServiceFeignClient.java
│       │   │               │   ├── controller
│       │   │               │   │   └── MemberFeignController.java
│       │   │               │   ├── dto
│       │   │               │   │   └── response
│       │   │               │   │       ├── ProblemMemberInfoResponseDto.java
│       │   │               │   │       ├── SubmissionMemberInfoResponseDto.java
│       │   │               │   │       └── SubmissionMemberRankInfoResponseDto.java
│       │   │               │   └── service
│       │   │               │       └── MemberFeignService.java
│       │   │               ├── jwt
│       │   │               │   ├── CustomUserDetails.java
│       │   │               │   ├── CustomUserDetailsService.java
│       │   │               │   ├── JwtUtil.java
│       │   │               │   └── LoginFilter.java
│       │   │               ├── repository
│       │   │               │   └── MemberRepository.java
│       │   │               └── service
│       │   │                   ├── MemberService.java
│       │   │                   ├── RedisService.java
│       │   │                   └── S3UploadService.java
│       │   └── resources
│       │       ├── application-dev.properties
│       │       ├── application.properties
│       │       ├── static
│       │       └── templates
│       └── test
├── Problem-Service
│   ├── Dockerfile
│   ├── HELP.md
│   ├── build
│   ├── build.gradle
│   ├── gradle
│   │   └── wrapper
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── gachonoj
│       │   │           └── problemservice
│       │   │               ├── ProblemServiceApplication.java
│       │   │               ├── common
│       │   │               │   ├── codes
│       │   │               │   │   ├── ErrorCode.java
│       │   │               │   │   ├── ResponseCode.java
│       │   │               │   │   └── SuccessCode.java
│       │   │               │   └── response
│       │   │               │       └── CommonResponseDto.java
│       │   │               ├── config
│       │   │               │   └── exception
│       │   │               │       └── GlobalExceptionHandler.java
│       │   │               ├── controller
│       │   │               │   └── ProblemController.java
│       │   │               ├── domain
│       │   │               │   ├── constant
│       │   │               │   │   ├── ExamStatus.java
│       │   │               │   │   ├── ExamType.java
│       │   │               │   │   ├── ProblemClass.java
│       │   │               │   │   ├── ProblemStatus.java
│       │   │               │   │   └── TestcaseStatus.java
│       │   │               │   ├── dto
│       │   │               │   │   ├── request
│       │   │               │   │   │   ├── CandidateListRequestDto.java
│       │   │               │   │   │   ├── ExamRequestDto.java
│       │   │               │   │   │   ├── ProblemRequestDto.java
│       │   │               │   │   │   ├── QuestionRequestDto.java
│       │   │               │   │   │   └── TestcaseRequestDto.java
│       │   │               │   │   └── response
│       │   │               │   │       ├── BookmarkProblemResponseDto.java
│       │   │               │   │       ├── ExamCardInfoResponseDto.java
│       │   │               │   │       ├── ExamDetailResponseDto.java
│       │   │               │   │       ├── ExamEnterResponseDto.java
│       │   │               │   │       ├── ExamOrContestInfoResponseDto.java
│       │   │               │   │       ├── ExamOrContestListResponseDto.java
│       │   │               │   │       ├── ExamResultDetailsResponseDto.java
│       │   │               │   │       ├── ExamResultListDto.java
│       │   │               │   │       ├── ExamResultPageDto.java
│       │   │               │   │       ├── PastContestResponseDto.java
│       │   │               │   │       ├── ProblemCardResponseDto.java
│       │   │               │   │       ├── ProblemDetailAdminResponseDto.java
│       │   │               │   │       ├── ProblemDetailResponseDto.java
│       │   │               │   │       ├── ProblemListByAdminResponseDto.java
│       │   │               │   │       ├── ProblemListResponseDto.java
│       │   │               │   │       ├── ProfessorExamListResponseDto.java
│       │   │               │   │       ├── QuestionResultDetailsResponseDto.java
│       │   │               │   │       ├── QuestionResultDto.java
│       │   │               │   │       ├── RecommendProblemResponseDto.java
│       │   │               │   │       ├── ScheduledContestResponseDto.java
│       │   │               │   │       ├── SolvedProblemResponseDto.java
│       │   │               │   │       ├── TestcaseResponseDto.java
│       │   │               │   │       └── WrongProblemResponseDto.java
│       │   │               │   └── entity
│       │   │               │       ├── Bookmark.java
│       │   │               │       ├── Exam.java
│       │   │               │       ├── Problem.java
│       │   │               │       ├── Question.java
│       │   │               │       ├── Test.java
│       │   │               │       └── Testcase.java
│       │   │               ├── feign
│       │   │               │   ├── client
│       │   │               │   │   ├── MemberServiceFeignClient.java
│       │   │               │   │   └── SubmissionServiceFeignClient.java
│       │   │               │   ├── controller
│       │   │               │   │   └── ProblemFeignController.java
│       │   │               │   ├── dto
│       │   │               │   │   └── response
│       │   │               │   │       ├── CorrectRateResponseDto.java
│       │   │               │   │       ├── ProblemMemberInfoResponseDto.java
│       │   │               │   │       ├── SubmissionDetailDto.java
│       │   │               │   │       ├── SubmissionExamResultInfoResponseDto.java
│       │   │               │   │       ├── SubmissionProblemTestCaseResponseDto.java
│       │   │               │   │       └── SubmissionResultCountResponseDto.java
│       │   │               │   └── service
│       │   │               │       └── ProblemFeignService.java
│       │   │               ├── repository
│       │   │               │   ├── BookmarkRepository.java
│       │   │               │   ├── ExamRepository.java
│       │   │               │   ├── ProblemRepository.java
│       │   │               │   ├── QuestionRepository.java
│       │   │               │   ├── TestRepository.java
│       │   │               │   └── TestcaseRepository.java
│       │   │               └── service
│       │   │                   ├── ExamService.java
│       │   │                   └── ProblemService.java
│       │   └── resources
│       │       ├── application-dev.properties
│       │       ├── application.properties
│       │       ├── static
│       │       └── templates
│       └── test
├── README.md
├── Submission-Service
│   ├── Dockerfile
│   ├── HELP.md
│   ├── build
│   ├── build.gradle
│   ├── gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── gachonoj
│       │   │           └── submissionservice
│       │   │               ├── SubmissionServiceApplication.java
│       │   │               ├── common
│       │   │               │   ├── codes
│       │   │               │   │   ├── ErrorCode.java
│       │   │               │   │   ├── ResponseCode.java
│       │   │               │   │   └── SuccessCode.java
│       │   │               │   └── response
│       │   │               │       └── CommonResponseDto.java
│       │   │               ├── config
│       │   │               │   └── exception
│       │   │               │       └── GlobalExceptionHandler.java
│       │   │               ├── controller
│       │   │               │   └── SubmissionController.java
│       │   │               ├── domain
│       │   │               │   ├── constant
│       │   │               │   │   ├── Language.java
│       │   │               │   │   └── Status.java
│       │   │               │   ├── dto
│       │   │               │   │   ├── request
│       │   │               │   │   │   ├── ExamSubmitRequestDto.java
│       │   │               │   │   │   └── ExecuteRequestDto.java
│       │   │               │   │   └── response
│       │   │               │   │       ├── ExecuteResultResponseDto.java
│       │   │               │   │       ├── MySubmissionResultResponseDto.java
│       │   │               │   │       ├── SubmissionRecordResponseDto.java
│       │   │               │   │       ├── SubmissionResultResponseDto.java
│       │   │               │   │       └── TodaySubmissionCountResponseDto.java
│       │   │               │   └── entity
│       │   │               │       ├── Love.java
│       │   │               │       └── Submission.java
│       │   │               ├── feign
│       │   │               │   ├── client
│       │   │               │   │   ├── MemberServiceFeignClient.java
│       │   │               │   │   └── ProblemServiceFeignClient.java
│       │   │               │   ├── controller
│       │   │               │   │   └── SubmissionFeignController.java
│       │   │               │   ├── dto
│       │   │               │   │   └── response
│       │   │               │   │       ├── CorrectRateResponseDto.java
│       │   │               │   │       ├── SubmissionCodeInfoResponseDto.java
│       │   │               │   │       ├── SubmissionDetailDto.java
│       │   │               │   │       ├── SubmissionExamResultInfoResponseDto.java
│       │   │               │   │       ├── SubmissionExamResultResponseDto.java
│       │   │               │   │       ├── SubmissionMemberInfoResponseDto.java
│       │   │               │   │       ├── SubmissionMemberRankInfoResponseDto.java
│       │   │               │   │       ├── SubmissionProblemTestCaseResponseDto.java
│       │   │               │   │       └── SubmissionResultCountResponseDto.java
│       │   │               │   └── service
│       │   │               │       └── SubmissionFeignService.java
│       │   │               ├── repository
│       │   │               │   ├── LoveRepository.java
│       │   │               │   └── SubmissionRepository.java
│       │   │               └── service
│       │   │                   ├── ExecuteService.java
│       │   │                   ├── LoveService.java
│       │   │                   └── SubmissionService.java
│       │   └── resources
│       │       ├── application-dev.properties
│       │       ├── application.properties
│       │       ├── static
│       │       └── templates
│       └── test
└── src

```


## 📚 Skills

<img width="500px" src=''  alt="BackEnd Skills"/>

## ⚙️ Infra

<img width="600px" src=''  alt="Infra"/>

## 🪄 CI/CD

<img width="600px" src=''  alt="CI/CD"/>

## 🤝 Team Collaboration Tool

<img width="600px" src=''  alt="Collaboration Tools"/>


© 2024 Gachon Univ. Online Judge. All Rights Reserved.
