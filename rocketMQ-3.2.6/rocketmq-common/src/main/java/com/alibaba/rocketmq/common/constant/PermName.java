/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.rocketmq.common.constant;

/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 */
public class PermName {
    /**
     * 2的3次方乘1
     *      8
     */
    public static final int PERM_PRIORITY = 0x1 << 3;
    /**
     * 2的2次方乘1
     *      4
     */
    public static final int PERM_READ = 0x1 << 2;
    /**
     * 2的1次方乘1
     *      2
     */
    public static final int PERM_WRITE = 0x1 << 1;
    /**
     * 2的0次方乘1
     *      1
     */
    public static final int PERM_INHERIT = 0x1 << 0;


    public static boolean isReadable(final int perm) {
        //  6 & 4 = 4
        return (perm & PERM_READ) == PERM_READ;
    }


    public static boolean isWriteable(final int perm) {
        //  6 & 2 = 2
        return (perm & PERM_WRITE) == PERM_WRITE;
    }


    public static boolean isInherited(final int perm) {
        // 6 & 1 = 0
        return (perm & PERM_INHERIT) == PERM_INHERIT;
    }


    public static String perm2String(final int perm) {
        final StringBuffer sb = new StringBuffer("---");
        if (isReadable(perm)) {
            sb.replace(0, 1, "R");
        }

        if (isWriteable(perm)) {
            sb.replace(1, 2, "W");
        }

        if (isInherited(perm)) {
            sb.replace(2, 3, "X");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(7 | 7);
        System.out.println(2 | 6);
        System.out.println(2 | 4);
        System.out.println(8 | 2);
        System.out.println(6 & 2);
        System.out.println(6 & 4);
        System.out.println(6 & 1);


    }
}
