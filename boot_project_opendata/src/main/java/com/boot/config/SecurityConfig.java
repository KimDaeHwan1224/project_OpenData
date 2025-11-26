package com.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(
                    "/", "/main",
                    "/login", "/login_yn",
                    "/register", "/register_ok",
                    "/checkId", "/checkEmail",
                    "/mail/**",
                    "/css/**", "/js/**", "/img/**",
                    "/api/**",
                    "/oauth/**",
                    "/admin/login", "/admin/login_yn"
                ).permitAll()
                .antMatchers("/admin/logout").permitAll()
                .antMatchers("/admin/**").authenticated()

                .anyRequest().permitAll()
            .and()

            // 🔥 반드시 추가해야 remember-me 자동 구성 비활성화됨
            .rememberMe().disable()

            // 일반 로그인 Security 비활성화
            .formLogin().disable()
            .httpBasic().disable()

            // 로그아웃 (일반 사용자만)
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
