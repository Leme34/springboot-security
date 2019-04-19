package com.lee.config.security;

import com.lee.common.SecurityConstants;
import com.lee.filter.ValidateCodeFilter;
import com.lee.properties.MySecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

@EnableConfigurationProperties(MySecurityProperties.class)  //读取自定义配置
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //启用@PreAuthorize注解方式校验权限
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //用于“记住我”功能生成token的密钥
    private static final String KEY = "com.lee.security";

    //spring security提供的UserDetailsService
    //在UserServiceImpl中实现他(用户信息获取服务)的接口loadUserByUsername
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private MyAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private MyLogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private MyInvalidSessionStrategy invalidSessionStrategy;
    @Autowired
    private MyExpiredSessionStrategy expiredSessionStrategy;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private MySecurityProperties securityProperties;
    @Autowired
    private ValidateCodeFilter validateCodeFilter;
    @Autowired
    private MyAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private SpringSocialConfigurer springSocialConfigurer;
    @Autowired
    private MyAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SessionStrategy sessionStrategy() {
        return new HttpSessionSessionStrategy();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用提供的 BCrypt 加密
        return new BCryptPasswordEncoder();
    }


    /**
     * 配置"记住我"功能的token存取器（持久化token）
     * 当登录请求参数中有"remember-me"=true，且成功登录后，会生成token写入浏览器cookie 并 存入数据表
     * 当再次登录时会根据username从浏览器cookie得到token查询数据库的用户信息，进行认证
     * 退出登录时会删除数据表中的对应记录
     * 若不配置自己实现的persistentTokenRepository，重启服务器后无法实现"记住我"
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 系统在启动时自动在当前数据库创建"persistent_logins"表,生成一次后再次启动应用一定要注释掉
//         tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }


    //定义授权规则(权限)
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); //禁用CSRF

        // 将 SocialAuthenticationFilter 添加到Spring Security的过滤器链的配置器，实现第三方登录
        // 所有"/auth"开头的url都会被 SocialAuthenticationFilter 拦截，详见源码的DEFAULT_FILTER_PROCESSES_URL
        // 不同的第三方登录url规则："/auth/${QQAutoConfig的createConnectionFactory方法的providerId}"
        // 例如"/auth/qq"就是请求qq第三方登录
        http.apply(springSocialConfigurer);

        http
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)  //在UsernamePasswordAuthenticationFilter之前加入验证码过滤器

                .formLogin()   //基于 Form 表单登录验证
                //需要认证时security跳转的url,若配置了authenticationEntryPoint 会首先进入authenticationEntryPoint处理
                .loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
                //自定义提交登录表单的action (form内容会给UsernamePasswordAuthenticationFilter认证)
                .loginProcessingUrl("/authentication/form")
//                .defaultSuccessUrl("",true)  //认证成功后总是跳转此url
                .successHandler(authenticationSuccessHandler)  //认证成功后使用注入的自定义SavedRequestAwareAuthenticationSuccessHandler的子类处理
//                .failureUrl("/login-error") // 认证失败跳转的页面,会交给failureHandler处理
                .failureHandler(authenticationFailureHandler)  //认证失败后使用注入的自定义SimpleUrlAuthenticationFailureHandler的子类处理


                .and()
                .logout()
                .logoutUrl("/signOut")  //登出表单提交的action
                .logoutSuccessHandler(logoutSuccessHandler)  //登出成功后的处理
//                .logoutSuccessUrl("/index")  //logoutSuccessHandler处理完后跳转的url
                .deleteCookies("JSESSIONID")  //删除用户cookie

                //自定义记住我功能:根据从浏览器cookie得到的token根据密钥与数据库中的token比对，进行认证
                .and()
                .rememberMe() // 启用 remember me
                .key(KEY)     // 用于生成token的密钥
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())  //记住我有效时间
                .userDetailsService(userDetailsService)  //记住我认证器根据cookie中的token查询用户信息时需要调用userDetailsService的loadUserByUsername()


                .and()
                .sessionManagement()                            //session超时时间在配置文件配置
                .invalidSessionStrategy(invalidSessionStrategy) //当session失效时的操作
//                .invalidSessionUrl(SecurityConstants.DEFAULT_SESSION_INVALID_URL)  //invalidSessionStrategy处理完后跳转的url
//                .maximumSessions(1)   //同一用户session的最大数量
//                .expiredSessionStrategy(expiredSessionStrategy)   //场景1：(允许把已登录用户踢下线)同一用户旧的session替换为新的session时的操作
//                .expiredUrl("/session/invalid") //expiredSessionStrategy处理完后跳转的url
//                .maxSessionsPreventsLogin(true)  //场景2：当同一用户session数量达到最大时阻止用户登录(同一用户只能在1处登录)

//                .and()

                .and()
                .authorizeRequests()
                .antMatchers("/authentication/form",
                        SecurityConstants.DEFAULT_SESSION_INVALID_URL,
                        "/register", SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
                        "/code/image",
                        securityProperties.getBrowser().getLoginPage(),
                        securityProperties.getBrowser().getSignUpUrl(),
                        "/social/signUp")
                .permitAll() // 都可以访问
//                .antMatchers("/users/**").hasAnyRole("ADMIN","USER");  //需要角色才能访问,框架会转换成ROLE_USER和ROLE_ADMIN进行验证
//                .antMatchers(HttpMethod.POST,"/users/**").hasAnyRole("USER","ADMIN")  //POST请求需要角色才能访问
                .anyRequest().authenticated() //剩下没有配置的url都需要登录认证才能访问


                .and()
                .exceptionHandling()
//                .accessDeniedPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)  // blogAccessDeniedHandler处理后重定向到 403 页面
                .accessDeniedHandler(accessDeniedHandler) // 由自定义AccessDeniedHandler处理权限被拒绝异常
                .authenticationEntryPoint(authenticationEntryPoint);  //用来解决匿名用户访问无权限资源时的异常

    }

    //用户认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //设置认证提供者
        auth.authenticationProvider(authenticationProvider());
        //自定义的UserDetailsService提供自定义用户信息获取服务
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * AuthenticationProvider 提供用户UserDetails的具体验证方式，
     * 在其中可以自定义用户密码的加密、验证方式
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // 通过dao层提供验证
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        // 设置密码加密方式,用于登录时的密码比对
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

}
