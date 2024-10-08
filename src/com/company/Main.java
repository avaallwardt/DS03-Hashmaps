package com.company;

public class Main {

    public static void main(String[] args) {
        MyHashMap<Integer, String> hashMap = new MyHashMap<>(5, 0.9);
        hashMap.put(null, null);
        //hashMap.putIfAbsent(1, "hi");
        hashMap.putIfAbsent(1, "hey");

        for(int i = 0; i < 100; i++){
            hashMap.put(1, "hi");
        }

        hashMap.put(null, null);

        System.out.println(hashMap.replace(1, "hey"));


        hashMap.display();
    }
}
