# ✈️ Voya9e
Voya9e는 다양한 기능을 활용한 안정적이고 효율적인 여행 일정 관리 사이트입니다.<br><br><br><br>
<img src="https://github.com/user-attachments/assets/a637d512-9bca-40c1-8d38-ae8d5c72fbc0" alt="voya9e" width="400" height="400"/>

<br>

## 프로젝트 소개

- 지도 검색, 날씨 검색 등을 통한 효율적인 여행 계획 수립
- 그룹화와 WebSocket을 활용한 팀 작업으로 안정적인 다중 일정 관리
- 영수증 OCR을 통한 정산 및 여행 경비 관리
- 챗봇을 활용한 여행지 추천, 일정 정리

<br>

## 팀원 구성

<div align="center">

| **윤건우** | **김가현** | **이현섭** | **정지수** | **장준우** |
| :------: |  :------: | :------: | :------: | :------: |
| [<img src="https://avatars.githubusercontent.com/u/69147082?v=4" height=150 width=150> <br/> @oo-ni](https://github.com/oo-ni) | [<img src="https://avatars.githubusercontent.com/u/118621852?v=4" height=150 width=150> <br/> @kahyun0255](https://github.com/kahyun0255) | [<img src="https://avatars.githubusercontent.com/u/97226053?v=4" height=150 width=150> <br/> @LeeHyeonseob](https://github.com/LeeHyeonseob) | [<img src="https://avatars.githubusercontent.com/u/76729926?v=4" height=150 width=150> <br/> @wjdwltn](https://github.com/wjdwltn) | [<img src="https://avatars.githubusercontent.com/u/176549799?v=4" height=150 width=150> <br/> @highjjjw](https://github.com/highjjjw)

</div>

<br>

## 개발 기간
2024.09.23 ~ 2024.10.10

<br>

## 팀 페이지 및 기획서
- <a href="https://www.notion.so/9-ef1c5042e8514c2eaad4f4b925f60e82?pvs=4">Notion Team Page</a>

- <a href="https://www.notion.so/9-2-34f399b1a3ef41ffa2558a9949ec9bf6?pvs=4">9팀 2차 프로젝트 기획서</a>

<br>

## 1. 개발 환경

- **OS** : Windows / MacOS
- **IDE** : IntelliJ IDEA 2024.1.4 (Ultimate Edition)
- **Language** : Java
- **Runtime Version** : 17.0.11+1-b1207.24 aarch64
- **Build Tool** : Gradle
- **Backend** : SpringBoot, JPA, QueryDSL
- **DB** : MySQL (Amazon RDS), Redis
- **Frontend** : React
- **ETC** : StompJS, JWT, Swagger UI, JUnit
<br>

## 2. 코드 컨벤션과 브랜치 전략
### 코드 컨벤션
<details>
<summary>Code Convention</summary>

<br>

> ☝ **명확한 의미 전달을 위해 축약형 사용을 지양합니다.**

> ☝ **작성한 코드를 팀원도 이해할 수 있도록 주석을 달아줍니다.** → 선택
> 
> ```java
> /**
>  * 사용자 관리를 위한 UserService 클래스
>  */
> public class UserService {
> 
>     /**
>      * 사용자를 등록하는 메서드
>      * @param user 등록할 사용자 객체
>      * @return 등록된 사용자 객체
>      */
>     public User registerUser(User user) {
>         // ...
>     }
> }
> ```

<br>

> ☝ **패키지 이름은 소문자로 생성하고, 역할이나 기능에 따라 명확하게 묶어서 명명합니다.**
> 
> 언더스코어 ‘_’나 대문자를 섞지 않습니다.
> 
> ```java
> com.example.project.controller
> com.example.project.service
> com.example.project.repository
> com.example.project.model
> ```

<br>

> ☝ **상수는 대문자와 언더스코어(”_“)로, 변수와 메서드는 CamelCase 형식으로 작성합니다.**
> 
> ```java
> // 상수
> static final int MAX_COUNT = 100;
> 
> // 변수
> int itemCount;
> 
> // 메서드
> public String printCount() { ... }
> ```

<br>

> ☝ **변수명을 짓기 어려울 때에는 아래 사이트의 도움을 받아봅시다!**
> 영어로 선택 후 원하는 단어를 검색하면 됩니다.
> 
> [Curioustore](https://curioustore.com/#!/)

<br>

> ☝ **Boolean 타입의 변수는 접두사로 is를 사용해 변수명을 작성합니다.**
> 
> ```java
> boolean isExist;
> boolean isTrue;
> ```

<br>

> ☝ **long 타입의 값의 마지막에는 대문자 ‘L’을 붙여줍시다.**
> 
> ```java
> long base = 54423234211L;
> ```

<br>

> ☝ **컬렉션 이름은 복수형을 사용하거나 컬렉션임을 명시해줍니다.**
> 
> ```java
> List ids;
> Map<User, int> userMap;
> ```

<br>

> ☝ **클래스명은 명사로 작성하고 UpperCamelCase를 사용합니다.**
> 
> ```java
> private class Address { ... }
> public class UserEmail { ... }
> ```

<br>

> ☝ **메서드명은 소문자로 시작하고 동사로 네이밍합니다.**
> 
> 대표적인 메서드들의 네이밍 규칙은 아래를 따릅니다.
> 
> ```java
> // 조회(상세)
> getXXX()
> getXXXDetail()
> getXXXInfo()
> 
> // 조회(리스트)
> getXXXList()
> 
> // 조회(카운트)
> getXXXCount()
> 
> // 등록
> createXXX()
> addXXX()
> registXXX()
> 
> // 수정
> updateXXX()
> modifyXXX()
> 
> // 삭제
> removeXXX()
> deleteXXX()
> ```

<br>

> ☝ **Enum 변수의 이름은 대문자로 작성합니다.**
> 
> ```java
> // 상태 - XXX_STATUS
> public enum MemberStatus {
>     WAITING_STATUS,    // 수락 대기 상태
>     ACCEPT_STATUS,     // 수락 상태
>     WITHDRAW_STATUS    // 탈퇴 상태
> }
> 
> // 유형 - XXX_TYPE
> public enum UserType {
>     ADMIN_TYPE,
>     CUSTOMER_TYPE,
>     GUEST_TYPE;
> }
> ```

<br>

> ☝ **builder를 호출하는 static 메서드는 다음 규칙을 따릅니다.**
> 
> 1. 파라미터가 1개인 경우: **xxxFrom**
> 
> ```java
> public static User createUserFromUsername(String username) {
>     // username을 사용해 builder 호출
>     return new User(username);
> }
> ```
> 
> 2. 파라미터가 2개 이상인 경우: **xxxOf**
> 
> ```java
> public static User createUserOf(String username, String email) {
>     // username과 email을 사용해 builder 호출
>     return new User(username, email);
> }
> ```

<br>

> ☝ **다른 객체로 변환하는 메서드의 이름은 toEntity 형식으로 선언합니다.**
> 
> ```java
> @Getter
> public class ProductCreateRequest {
>     private ProductType type;
>     private ProductSellingStatus sellingStatus;
>     private String name;
>     private int price;
> 
> 		// AllArgumentConstructor는 private으로 직접 사용을 막아주고, 
> 		// @Builder 선언해서 직접 사용 막기
>     @Builder
>     private ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
>         this.type = type;
>         this.sellingStatus = sellingStatus;
>         this.name = name;
>         this.price = price;
>     }
> 
> 		// toEntity
>     public Product toEntity(String productNumber) {
>         return Product.builder()
>                 .productNumber(productNumber)
>                 .type(type)
>                 .sellingStatus(sellingStatus)
>                 .name(name)
>                 .price(price)
>                 .build();
>     }
> }
> ```

<br>

> ☝ **Dto에서 배열 1개만 리턴할때는 Dto에 담지 않고 배열 자체를 반환해주도록 합시다.**
> 
> ```java
> {
>   list : [
>     "안녕하세요",
>     "~입니다."
>   ]
> }
> ```
> 
> 프론트에서 list에 한번 더 접근해야 하기 때문에 번거로워질 수 있습니다.
> 
> 따라서 아래처럼 Dto가 아닌 배열 자체를 반환해주도록 합니다.
> 
> ```java
> [
>   "안녕하세요",
>   "~입니다."
> ]
> ```

</details>


### 브랜치 전략

- Git-flow 전략을 기반으로 main, develop 브랜치와 feature 보조 브랜치를 운용했습니다.
- main, develop, Feat 브랜치로 나누어 개발을 하였습니다.
    - **main** 브랜치는 배포 단계에서만 사용하는 브랜치입니다.
    - **develop** 브랜치는 개발 단계에서 git-flow의 master 역할을 하는 브랜치입니다.
    - **Feature** 브랜치는 기능 단위로 독립적인 개발 환경을 위하여 사용하고 merge 후 각 브랜치를 삭제해주었습니다.

<br>

## 3. 프로젝트 구조

<details>
<summary>Backend 코드 구조</summary>
  
  ```
  src
  └── main
      └── java
          └── com.grepp.nbe1_2_team09
              ├── admin
              │   ├── config
              │   │   ├── RedisConfig.java
              │   │   └── SecurityConfig.java
              │   ├── dto
              │   │   └── CustomUserInfoDTO.java
              │   ├── jwt
              │   │   ├── CookieUtil.java
              │   │   ├── JwtFilter.java
              │   │   └── JwtUtil.java
              │   ├── redis
              │   │   ├── entity
              │   │   └── repository
              │   └── service
              │       ├── oauth2
              │       ├── CustomUserDetails.java
              │       └── CustomUserDetailsService.java
              │
              ├── common
              │   ├── config
              │   │   ├── OpenAiConfig.java
              │   │   ├── RestTemplateConfig.java
              │   │   ├── SwaggerConfig.java
              │   │   └── WebSocketConfig.java
              │   ├── exception
              │   │   ├── BaseException.java
              │   │   ├── ErrorResponse.java
              │   │   ├── ExceptionMessage.java
              │   │   └── GlobalExceptionHandler.java
              │   └── util
              │       ├── aop
              │       │   ├── LogAspect.java
              │       │   └── LogExecutionTime.java
              │       └── TranslationUtil.java
              │
              ├── controller
              │   ├── chatBot
              │   │   ├── ChatBotController.java
              │   │   └── dto
              │   ├── city
              │   │   ├── CityApiController.java
              │   │   └── dto
              │   ├── event
              │   │   ├── EventController.java
              │   │   ├── EventLocationController.java
              │   │   └── dto
              │   ├── finance
              │   │   ├── AccountBookController.java
              │   │   ├── ExchangeRateController.java
              │   │   └── dto
              │   ├── group
              │   │   ├── GroupController.java
              │   │   └── dto
              │   ├── location
              │   │   ├── LocationApiController.java
              │   │   └── LocationController.java
              │   ├── user
              │   │   ├── UserController.java
              │   │   └── dto
              │   └── weather
              │       ├── WeatherController.java
              │       └── dto
              │   
              ├── domain
              │   ├── entity
              │   │   ├── event
              │   │   │   ├── Event.java
              │   │   │   ├── EventLocation.java
              │   │   │   └── EventStatus.java
              │   │   ├── group
              │   │   │   ├── invitation
              │   │   │   │   ├── GroupInvitation.java
              │   │   │   │   └── InvitationStatus.java
              │   │   │   ├── Group.java
              │   │   │   ├── GroupMembership.java
              │   │   │   ├── GroupRole.java
              │   │   │   └── GroupStatus.java
              │   │   ├── user
              │   │   │   ├── OAuthProvider.java
              │   │   │   ├── Role.java
              │   │   │   └── User.java
              │   │   ├── ExchangeRate.java
              │   │   ├── Expense.java
              │   │   ├── Location.java
              │   │   ├── LocationType.java
              │   │   ├── Route.java
              │   │   └── Task.java
              │   │
              │   ├── repository
              │   │   ├── event
              │   │   │   ├── eventrepo
              │   │   │   │   ├── EventRepository.java
              │   │   │   │   ├── EventRepositoryCustom.java
              │   │   │   │   └── EventRepositoryImpl.java
              │   │   │   └── EventLocationRepository.java
              │   │   ├── finance
              │   │   │   ├── AccountBookRepository.java
              │   │   │   └── ExchangeRateRepository.java
              │   │   ├── group
              │   │   │   ├── membership
              │   │   │   │   ├── GroupMembershipRepository.java
              │   │   │   │   ├── GroupMembershipRepositoryCustom.java
              │   │   │   │   └── GroupMembershipRepositoryImpl.java
              │   │   │   ├── GroupInvitationRepository.java
              │   │   │   └── GroupRepository.java
              │   │   ├── location
              │   │   │   └── LocationRepository.java
              │   │   └── user
              │   │       └── UserRepository.java
              │   │
              │   └── service
              │       ├── chatBot
              │       │   └── ChatBotService.java
              │       ├── city
              │       │   └── CityApiService.java
              │       ├── event
              │       │   ├── EventLocation.java
              │       │   └── EventService.java
              │       ├── finance
              │       │   ├── AccountBookService.java
              │       │   ├── ExchangeRateService.java
              │       │   └── OCRService.java
              │       ├── group
              │       │   └── GroupService.java
              │       ├── location
              │       │   ├── LocationApiService.java
              │       │   └── LocationService.java
              │       ├── user
              │       │   └── UserService.java
              │       └── weather
              │           └── WeatherService.java
              │
              ├── notification
              │   ├── controller
              │   │   ├── NotificationController.java
              │   │   └── dto
              │   ├── entity
              │   │   └── Notification.java
              │   ├── repopsitory
              │   │   └── NotificationRepository.java
              │   └── service
              │       └── NotificationService.java
              │
              ├── schedule.controller
              │   ├── dto
              │   └── ScheduleController.java
              │
              └── Nbe12Team09Application.java
  └── test
      └── java
          └── com.grepp.nbe1_2_team09
              ├── domain.service
              │   ├── EventServiceTest.java
              │   ├── AccountBookServiceTest.java
              │   ├── OCRServiceTest.java
              │   ├── GroupServiceTest.java
              │   └── LocationServiceTest.java
              └── Nbe12Team09ApplicationTests.java
  ```
</details>
<details>
<summary>Frontend 코드 구조</summary>
  
  ```
  
  ```
</details>

<br>

### Sequence Diagram
<details>
<summary>Sequence Diagram</summary>
  
  ```
  
  ```
</details>

<br>

### Figma 화면 설계
<a href="https://www.figma.com/design/zirndKAJsew3kCY0CvopmQ/Untitled?node-id=0-1&t=77nvrr545N2jRpjr-1">
  Figma - Voya9e 화면 설계
</a>

<br><br>

### ERD
![erd](https://github.com/user-attachments/assets/38841a4a-f8ce-43ed-9f32-d38deda3b99d)

<br>

## 4. 역할 분담

### 🍏 윤건우
- **회원 관리**
  - 회원 가입
  - 로그인
  - 회원 정보 수정
  - 회원 탈퇴
  - OAuth 2.0 카카오 소셜 로그인
- **인증/인가**
  - JWT, 쿠키를 활용한 AccessToken 관리
  - Redis를 활용한 RefreshToken 관리
- **날씨**
  - OpenWeather API(Geocoding, 5 Day/ 3 Hour Forecast)를 활용한 날씨 검색

<br>

### 🍋 김가현
- **가계부 관리**
    - 그룹 별로 가계부 삽입, 수정, 삭제, 읽기
    - Google Vision API를 활용한 영수증 OCR
    - open ai를 활용한 영수증 날짜 포맷팅
    - OCR과 날짜 포맷팅을 이용해 영수증 이미지로 효율적으로 가계부 삽입 가능
- **환율**
    - exchangerate API를 활용한 환율 변환 기능
    - 매일 00시에 자동으로 API 호출해서 환율 정보를 갱신하도록 함
- **챗봇**
    - open ai를 활용한 챗봇 기능
    - 최대 5개의 메시지를 저장해 대화가 이어지도록 함

<br>

### 🍒 이현섭
- **그룹**
    - 그룹 생성
    - 그룹 삭제
    - 그룹 이름 변경
    - 그룹에 멤버 초대
        - WEBSOCKET을 활용한 비동기 통신 초대 메세지 구현
        - 초대 수락/거절 여부 알림 구현

<br>

### 🍊 정지수
- **일정 추가하기**
    - 일정 추가를 위한 날짜 검색, 도시 검색 기능
- 일정표
    - WebSoekct을 활용한 공동 일정 관리 (엑셀형식)
    - 일정표에 장소 추가하기
    - google api
        - 도시 검색, 나라별 도시 검색, 장소 검
        - 상세보기
        - 지도
    - 지도에 마커 경로 이어주기
        - 각 날짜마다 장소들을 시간 순서대로 마커 경로 이어주기
