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
package com.alibaba.rocketmq.common.sysflag;

/**
 * topic 配置标识
 * 
 * @author: manhong.yqd<jodie.yqd@gmail.com>
 * @since: 14-5-28
 */
public class TopicSysFlag {
    /**
     * 单元化逻辑 topic 标识
     *   2的 0 次方乘以 1  =  1
     */
    private final static int FLAG_UNIT = 0x1 << 0;
    /**
     * 该 topic 有单元化订阅组
     *  2的 1 次方乘以 1  = 2
     */
    private final static int FLAG_UNIT_SUB = 0x1 << 1;


    /**
     * unit false  hasUnitSub true 返回2
     * unit true  hasUnitSub false 返回1
     * @param unit
     * @param hasUnitSub
     * @return
     */
    public static int buildSysFlag(final boolean unit, final boolean hasUnitSub) {
        int sysFlag = 0;

        if (unit) {
            // 1 | 0 = 1
            sysFlag |= FLAG_UNIT;
        }

        if (hasUnitSub) {
            // 0 | 2 = 2
            sysFlag |= FLAG_UNIT_SUB;
        }

        return sysFlag;
    }


    public static int setUnitFlag(final int sysFlag) {
        return sysFlag | FLAG_UNIT;
    }


    public static int clearUnitFlag(final int sysFlag) {
        return sysFlag & (~FLAG_UNIT);
    }


    public static boolean hasUnitFlag(final int sysFlag) {
        return (sysFlag & FLAG_UNIT) == FLAG_UNIT;
    }


    public static int setUnitSubFlag(final int sysFlag) {
        return sysFlag | FLAG_UNIT_SUB;
    }


    public static int clearUnitSubFlag(final int sysFlag) {
        return sysFlag & (~FLAG_UNIT_SUB);
    }


    public static boolean hasUnitSubFlag(final int sysFlag) {
        return (sysFlag & FLAG_UNIT_SUB) == FLAG_UNIT_SUB;
    }


    public static void main(String[] args) {
        System.out.println(1 | 0);
        System.out.println(2 | 0);
    }
}
