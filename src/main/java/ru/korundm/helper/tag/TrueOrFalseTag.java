package ru.korundm.helper.tag;

import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

@JBossLog
public class TrueOrFalseTag extends TagSupport {

    @Setter
    private String name;

    @Setter
    private Boolean value;

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            String htmlSelector = "<select class=\"ui dropdown label std-select\"" + (StringUtils.isNotBlank(name) ? " name=\"" + name + "\"" : "") + ">" +
                "<option value=\"0\"" + (Boolean.FALSE.equals(value) ? " selected=\"selected\"" : "") + ">Нет</option>" +
                "<option value=\"1\"" + (Boolean.TRUE.equals(value) ? " selected=\"selected\"" : "") + ">Да</option>" +
                "</select>";
            out.print(htmlSelector);
        } catch (IOException ioe) {
            log.error("Error: " + ioe.getMessage());
        }
        return SKIP_BODY;
    }
}