package ru.korundm.configuration.interceptor;

import lombok.extern.jbosslog.JBossLog;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * TODO временная тема, надо доработать
 * Перехватчик для логирования
 * @author pakhunov_an
 * Date:   06.02.2019
 */
@JBossLog
public final class LoggerInterceptor implements HandlerInterceptor {

    /**
     * Метод отрабатывает перед обработкой запроса в контроллере
     */
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object o
    ) {
        log.info("[preHandle][" + request + "]" + "[" + request.getMethod() + "]" + request.getRequestURI() + getParameters(request));
        return true;
    }

    /**
     * Метод отрабатывает после выполнения контроллера, но до выдачи View
     */
    @Override
    public void postHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object o,
        ModelAndView modelAndView
    ) {
        log.info("[postHandle][" + request + "]");
    }

    /**
     * Метод отрабатывает после выдачи View
     */
    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object o,
        Exception ex
    ) {
        if (ex != null) {
            ex.printStackTrace();
        }
        log.info("[afterCompletion][" + request + "][exception: " + ex + "]");
    }

    private String getParameters(HttpServletRequest request) {
        StringBuilder posted = new StringBuilder();
        Enumeration<?> e = request.getParameterNames();
        if (e != null) {
            posted.append("?");
        }
        while (e != null && e.hasMoreElements()) {
            if (posted.length() > 1) {
                posted.append("&");
            }
            String curr = (String) e.nextElement();
            posted.append(curr).append("=");
            if (curr.contains("password")
                    || curr.contains("pass")
                    || curr.contains("pwd")) {
                posted.append("*****");
            } else {
                posted.append(request.getParameter(curr));
            }
        }
        String ip = request.getHeader("X-FORWARDED-FOR");
        String ipAddr = ip == null ? getRemoteAddr(request) : ip;
        if (ipAddr != null && !ipAddr.isEmpty()) {
            posted.append("&_psip=").append(ipAddr);
        }
        return posted.toString();
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && !ipFromHeader.isEmpty()) {
            log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }
}