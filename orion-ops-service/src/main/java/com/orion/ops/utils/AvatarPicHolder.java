package com.orion.ops.utils;

import com.orion.lang.utils.Arrays1;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.awt.ImageIcons;
import com.orion.lang.utils.awt.Images;
import com.orion.lang.utils.codec.Base64s;
import com.orion.lang.utils.io.FileReaders;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.system.SystemEnvAttr;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 头像处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/21 21:43
 */
@Slf4j
public class AvatarPicHolder {

    private AvatarPicHolder() {
    }

    /**
     * 头像生成器
     */
    private static final ImageIcons ICONS_GEN = new ImageIcons();

    private static final String AVATAR_PIC_SUFFIX = Const.SUFFIX_PNG;

    /**
     * 获取头像路径
     *
     * @param uid    uid
     * @param suffix 后缀
     * @return path
     */
    public static String getPicPath(Long uid, String suffix) {
        return Const.AVATAR_DIR + "/" + uid + "." + suffix;
    }

    /**
     * 创建并获取头像全路径
     *
     * @param uid    uid
     * @param suffix 后缀
     * @return path
     */
    public static String touchPicFile(Long uid, String suffix) {
        String path = Files1.getPath(SystemEnvAttr.PIC_PATH.getValue(), getPicPath(uid, suffix));
        Files1.touch(path);
        return path;
    }

    /**
     * 生成用户头像
     *
     * @param uid      uid
     * @param nickname name
     * @return 路径
     */
    public static String generatorUserAvatar(Long uid, String nickname) {
        if (Strings.isBlank(nickname)) {
            nickname = "O";
        }
        String url = getPicPath(uid, AVATAR_PIC_SUFFIX);
        char name = Arrays1.last(nickname.toCharArray(), 'O');
        BufferedImage img = ICONS_GEN.execute(name);
        String path = Files1.getPath(SystemEnvAttr.PIC_PATH.getValue(), url);
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
        String path = Files1.getPath(SystemEnvAttr.PIC_PATH.getValue(), url);
        if (!Files1.isFile(path)) {
            return null;
        }
        return Base64s.img64Encode(FileReaders.readAllBytes(path), Files1.getSuffix(path));
    }

    /**
     * 删除图片
     *
     * @param url url
     */
    public static void deletePic(String url) {
        String path = Files1.getPath(SystemEnvAttr.PIC_PATH.getValue(), url);
        Files1.delete(path);
    }

    /**
     * 判断是否存在
     *
     * @param path path
     * @return 是否存在
     */
    public static boolean isExist(String path) {
        return Strings.isNotBlank(path) && Files1.isFile(Files1.getPath(SystemEnvAttr.PIC_PATH.getValue(), path));
    }

}
