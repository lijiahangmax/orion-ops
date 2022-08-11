package com.orion.ops.utils;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.codec.Base64s;
import com.orion.lang.utils.io.FileReaders;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.system.SystemEnvAttr;
import lombok.extern.slf4j.Slf4j;

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
     * 删除图片
     *
     * @param url url
     */
    public static void deletePic(String url) {
        String path = Files1.getPath(SystemEnvAttr.PIC_PATH.getValue(), url);
        Files1.delete(path);
    }

    /**
     * 获取头像
     *
     * @param path path
     * @return avatar base64
     */
    public static String getUserAvatar(String path) {
        if (isExist(path)) {
            return getBase64(path);
        } else {
            return null;
        }
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

    /**
     * 获取头像 base64
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

}
