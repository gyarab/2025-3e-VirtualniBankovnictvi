package cz.gyarabProject.api.cs;

import cz.gyarabProject.api.cs.datatype.Token;
import cz.gyarabProject.database.entity.AccountId;
import cz.gyarabProject.database.repository.AccountIdRepository;
import cz.gyarabProject.database.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class OAuth2TokenInterceptor implements HandlerInterceptor {
    private final OAuth2 oAuth2;
    private final UserRepository user;

    public OAuth2TokenInterceptor(OAuth2 oAuth2, UserRepository user) {
        this.oAuth2 = oAuth2;
        this.user = user;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Long id = (Long) request.getSession().getAttribute("userId");
        Token token = user.getUserById(id).getTokenCS();
        if (!token.isRefreshValid()) {
            response.sendRedirect(oAuth2.authUri(id + " " + request.getRequestURL().toString()));
            return false;
        } else if (!token.isValid()) {
             return oAuth2.newAccessToken(id);
        }
        return true;
    }
}
