package com.meterspheretools.utils

class RandomUtil {
    static getCharSet(){
        List<Character> charSet = new ArrayList<>()
        for (int i=0; i<256; i++){
            charSet.add(i, Character.forDigit(i, 10))
        }
        return charSet
    }

    static getRandomSample(List container, int count){
        List sample = new ArrayList()
        def rand = new Random()
        while (count > 0){
            def index = rand.nextInt(container.size())
            if (sample.indexOf(container.get(index)) == -1) {
                sample.add(container.get(index))
            }
        }
    }


}
