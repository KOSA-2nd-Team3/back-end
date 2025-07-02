# JDK 21 기반 경량 이미지 사용 (alpine: 가볍고 빠름)
FROM eclipse-temurin:21-jdk-jammy

# (선택) 타임존 세팅 - Asia/Seoul로 맞추고 싶을 때
ENV TZ=Asia/Seoul

# (선택) UTF-8 로케일 설정
ENV LANG=C.UTF-8

# 임시 디렉토리 마운트 (스프링 부트 표준)
VOLUME /tmp

# 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 앱 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
