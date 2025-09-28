package com.example.demo;

  import org.springframework.boot.CommandLineRunner;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.context.annotation.Bean;

  @SpringBootApplication
  public class DemoApplication {

      // 프로그램의 시작점
      public static void main(String[] args) {
          SpringApplication.run(DemoApplication.class, args);
      }

      // 애플리케이션이 시작될 때 DB에 테스트용 데이터를 자동으로 추가하는 부분
      @Bean
      public CommandLineRunner demoData(UserRepository userRepository) {
          return args -> {
              // DB에 데이터가 없을 때만 실행
              if (userRepository.count() == 0) {
                  User user1 = new User();
                  user1.setName("김용준"); // 샘플 데이터 1
                  user1.setEmail("yongjun@example.com");
                  userRepository.save(user1);

                  User user2 = new User();
                  user2.setName("제미니"); // 샘플 데이터 2
                  user2.setEmail("gemini@google.com");
                  userRepository.save(user2);
              }
          };
      }
  }