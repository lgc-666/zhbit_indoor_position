package zhbit.za102.Filter;


import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.springframework.cache.annotation.CacheEvict;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class ShiroLogoutFilter extends LogoutFilter {
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        logout(request,response);
        return false;
    }

    public void logout(ServletRequest request, ServletResponse response){
        Subject subject = getSubject(request,response);
        subject.logout();
    }

}
