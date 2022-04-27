package com.jmetertools.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    public static <T> List<T> getRandomSample(List<T> container, int count){
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


}

