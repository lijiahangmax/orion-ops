/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.utils;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.codec.Base64s;
import com.orion.lang.utils.io.FileReaders;
import com.orion.lang.utils.io.Files1;
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
