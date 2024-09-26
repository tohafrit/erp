package ru.korundm.helper.tag;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.korundm.dao.UserService;
import ru.korundm.helper.AutowireHelper;
import ru.korundm.helper.LoggedUser;

import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class SecurityTag extends ConditionalTagSupport {

    @Autowired
    private UserService userService;

    @Setter
    private String key;

    @Override
    protected boolean condition() {
        AutowireHelper.autowire(this);
        try {
            return userService.hasPrivilege(LoggedUser.get(), key);
        } catch (RuntimeException e) {
            return false;
        }
    }
}