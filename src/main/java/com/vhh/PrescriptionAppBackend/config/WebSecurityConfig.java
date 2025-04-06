package com.vhh.PrescriptionAppBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vhh.PrescriptionAppBackend.filter.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests.requestMatchers("**").permitAll())
                .build();
    }

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         // Vô hiệu hóa CSRF vì chúng ta dùng token (stateless)
    //         .csrf(csrf -> csrf.disable())
    //         // Sử dụng chính sách session STATELESS vì dùng token
    //         .cors(Customizer.withDefaults())
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         // Cấu hình Resource Server để xử lý JWT
    //         .oauth2ResourceServer(oauth2 -> oauth2
    //             .jwt(Customizer.withDefaults()) // Sử dụng cấu hình JWT mặc định từ application.properties
    //             // Có thể thêm customizer ở đây nếu cần xử lý phức tạp hơn
    //             // .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
    //         )
    //         // Cấu hình phân quyền
    //         .authorizeHttpRequests(authz -> authz
    //             .requestMatchers("/public/**").permitAll() // Ví dụ: các endpoint public
    //             .requestMatchers("/api/auth/**").authenticated() // Endpoint yêu cầu token hợp lệ
    //             // .requestMatchers("/api/admin/**").hasAuthority("SCOPE_admin") // Ví dụ phân quyền theo scope (nếu có trong token)
    //             .anyRequest().authenticated() // Tất cả các request khác đều yêu cầu xác thực
    //         );

    //     return http.build();
    // }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/**")
    //                     .allowedOrigins("http://localhost:3000") // Hoặc frontend của bạn
    //                     .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //                     .allowedHeaders("*")
    //                     .allowCredentials(true);
    //         }
    //     };
    // }
}
