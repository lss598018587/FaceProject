package com.wm.IO.Netty.heartToHeart;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.util.HashMap;

/**
 * Created by wangmiao on 2018/2/18.
 */
public class TestSigar {
    public static void main(String[] args) throws SigarException {
//        Sigar sigar = new Sigar();
//        CpuPerc infos[] = sigar.getCpuPercList();
//        CpuPerc cpuList[] =null;
//        System.out.println(System.getProperty("java.library.path"));
//        System.out.println("cpu 总量参数情况："+sigar.getCpu());
//        System.out.println("cpu 百分比情况："+sigar.getCpuPerc());
        Sigar sigar = new Sigar();
        CpuPerc cpuPerc = sigar.getCpuPerc();

        System.out.println(cpuPerc.getSys());

        Mem mem = sigar.getMem();
        System.out.println(mem.getTotal());
    }
}
