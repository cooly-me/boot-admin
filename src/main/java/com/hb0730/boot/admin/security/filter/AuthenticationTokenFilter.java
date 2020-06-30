package com.hb0730.boot.admin.security.filter;

import com.hb0730.boot.admin.commons.utils.spring.SecurityUtils;
import com.hb0730.boot.admin.configuration.properties.BootAdminProperties;
import com.hb0730.boot.admin.security.model.LoginUser;
import com.hb0730.boot.admin.security.service.ITokenService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * <p>
 * token过滤器 验证token有效性
 * </P>
 *
 * @author bing_huang
 * @since V1.0
 */
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    /**
     * token service
     */
    private final ITokenService tokenService;
    /**
     * token 配置
     */
    private final BootAdminProperties properties;

    public AuthenticationTokenFilter(ITokenService tokenService, BootAdminProperties properties) {
        this.tokenService = tokenService;
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (!Objects.isNull(loginUser) && StringUtils.isEmpty(SecurityUtils.getAuthentication())) {
            tokenService.verifyAccessToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
