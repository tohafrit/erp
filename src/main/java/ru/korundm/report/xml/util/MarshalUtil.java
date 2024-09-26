package ru.korundm.report.xml.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.FileCopyUtils;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@JBossLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarshalUtil {

    private static final String XML_NAME = "message.xml";

    public static <T> void marshal(T object, HttpServletResponse response) {
        try {
            ClassPathResource classPathResource = new ClassPathResource("blank/xml" + File.separator + XML_NAME);
            File file = classPathResource.getFile();
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.displayName());
            marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.marshal(object, file);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + MimeUtility.encodeText(XML_NAME, StandardCharsets.UTF_8.displayName(), "Q") + "\"");
            response.setContentLength((int) file.length());
            FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        } catch (JAXBException | IOException exception) {
            log.error(MarshalUtil.class.getName() + " method: marshall(T object); ex: " + exception);
        }
    }
}