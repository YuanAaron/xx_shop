package cn.coderap.file.service.impl;

import cn.coderap.file.pojo.FastDFSFile;
import cn.coderap.file.service.FileService;
import cn.coderap.file.util.FastDFSClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file) {
        String url = null;
        FastDFSFile fastDFSFile = new FastDFSFile();
        try {
            // 文件名
            String fileName = file.getOriginalFilename();
            // 文件内容
            byte[] contents = file.getBytes();
            // 文件扩展名
            String ext = fileName.substring(fileName.lastIndexOf(".")+1);
            fastDFSFile.setName(fileName);
            fastDFSFile.setContent(contents);
            fastDFSFile.setExt(ext);
            String[] results = FastDFSClientUtil.upload(fastDFSFile);
            url = FastDFSClientUtil.getTrackerURL() + results[0] + "/" + results[1];
        } catch (IOException e) {
            log.error("文件上传失败", e);
        }
        return url;
    }

    @Override
    public void download(String group, String remoteFileName, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            response.reset();
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(remoteFileName, StandardCharsets.UTF_8));

            InputStream inputStream = FastDFSClientUtil.downFile(group, remoteFileName);
            outputStream = response.getOutputStream();
            bis = new BufferedInputStream(inputStream);
            bos = new BufferedOutputStream(outputStream);

            byte[] buff = new byte[1024];
            int bytesRead;
            while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("文件下载失败", e);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                log.error("文件流关闭失败",e);
            }
        }
    }

    @Override
    public void delete(String group, String remoteFileName) {
        FastDFSClientUtil.deleteFile(group, remoteFileName);
    }
}
