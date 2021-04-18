package com.orion.ops;

import com.orion.http.apache.file.ApacheUpload;
import com.orion.http.support.HttpUploadPart;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 14:47
 */
public class UploadTests {

    @Test
    public void upload() {
        ApacheUpload up = new ApacheUpload("http://localhost:9119/orion/api/key/add");
        up.part(new HttpUploadPart("file", new File("C:\\Users\\ljh15\\Desktop\\env\\id_rsa")));
        up.formPart("name", "ecs key");
        up.formPart("password", "admin123");
        up.formPart("description", "ecs秘钥");
        System.out.println(up.await().getBodyString());
    }

}
