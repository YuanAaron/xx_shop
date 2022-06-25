package cn.coderap.file.util;

import org.junit.Test;

public class FastDFSClientTest {

    @Test
    public void test1() throws  Exception{
        System.out.println(FastDFSClientUtil.getTrackerServer());
        System.out.println(FastDFSClientUtil.getStorageServer("group1"));
    }

}
