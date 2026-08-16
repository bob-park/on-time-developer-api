# OnTime Developer API
이 repository 는 Spring Boot 기반의 프로젝트를 시작하기 위한 템플릿입니다. 이 템플릿은 기본적인 구조와 설정을 포함하고 있어, 새로운 프로젝트를 빠르게 시작할 수 있도록 도와줍니다. 

## dependency
- java 25
- malgn-spring-boot-starter
- spring boot 4
- spring modulith
- jpa
- querydsl
- OAuth 2.0 (KeyFlow OAuth 2.0 Authorization Server 연동)
- p6spy
- postgresql jdbc

## Architecture
- Domain Model Pattern
- Hexagonal Architecture

## Project Java Version 관리
기본적으로 `sdkman` 으로 Project 의 Java Version 을 관리하고 있습니다. 



## dir 구조
```text
├── docs
│   ├── agents          # agent(AI) 용 프로젝트 가이드 문서 (AGENTS.md 의 Map 에서 참조)
│   ├── apis            # OpenAPI 3 Spec
│   ├── domain          # domain 문서 (glossary.md, mermaid diagram)
│   └── superpowers     # superpowers skill 산출물
│       ├── specs       # 설계 spec (YYYY-MM-DD-<topic>-design.md)
│       └── plans       # 구현 plan (YYYY-MM-DD-<topic>.md)
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── malgn
    │   │           ├── Application.java
    │   │           ├── config                    # application 전역 설정 (feign / jpa / security / web mvc)
    │   │           │   ├── feign
    │   │           │   │   └── decoder
    │   │           │   ├── jpa
    │   │           │   └── security
    │   │           │       ├── converter
    │   │           │       └── manager
    │   │           └── {module}                  # Spring Modulith module (예: storages)
    │   │               ├── adapter
    │   │               │   ├── in
    │   │               │   │   └── web
    │   │               │   │       ├── api
    │   │               │   │       │   └── v1    # {Name}ApiV1 + nested {Parent}{Child}ApiV1 — version 별 단일 package
    │   │               │   │       │       └── dto  # 해당 version controller 들이 공유하는 Request/Response DTO
    │   │               │   │       └── view      # server-rendered {Feature}View controller
    │   │               │   │           └── dto   # form / view-model record
    │   │               │   └── out
    │   │               │       ├── persistence
    │   │               │       │   └── jpa       # {Name}JpaRepository / {Name}JpaRepositoryAdapter
    │   │               │       │       └── query # {Name}JpaQueryRepository (+ query/impl 에 QueryDSL 구현)
    │   │               │       └── {system}      # 기타 outbound adapter (예: aws.s3, aws.cloudfront)
    │   │               ├── application
    │   │               │   ├── provided          # inbound ports: {Name}Query / {Name}Register / {Name}Editor
    │   │               │   │   └── model         # {Name}Result / {Name}{Action}Command / {Name}QueryCriteria
    │   │               │   ├── required          # outbound ports: {Name}Repository
    │   │               │   │   └── model         # {Name}QueryCondition / 기타 outbound request model
    │   │               │   └── service           # {Name}QueryService / {Name}CommandService
    │   │               ├── config                # module 전용 @Configuration + {Name}ExceptionHandler
    │   │               │   └── properties        # @ConfigurationProperties class
    │   │               └── domain                # domain model (domain model 이 필요한 module 에만 존재)
    │   │                   └── events            # domain event record (@NamedInterface("domain-events"))
    │   └── resources
    │       ├── application.yml
    │       └── db
    │           └── migration                     # flyway migration script
    └── test
        └── java
            └── com
                └── malgn                         # ApplicationTests / ApplicationModularityTests
```

* `docs`: 해당 프로젝트에서 사용하는 문서
  * `agents`: agent(AI) 용 프로젝트 가이드 문서. 상세 규칙은 `AGENTS.md` 의 Map 에서 참조한다.
  * `apis`: API Docs 로 `OpenAPI 3` Spec 을 따름
  * `domain`: 해당 프로젝트에서 사용하는 domain 에 대한 문서
    * entity 관계는 Class Diagram + Entity Relationship Diagram 등 이해할 수 있는 Diagram 을 포함해야한다. 
    * Diagram 표현은 `mermaid` 로 한다. (github 에서 바로 rendering 됨)
    * `glossary.md`: 해당 프로젝트에서 사용하는 용어집
  * `superpowers`: superpowers skill 산출물 (`specs`: 설계 spec, `plans`: 구현 plan)


최상위 package 는 `config` 와 module package 들로 구성된다. (Spring Modulith)

* `Application.java`: Spring Boot entry point
* `config`: application 전역 설정 (Security/OAuth2, JPA, Feign, Web MVC 등). 비즈니스 로직 없음.
* `{module}` (예: `storages`): Spring Modulith 의 module (bounded context). 각 module 내부는 hexagonal 구조를 따른다.
  * `adapter`
    * `in.web.api.v1`: inbound adapter — REST API controller (`{Name}ApiV1`) / DTO. version 별 단일 package (`v1`, `v2`, ...)
    * `in.web.view`: inbound adapter — server-rendered view controller (`{Feature}View`) / form · view-model DTO
    * `out.persistence.jpa`: outbound adapter — JPA persistence (`{Name}JpaRepository` / `{Name}JpaRepositoryAdapter`, `query` 하위에 QueryDSL 기반 `{Name}JpaQueryRepository`)
    * `out.{system}`: outbound adapter — 기타 `required` port 구현체 (예: `aws.s3`, `aws.cloudfront`)
  * `application`
    * `provided`: module 이 외부에 제공하는 inbound ports (+ `model`: command / result / search criteria)
    * `required`: module 이 필요로 하는 outbound ports 로, outbound adapter 가 구현 (+ `model`: query condition / request)
    * `service`: `provided` port 를 구현한 application service (`{Name}QueryService` / `{Name}CommandService`)
  * `config`: module 전용 설정 + `{Name}ExceptionHandler` (+ `properties`: `@ConfigurationProperties`)
  * `domain`: module 의 domain model (domain model 이 필요한 module 에만 존재)
    * `events`: cross-module 통신용 domain event record (`@NamedInterface("domain-events")`)


## 주의사항
* 반드시, project name 을 비롯한 모든 module name 변경할 것
* 빌드시 api docs 는 `${build dir}/resources/statics/docs/apis` 에 복사되어 빌드 후 static resource 로 다운로드 가능해짐
  * `swagger-ui` 문서 통합용 

## `PR` Merge 시 자동 version up 기능
`github workflows` + `github actions` 로 PR 시 자동으로 version up 을 진행한다.

### version pattern
[major].[minor].[patch]-rc[index]-[yyyyMMdd]

#### major
PR 제목에 `xxx [major]` 인 경우 major 버전이 `+1` 된다. 

#### minor
PR 제목에 `xxx [minor]` 인 경우 minor 버전이 `+1` 된다.

#### patch
PR 제목에 `xxx` 인 경우 patch 버전이 `+1` 된다. 단, 같은 날인 경우 rc[index] 가 `+1` 된다.


## build
기본적으로 docker container 로 빌드한다. 
multi-arch 도 지원해야하므로, `docker buildx bake` 를 사용하여 build 한다.
반드시 `docker-compose.yml` 를 수정하여, 올바른 image 와 tag 를 사용할 수 있도록 해야한다.

```bash
# 기본적으로 build.gradle 에 정의된 version 을 사용한다.
VERSION=$(./gradlew properties -q | grep "^version:" | awk '{print $2}') docker buildx bake -f docker-compose.yml --push --provenance false
```



## `Feign Client` 분리
내부 모듈간 REST API 요청시 인증 문제로 데이터 수신이 어려울 수 있는데, 다음과 같이 Feign Client Configuration 을 분리하여 사용하면 그 문제를 해결 할 수 있다.

### Configuration 분리
- HTTP Request Thread 요청시 Feign Client (SecurityContext) 
- System 요청시 Feign Client (OAuth2AuthorizedClientManager) 



#### HTTP Request Thread 요청시 Feign Client (SecurityContext)
- `FeignConfiguration`
- HTTP Request 의 SecurityContext 를 사용하여 인증 처리한다.

#### System 요청시 Feign Client (OAuth2AuthorizedClientManager)
- `FeignSystemConfiguration`
- OAuth2AuthorizedClientManager 를 사용하여 인증 처리한다.
  - 단, 내부 모듈 통신시 반드시 `client_credentials` 인증 방식을 사용해야 한다.

### 사용 예시

#### HTTP Request 의 SecurityContext 사용 Feign Client
```java
@FeignClient(name ="system-api", contextId ="system-api", path ="/api/v1/system",
    configuration =FeignConfiguration.class)
public interface HttpFeignClient {
  ...
}
```


#### System 내부 모듈간 통신용 Feign Client
```java
@FeignClient(name = "system-api", contextId = "system-api", path = "/api/v1/system", configuration = FeignSystemConfiguration.class)
public interface SystemFeignClient {
  ...
}
```



## `StorageManager`
저장된 파일(asset) 을 client 가 다운로드/조회할 수 있는 **서명된(signed) url** 을 발급하기 위한 port 이다.

- `storage.aws.type` 값에 따라 `AwsS3StorageManager` 또는 `AwsCloudFrontStorageManager` 가 선택되어 `Bean` 으로 등록된다.
  - `s3_presigned_url` (default): `AwsS3StorageManager` — S3 presigned url 발급
  - `cloudfront_signed_url`: `AwsCloudFrontStorageManager` — CloudFront signed url 발급
- url 발급 시 다음을 설정할 수 있다.
  - `customFilename`: 다운로드될 파일 이름. 미지정 시 object key 의 파일명을 사용한다. (presigned url 의 `Content-Disposition: attachment; filename=...` 으로 지정됨)
  - `duration`: url 만료 시간. 미지정 시 기본값 `10m`(`StorageManager.DEFAULT_EXPIRATION`) 을 사용한다.
- 그 외 `getFileMeta(key)` 로 object 의 메타데이터(`AssetFileMeta`: key / contentLength / lastModifiedDate) 를 조회할 수 있다.

> ⚠️ `cloudfront_signed_url` (`AwsCloudFrontStorageManager`) 은 **현재 구현 준비중** 이다. 운영에서는 `s3_presigned_url` 을 사용한다.

### `application.yml`
```yaml
...

storage:
  aws:
    type: s3_presigned_url # default. (s3_presigned_url | cloudfront_signed_url)
    s3:
      bucket: templates
      
      # type 이 cloudfront_signed_url 인 경우 (구현 준비중)
      cloudfront:
        distribution-domain: https://cdn.example.com

...
```

### `StorageManager` 사용
`generateUrl(...)` 로 서명된 url 을 발급받아 client 에게 전달한다.

```java
String generateUrl(String key, String customFilename, Duration duration);

// 아래는 default 메서드로, 생략된 인자는 기본값을 사용한다.
String generateUrl(String key);                          // customFilename: null, duration: 10m
String generateUrl(String key, Duration duration);       // customFilename: null
String generateUrl(String key, String customFilename);   // duration: 10m
```

```java
@RequiredArgsConstructor
@Service
public class TemplateService {

    ...
    private final StorageManager storageManager;

    ...

    public void getAsset(...) {

        ...

        String key = asset.getFile().getKey();

        // 기본 만료시간(10m) 으로 url 발급
        String url = storageManager.generateUrl(key);

        // 다운로드 파일명 / 만료시간을 직접 지정할 수도 있다.
        // String url = storageManager.generateUrl(key, "report.pdf", Duration.ofMinutes(30));

        ...

    }

}
```

### `Bean` 등록
storage 연동 설정(`StorageConfiguration`) 에서 `storage.aws.type` 에 따라 `StorageManager` 구현체를 bean 으로 등록한다.

```java
public class StorageConfiguration {

    ...

    @Value("${storage.aws.s3.bucket}")
    private String bucket;

    ...

    /**
     * app.aws.cdn.type = s3_presigned_url (default)
     */
    @Bean
    public StorageManager awsS3StorageManager(S3Client s3Client, S3Presigner s3Presigner) {
        return new AwsS3StorageManager(bucketName, s3Client, s3Presigner);
    }

    ...

}
```


## `Upload Manager`
파일 업로드를 위한 port 로, 다음 두 가지를 제공한다.

- `UploadManager`: 단일 파일 업로드용
- `MultipartUploadManager`: 대용량 파일을 여러 part 로 나누어 업로드하는 multipart 업로드용

두 manager 모두 **서버가 직접 파일을 업로드하지 않는다.** 서버는 `presigned url` 만 발급(반환)하고,
실제 업로드는 발급된 url 로 **client 에서 직접(direct upload)** 수행하는 방식이다.
따라서 파일 stream 이 서버를 거치지 않으므로 서버 부하 없이 업로드를 처리할 수 있다.

기본 구현체로 AWS S3 기반의 `AwsS3UploadManager` / `AwsS3MultipartUploadManager` 를 제공한다.
(필요시 추가 구현체를 작성하여 다른 storage 로 교체할 수 있다.)

### `Bean` 등록
storage 연동 설정(`StorageCOnfiguration` 등) 에서 bean 으로 등록한다.

```java
public class StorageCOnfiguration {

    ...

    @Value("${storage.aws.s3.bucket}")
    private String bucketName;

    ...

    /**
     * aws s3 upload manager
     */
    @Bean
    public UploadManager awsS3UploadManager(S3Client s3Client, S3Presigner s3Presigner) {
        return new AwsS3UploadManager(bucketName, s3Client, s3Presigner);
    }

    /**
     * aws s3 multipart upload manager
     */
    @Bean
    public MultipartUploadManager awsS3MultipartUploadManager(S3Client s3Client, S3Presigner s3Presigner) {
        return new AwsS3MultipartUploadManager(bucketName, s3Client, s3Presigner);
    }

    ...

}
```

### `UploadManager` 사용 (단일 업로드)
`generatedPresignedUrl(UploadRequest)` 로 presigned url 을 발급받아 client 에게 전달하면, client 가 해당 url 로 직접 업로드한다.

- `key`: 업로드 대상 object key (필수)
- `expiration`: presigned url 만료 시간 (default `15m`)

```java
@RequiredArgsConstructor
@Service
public class UploadService {

    ...
    private final UploadManager uploadManager;

    ...

    public void upload(...) {
        ...

        String presignedUrl =
            uploadManager.generatedPresignedUrl(
                UploadRequest.builder()
                    .key(key)
                    .build());

        // client 는 presignedUrl 로 직접 업로드한다.
        ...
    }

}
```

### `MultipartUploadManager` 사용 (multipart 업로드)
대용량 파일을 part 단위로 나누어 업로드한다. part 별로 presigned url 을 발급받아 client 가 직접 업로드하고,
업로드 완료 시 반환되는 `eTag` 들을 모아 최종적으로 multipart 업로드를 완료한다.

- `generateUploadId(key)`: multipart 업로드 식별자(`uploadId`) 발급
- `generatedPresignedUrl(UploadMultiPartRequest)`: part 별 presigned url 발급
  - `key`: object key (필수)
  - `uploadId`: 발급받은 upload id (필수)
  - `partNumber`: 업로드할 part 번호 (필수)
  - `uploadLength`: 전체 파일 크기 (필수)
  - `expiration`: presigned url 만료 시간 (default `15m`)
- `completedUpload(key, uploadId, parts)`: 모든 part 업로드 완료 후 multipart 업로드 완료 처리

```java
@RequiredArgsConstructor
@Service
public class UploadService {

    ...
    private final MultipartUploadManager multipartUploadManager;

    ...

    public void multipartUpload(...) {

        List<UploadCompletedPart> parts = Lists.newArrayList();

        // 1. upload id 발급
        String uploadId = multipartUploadManager.generateUploadId(key);

        // 2. part 별 presigned url 발급
        Duration expiration = Duration.ofHours(1);

        String presignedPartUrl =
            multipartUploadManager.generatedPresignedUrl(
                UploadMultiPartRequest.builder()
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)       // part 번호
                    .uploadLength(fileSize)        // total file size
                    .expiration(expiration)        // default 15m
                    .build());

        // 3. client 에서 presigned part url 로 직접 업로드 & 완료 후 반환되는 eTag 저장

        // 4. 업로드 완료된 part 정보 추가
        parts.add(
            UploadCompletedPart.builder()
                .eTag(eTag)
                .partNumber(partNumber)            // part 번호
                .build());

        // 5. multipart 업로드 완료
        multipartUploadManager.completedUpload(key, uploadId, parts);

        ...
    }

}
```


## `Circuitbreaker` 사용하기 (feat. openfeign + resilience4j + redis cache)
- MSA 환경에서 안정적인 서비스를 하기 위해 circuit breaker 를 사용한다.
- 사용 예시는 아래와 같다. 
  - 예시는 `template-malgn-spring-modulith` 에 있다.


### 예시 - UserClient

#### 시나리오
- 구성
  - `UserClient` 중 `getUser(..)` 에 `CircuitBreaker` 적용
- 시나리오
> 1. `authorization-server` 가 정상적으로 구동하고 있는 경우
>   - `getUser(..)` 가 응답이 `200` 인 경우 `CacheStore` 에 저장 
> 2. `authorization-server` 가 비정상인 경우
>   - `fallback` 메서드 호출
>   - `fallback` 메서드에서 `CacheStore` 에서 조회 후 반환

```yaml
# application.yml
...
# malgn
malgn:
  redis:
    enabled: on # CacheStore 가 같이 활성화
    host: redis://localhost:6379
...
```

```java
// UserClientAdapter
public class UserFeignClientAdapter {

  private static final String KEY_USER_SUMMARY = "users:%d:summary";

  private final UserFeignClient userClient;

  private final CacheStore cacheStore;

  @CircuitBreaker(name = "userClient", fallbackMethod = "getUserFallback")
  @Override
  public UserSummary getUser(Long userUniqueId) {
    UserSummary user = userClient.getUser(userUniqueId);

    cacheStore.put(getCacheKey(userUniqueId), user);

    return user;
  }

  private UserSummary getUserFallback(Long userUniqueId, Throwable throwable) {
    return cacheStore.get(getCacheKey(userUniqueId), UserSummary.class)
            .orElseThrow(() -> new ServiceRuntimeException(throwable.getMessage()));
  }

  private String getCacheKey(Long id) {
    return KEY_USER_SUMMARY.formatted(id);
  }
    
}

```


## OpenFGA (권한 체크)
`malgn-spring-boot-starter` 는 [OpenFGA](https://openfga.dev/) 기반의 권한 체크(ReBAC) 기능을 제공한다.

- 반드시 `malgn.auth.openfga.enabled` 가 `on` 이어야 활성화된다.
- `store-id`, `model-id` 는 필수 입력이다.
- `host` 미지정 시 기본값 `http://localhost:8080` 을 사용한다.

### `application.yml` 에 설정 추가
```yaml
malgn:
  auth:
    openfga:
      enabled: on
      host: ${openfga_host:http://localhost:8080}
      store-id: ${openfga_store_id}
      model-id: ${openfga_model_id}
```

### `RestController` 사용시
`@PreAuthorize(..)` 에서 `fga` bean 으로 권한 체크를 할 수 있다.

- `fga.check(objectType, objectId, relation)`
  - `objectType`: 권한을 확인하고자 하는 object 의 type ex) `user`
  - `objectId`: 권한을 확인하고자 하는 object 의 id ex) user 의 pk
  - `relation`: 사용자가 필요한 relation ex) 읽기 권한
- 3-arg `check` 는 현재 `SecurityContext` 의 인증 사용자를 userType `user`, userId `authentication.getName()` 으로 해석한다. (인증 정보가 없으면 `IllegalStateException` 발생)
- 오버로드: `check(objectType, objectId, RelationType relation)`, `check(objectType, objectId, relation, userType, userId)` — 다른 userType / userId 로 체크할 때 사용한다.

```java
...
    @PreAuthorize("@fga.check('user', #id, 'can_read')")
    @GetMapping(path = "{id:\\d+}")
    public UserResponseV1 getUser(@PathVariable long id) {
       ...
    }
...
```

### 수동으로 사용시
`AccessControl` 을 주입받아 직접 사용할 수 있다.

- `check(AccessControlQueryRequest)`: 권한 체크
- `listAccessible(AccessControlListQueryRequest)`: 접근 가능한 object id 목록 조회
- `write(List<AccessControlWriteRequest>)`: 권한(tuple) 추가
- `remove(List<AccessControlRemoveRequest>)`: 권한(tuple) 삭제

```java
...
    private final AccessControl accessControl;

    public boolean check(...) {
       ...

       boolean available =
           accessControl.check(
               AccessControlQueryRequest.builder()
                   .userType("user")
                   .userId("1")
                   .relation("can_read")
                   .objectType("user")
                   .objectId("2")
                   .build());

       ...
    }
...
```

### 참고 사항
> ⚠️ OpenFGA 를 사용하여 권한을 체크하는 리소스는 반드시 create + update + delete 시 OpenFGA 에도 tuple 을 반영(`write` / `remove`)해야 한다.

```java
...
    private final AccessControl accessControl;

    public void example(...) {

        accessControl.write(
            List.of(
                AccessControlWriteRequest.builder()
                    .userType("user")
                    .userId(String.valueOf(user.getId()))
                    .relation("owner")
                    .objectType("user")
                    .objectId(String.valueOf(user.getId()))
                    .build()));

        accessControl.remove(
            List.of(
                AccessControlRemoveRequest.builder()
                    .userType("user")
                    .userId(String.valueOf(user.getId()))
                    .relation("owner")
                    .objectType("user")
                    .objectId(String.valueOf(user.getId()))
                    .build()));
    }
...
```
