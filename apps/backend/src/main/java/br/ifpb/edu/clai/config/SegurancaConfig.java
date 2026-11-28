package br.ifpb.edu.clai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SegurancaConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/css/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()

                .requestMatchers(
                    "/usuarios",
                    "/usuarios/**"
                ).denyAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/alunos/novo",
                    "/alunos/{id}/editar",
                    "/estudos-de-caso/novo",
                    "/estudos-de-caso/{id}/editar",
                    "/peis/novo",
                    "/peis/{id}/editar",
                    "/peas/novo",
                    "/peas/{id}/editar",
                    "/planos-acao/novo",
                    "/planos-acao/{id}/editar",
                    "/relatorios-observacao/novo",
                    "/relatorios-observacao/{id}/editar",
                    "/agendamentos/novo",
                    "/agendamentos/{id}/editar",
                    "/agendamentos/{id}/finalizar"
                ).hasRole("EDICAO")

                .requestMatchers(
                    HttpMethod.POST,
                    "/alunos/**",
                    "/estudos-de-caso/**",
                    "/peis/**",
                    "/peas/**",
                    "/planos-acao/**",
                    "/relatorios-observacao/**",
                    "/agendamentos/**"
                ).hasRole("EDICAO")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/alunos/**",
                    "/estudos-de-caso/**",
                    "/peis/**",
                    "/peas/**",
                    "/planos-acao/**",
                    "/relatorios-observacao/**",
                    "/agendamentos/**").hasRole("EDICAO")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/alunos/**",
                    "/estudos-de-caso/**",
                    "/peis/**",
                    "/peas/**",
                    "/planos-acao/**",
                    "/relatorios-observacao/**",
                    "/agendamentos/**").hasRole("EDICAO")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/alunos/**",
                    "/estudos-de-caso/**",
                    "/peis/**",
                    "/peas/**",
                    "/planos-acao/**",
                    "/relatorios-observacao/**",
                    "/agendamentos/**").hasRole("EDICAO")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
