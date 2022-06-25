package cn.coderap.file.pojo;

import lombok.Data;

@Data
public class FastDFSFile {
    //文件名称
    private String name;
    //文件内容
    private byte[] content;
    //文件扩展名
    private String ext;
    //作者
    private String author;
    //md5
    private String md5;

    public FastDFSFile() {
    }

    public FastDFSFile(String name, byte[] content, String ext, String author) {
        this.name = name;
        this.content = content;
        this.ext = ext;
        this.author = author;
    }

    public FastDFSFile(String name, byte[] content, String ext) {
        this.name = name;
        this.content = content;
        this.ext = ext;
    }
}

