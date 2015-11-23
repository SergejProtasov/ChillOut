package web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        if(session!=null){
            SessionBean userAutorisationBean = (SessionBean) session.getAttribute("sessionBean");
            if(userAutorisationBean!=null){
                if(userAutorisationBean.searchUser()){
                    chain.doFilter(request, response);
                }else{
                    httpResponse.sendRedirect(httpRequest.getContextPath()+"/index.xhtml");
                }
            }else{
                httpResponse.sendRedirect(httpRequest.getContextPath()+"/index.xhtml");
            }
        }
        else {
            httpResponse.sendRedirect(httpRequest.getContextPath()+"/index.xhtml");
        }
    }

    @Override
    public void destroy() {

    }
}
