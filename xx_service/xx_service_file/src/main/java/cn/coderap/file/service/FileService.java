package cn.coderap.file.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public interface FileService {

    String upload(MultipartFile file);

    void download(String group, String remoteFileName, HttpServletResponse response);

    void delete(String group, String remoteFileName);
}
