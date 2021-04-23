package com.orion.ops.utils;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnvAttr;
import com.orion.utils.Arrays1;
import com.orion.utils.Strings;
import com.orion.utils.codec.Base64s;
import com.orion.utils.image.ImageIcons;
import com.orion.utils.image.Images;
import com.orion.utils.io.FileReaders;
import com.orion.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 头像生成器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/21 21:43
 */
@Slf4j
public class HeadPicGenerator {

    private HeadPicGenerator() {
    }

    /**
     * 头像生成器
     */
    private static final ImageIcons ICONS_GEN = new ImageIcons();

    private static final String HEAD_PIC_DIR = "/head_pic/";

    private static final String HEAD_PIC_SUFFIX = Const.SUFFIX_PNG;

    /**
     * 生成用户头像
     *
     * @param uid      uid
     * @param nickname name
     * @return 路径
     */
    public static String generatorUserHeadPic(Long uid, String nickname) {
        if (Strings.isBlank(nickname)) {
            nickname = "O";
        }
        String url = HEAD_PIC_DIR + uid + "." + HEAD_PIC_SUFFIX;
        char name = Arrays1.last(nickname.toCharArray(), 'O');
        BufferedImage img = ICONS_GEN.execute(name);
        String path = Files1.getPath(EnvAttr.PIC_PATH.getValue() + url);
        File file = new File(path);
        Files1.touch(file);
        try {
            Images.write(img, file);
        } catch (IOException e) {
            log.error("生成用户头像失败 uid: {}, name: {}, path: {}, e: {}", uid, name, path, e);
        }
        return url;
    }

    /**
     * 获取头像base64
     *
     * @param url url
     * @return base64
     */
    public static String getBase64(String url) {
        String path = Files1.getPath(EnvAttr.PIC_PATH.getValue() + url);
        return Base64s.img64Encode(FileReaders.readFast(path), Files1.getSuffix(path));
    }

}
