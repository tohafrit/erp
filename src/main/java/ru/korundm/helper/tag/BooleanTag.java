package ru.korundm.helper.tag;

import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

@JBossLog
public class BooleanTag extends TagSupport {

    @Setter
    private String value;

    @Setter
    private boolean isImage;

    @Setter
    private String ttext;

    @Setter
    private String ftext;

    @Setter
    private String tbound;

    @Setter
    private String fbound;

    @Override
    public int doStartTag() {
        JspWriter out = pageContext.getOut();
        String pageText = "", title = "";
        switch (value) {
            case "1":
            case "true":
                if (ttext != null) {
                    title = ttext;
                } else if (tbound != null) {
                    title = LocaleSupport.getLocalizedMessage(pageContext, tbound, new Object[]{});
                }
                pageText = isImage ? "<i title=\"" + title + "\" class=\"icon large check circle green\"></i>" : title;
                break;
            case "0":
            case "false":
                if (ftext != null) {
                    title = ftext;
                } else if (fbound != null) {
                    title = LocaleSupport.getLocalizedMessage(pageContext, fbound, new Object[]{});
                }
                pageText = isImage ? "<i title=\"" + title + "\" class=\"icon large times circle red\"></i>" : title;
                break;
        }
        try {
            out.print(pageText);
        } catch (IOException ioe) {
            log.error("Error: " + ioe.getMessage());
        }
        return SKIP_BODY;
    }
}