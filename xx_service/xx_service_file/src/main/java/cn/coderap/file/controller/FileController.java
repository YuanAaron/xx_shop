package cn.coderap.file.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return new Result(true, StatusCode.OK, "上传成功", url);
    }

    @GetMapping("/download")
    public void download(@RequestParam("group") String group,
                         @RequestParam("remoteFileName") String remoteFileName,
                         HttpServletResponse response) {
        fileService.download(group,remoteFileName,response);
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestParam("group") String group,
                         @RequestParam("remoteFileName") String remoteFileName) {
        fileService.delete(group, remoteFileName);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
