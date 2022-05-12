package com.jmetertools.utils;


import com.jmetertools.base.exceptions.ParamException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public static <T> List<T> getSample(List<T> container, int count){
        List<T> sample = new ArrayList<>();
        Random rand = new Random();
        while (count > 0){
            int index = rand.nextInt(container.size());
            if (!sample.contains(container.get(index))) {
                sample.add(container.get(index));
                count--;
            }
        }
        return sample;
    }

    /**
     * 获取随机数，范围为 [1,bound]
     * @param bound 边界值
     * @return 范围内的随机整数
     */
    public static int getRandomInt(int bound){
        return ThreadLocalRandom.current().nextInt(bound) + 1;
    }

    /**
     * 获取随机数，范围为 [1,bound]
     * @param bound 边界值
     * @return 范围内的随机整数
     */
    public static long getRandomLong(long bound){
        return ThreadLocalRandom.current().nextLong(bound) + 1;
    }

    /**
     * 获取随机数，范围为 [start, end)
     * @param start 起点
     * @param end 终点
     * @return 范围内的随机数
     */
    public static int getRandomInt(int start, int end){
        return ThreadLocalRandom.current().nextInt(end - start) + start;
    }

    /**
     * 获取随机元素
     * @param container 待取样对象
     * @param <T> 对象元素类型
     * @return 从容器中取样的内容
     */
    public static <T> T getSample(T... container){
        return container[ThreadLocalRandom.current().nextInt(container.length)];
    }

    /**
     * 随机取样一个元素
     * @param container 待取样容器，不能为null或
     * @param <T> 元素类型，泛型
     * @return 取样结果
     */
    public static <T> T getSample(List<T> container){
        if (container == null || container.isEmpty()) ParamException.fail("列表内容不能为空!");
        return container.get(ThreadLocalRandom.current().nextInt(container.size()));
    }

    /**
     * 从字符串数组中随机取样一个元素
     * @param container 待取样容器，不能为null或空
     * @return 取样结果
     */
    public static String getSample(String... container){
        if (ArrayUtils.isEmpty(container)) ParamException.fail("数组不能为空!");
        return container[ThreadLocalRandom.current().nextInt(container.length)];
    }

    /**
     * 从字符串中随机取样一个元素
     * @param container 待取样字符串
     * @return 取样结果
     */
    public static String getSample(String container){
        if (StringUtils.isBlank(container)) ParamException.fail("字符串不能为空!");
        return Character.toString(container.charAt(ThreadLocalRandom.current().nextInt(container.length())));
    }


}

