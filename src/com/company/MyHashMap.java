package com.company;

import java.util.LinkedList;

public class MyHashMap<Key, Type> {

    // a node itself cannot be null but the data/value it holds can be null
    // key can be null but there can only be 1 null key
    // if 2 objects are in the same bucket, they might have the same key, but not necessarily
    // IMPORTANT QUESTION: so for putIfAbsent, do you only check for equality on the key value and not the object??? This is what the rubric implies, but it's ambiguous
    /*
    BIG QUESTION: so i should never call key in this class? bc i don't need to, i can just use .getHash??>

    The reason hash maps are good and because they need a key is because they are O(1) for searching because can just search by the key and then you get the bucket and even though you have to
    it resizes once it hits 75% the size of the maximum size
    QUESTIONS
    - should auto-resize -- do we need to make a method for resizing once it gets too big? -- make it a method
    - is the key always an instance variable of an object? -- for this, no but it doesnt HAVE to
    NEED TO ADD NULL CHECKS -- how deep should i go with this?
    // the LL in each bucket should
    // can have same key in a hashMap and can only have one null key --> if it's null, it automatically goes to index 0 bc can't call method .hashCode on a null object
     */
    // commit and push

    // normal hash map would be an array and each bucket would hold node objects
    // node would have data, nextPointer, etc
    private LinkedList<HashMapNodeData>[] array;
    //Integer[] list = new Integer[6];
    private double loadFactor;
    private boolean hasNullKey;

    // getters and setters also included
    public LinkedList<HashMapNodeData>[] getArray() {
        return array;
    }

    public void setArray(LinkedList<HashMapNodeData>[] array) {
        this.array = array;
    }

    public double getLoadFactor() {
        return loadFactor;
    }

    public void setLoadFactor(double loadFactor) {
        this.loadFactor = loadFactor;
    }

    public boolean isHasNullKey() {
        return hasNullKey;
    }

    public void setHasNullKey(boolean hasNullKey) {
        this.hasNullKey = hasNullKey;
    }

    public MyHashMap(){
        // can't specify the generic type in the initialization of an array that holds a linked list
        // IMPORTANT: if you say array = new LinkedList<>[16]; you get an error. I didn't know this before and we were told to do it with carrots but it doesn't work
        array = new LinkedList[16];
        // need to initialize each bucket (won't fill automatically) -- do i need to initialize each bucket in the array or will they not be set to null automatically?
        for(int i = 0; i < array.length; i++){
            array[i] = new LinkedList<HashMapNodeData>();
        }
        loadFactor = 0.75;
        hasNullKey = false;
    }

    public MyHashMap(int capacity, double loadFactor){
        array = new LinkedList[capacity];
        // do i need to initialize each bucket in the array or will they not be set to null automatically?
        for(int i = 0; i < array.length; i++){
            array[i] = new LinkedList<HashMapNodeData>();
        }
        this.loadFactor = loadFactor;
        hasNullKey = false;
    }


    public void empty() { //Empties the HashMap of all entries
        for(int i = 0; i < array.length; i++){
            array[i].clear();
        }
    }

    public boolean isEmpty() { //I mean....ya know.
        int numNotEmpty = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] != null){
                if(!array[i].isEmpty()){
                    return false;
                }
            }
        }
        hasNullKey = false; // it no longer has any keys, so it doesn't have a null key
        return true;
    }


    // the hashcodes for 2 keys can be the same if they are in the same list (if they are not in the same list, there is no chance they have the same hashCode but if they are in the same list, they MIGHT have the same hashCode but not necessarily)
    public void put(Key key, Type value) { //The HashMap equivalent of add()
        if(key == null){
            if(hasNullKey == true){
                System.out.println("This hash map already has a null key.");
                return;
            }
            else{
                hasNullKey = true;
            }
        }
        array[calculateIndex(key)].add(new HashMapNodeData(key, value));


        if(isResizeNeeded() == true) {
            // resizing
            // i chose to make an array to hold the old objects and then set the array instance variable to a larger array because .calculateIndex() only can use the size of the array (not a separate array object) and if i waited to set the array instance variable to the new array until the end, the magical formula would calculate the wrong index for everything
            // create a placeholder for all of the objects in array
            LinkedList<HashMapNodeData>[] originalArray = new LinkedList[array.length];
            // have to iterate through and transfer all of the elements from array into a placeholder for original array because if i just set originalArray to array, it could just be a reference and would change if i changed array
            for (int i = 0; i < array.length; i++) {
                originalArray[i] = array[i];
            }

            // set array to an arrayList that is twice as big
            array = new LinkedList[array.length * 2];
            for (int i = 0; i < array.length; i++) {
                array[i] = new LinkedList<HashMapNodeData>();
            }

            for (int i = 0; i < originalArray.length; i++) {
                if (originalArray[i] != null) {
                    for (int k = 0; k < originalArray[i].size(); k++) {
                        // don't need this line because the hash doesn't change --> originalArray[i].get(k).setHash(originalArray[i].get(k).getKey().hashCode());
                        int bucket = calculateIndex((Key) originalArray[i].get(k).getKey());
                        // this next line is updating the has after it has resized
                        array[bucket].add(originalArray[i].get(k));
                    }
                }
            }
        }
    }

    public boolean isResizeNeeded(){
        int hashMapSize = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] != null){
                hashMapSize += array[i].size();
                // a node could exist but the data it holds could be set to null and you still need to count it in the total count
            }
        }
        double loadFactor = hashMapSize / array.length;
        if(loadFactor > this.loadFactor){
            return true;
        }
        return false;
    }


    // functions like it would in a hash set (cannot have multiples of the same values, only of the same keys)
    // i ended up checking both the key and value and only adding it if the two together are not found because otherwise the value instance variable isn't used at all. The second sentence description on the rubric of this method was unclear/misleading, though.
    public void putIfAbsent(Key key, Type value) {
        //Only places in HashMap if no current entry exists. Does NOT overwrite.
       // they can be in the same bucket but not have the key match
        for(int i = 0; i < array[calculateIndex(key)].size(); i++){
            if(key == null) {
                // this next line tells us that the key is matched
                if (array[calculateIndex(key)].get(i).getKey() == null) {
                    return;
                    /*
                    if (array[calculateIndex(key)].get(i).getValue() == null) {
                        if (array[calculateIndex(key)].get(i).getValue() == value) {
                            return;
                        }
                    }
                    else if (array[calculateIndex(key)].get(i).getValue() != null) {
                        if (array[calculateIndex(key)].get(i).getValue().equals(value)) {
                            return;
                        }
                    }
                     */
                }

            }
            // this next line tells us that the key is matched
            else if(key.equals(array[calculateIndex(key)].get(i).getKey())){
                return;
/*
                if(array[calculateIndex(key)].get(i).getValue() == null){
                    if(array[calculateIndex(key)].get(i).getValue() == value) {
                        return;
                    }
                }
                else if(array[calculateIndex(key)].get(i).getValue() != null) {
                    if(array[calculateIndex(key)].get(i).getValue().equals(value)) {
                        return;
                    }
                }

 */
            }
        }
        // if the method doesn't end (aka the value with the right key and object combo isn't found, then will add it)
        // put method already considers null index thing
        put(key, value);
    }

    // should i return HashMapNodeData (all of the data) or the specific value
    public HashMapNodeData remove(Key key, Type value) {
        //Returns the item being removed
        // LinkedList<Type> list = array[calculateIndex()]; -- it would work if I made this instead of using array[magicalFormula()] multiple times -- yes bc it doesn't make a copy of the list, it just references it right???
        for(int i = 0; i < array[calculateIndex(key)].size(); i++){
            if(key == null){
                // this next line tells us that the key is matched
                if(array[calculateIndex(key)].get(i).getKey() == null){
                    if(array[calculateIndex(key)].get(i).getValue() == null){
                        if(array[calculateIndex(key)].get(i).getValue() == value) {
                            hasNullKey = false;
                            return array[calculateIndex(key)].remove(i);
                        }
                    }
                    else if(array[calculateIndex(key)].get(i).getValue() != null) {
                        if(array[calculateIndex(key)].get(i).getValue().equals(value)) {
                            hasNullKey = false;
                            return array[calculateIndex(key)].remove(i);
                        }
                    }
                }
            }
            // this next line tells us that the key is matched
            else if(key.equals(array[calculateIndex(key)].get(i).getKey())){
                if(array[calculateIndex(key)].get(i).getValue() == null){
                    if(array[calculateIndex(key)].get(i).getValue() == value) {
                        return array[calculateIndex(key)].remove(i);
                    }
                }
                else if(array[calculateIndex(key)].get(i).getValue() != null) {
                    if(array[calculateIndex(key)].get(i).getValue().equals(value)) {
                        return array[calculateIndex(key)].remove(i);
                    }
                }
            }
        }
        System.out.println("Item with the matching key and value not found.");
        return null;
    }


    public boolean containsValue(Type value) {
        //Will need to search all buckets - think about why.
        // you have to search all buckets because you don't have a key to automatically calculate the right bucket, right?
        for(int i = 0; i < array.length; i++){
            // must do a null check to make sure you don't call a method on a null object
            if(array[i] != null){
                for(int k = 0; k < array[i].size(); k++){
                    if(array[i].get(k).getValue() != null){
                        if(array[i].get(k).getValue().equals(value)){
                            return true;
                        }
                    }
                    else{
                        if(value == null){
                            return true; // this means that both the value at the index and provided are null
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean containsKey(Key key) { //Will search only one bucket.
        if(key == null){
            for(int i = 0; i < array[calculateIndex(key)].size(); i++){
                if(array[calculateIndex(key)].get(i).getKey() == null){
                    return true;
                }
            }
            // or could just return hasNullKey;
        }
        else{
            for(int i = 0; i < array[calculateIndex(key)].size(); i++){
                if(array[calculateIndex(key)].get(i).getKey() != null){
                    if(array[calculateIndex(key)].get(i).getKey().equals(key)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public HashMapNodeData get(Key key) { //Only searches one bucket.
        /*
        for(int i = 0; i < array[calculateIndex(key)].size(); i++){
            if(key == null){
                if(array[calculateIndex(key)].get(i).getKey() == null){
                    return array[calculateIndex(key)].get(i);
                }
            }
            else{
                if(array[calculateIndex(key)].get(i).getKey().equals(key)){
                    return array[calculateIndex(key)].get(i);
                }
            }
        }
        THIS CODE WOULD WORK
         */

        if(key == null){
            for(int i = 0; i < array[calculateIndex(key)].size(); i++){
                if(array[calculateIndex(key)].get(i).getKey() == null){
                    return array[calculateIndex(key)].get(i);
                }
            }
        }
        else{
            for(int i = 0; i < array[calculateIndex(key)].size(); i++){
                if(array[calculateIndex(key)].get(i).getKey() != null) {
                    if(array[calculateIndex(key)].get(i).getKey().equals(key)){
                        return array[calculateIndex(key)].get(i);
                }
                }
            }
        }

        return null;
    }

    //
    public Type getOrDefault(Key key, Type value) { //Searches for key and returns respective value if found, otherwise returns 2nd parameter
        if(containsKey(key)){
            return (Type) get(key).getValue();
        }
        else{
            return value;
        }
    }

    public int size() { //Returns num of items in total HashMap.
        int size = 0;
        for(int i = 0; i < array.length; i++){
            // array[i] won't be null because it will automatically contain an empty linked list
            // BUT maybe since the head is null, the linked list won't exist??? check this
            if(array[i] != null){
                size += array[i].size();
            }
        }
        return size;
    }

    public Type replace(Key key, Type value) { //Overwrites existing key/value. Return is old object. If no object exists, should output a message indicating so and do nothing.
        for(int i = 0; i < array[calculateIndex(key)].size(); i++){
            if(key == null){
                if(array[calculateIndex(key)].get(i).getKey() == null){
                    Type originalValue = (Type) array[calculateIndex(key)].get(i).getValue();
                    array[calculateIndex(key)].get(i).setValue(value);
                    return originalValue;
                }
            }
            else{
                if(array[calculateIndex(key)].get(i).getKey() != null){
                    if(array[calculateIndex(key)].get(i).getKey().equals(key)){
                        Type originalValue = (Type) array[calculateIndex(key)].get(i).getValue();
                        array[calculateIndex(key)].get(i).setValue(value);
                        return originalValue;
                    }
                }
            }
        }
        System.out.println("No such object exists in the hash map.");
        return value;
    }

    public void display() { //Output entire HashMap. Should indicate which bucket each element belongs to and, if multiple per bucket, starts at head and goes to tail.
        for(int i = 0; i < array.length; i++){
           if(array[i] != null){
               for(int k = 0; k < array[i].size(); k++){
                   // i is the index of the array that we are on, and bucket is another same for index of array, so bucket is i
                   // i just print the object itself in the LL, i don't call a getValue or getKey method or anything like that on it right?
                   System.out.println(array[i].get(k) + " (bucket: " + i + ")");
               }
           }
        }
    }

    // this takes in the HashMapNodeData instead and then gets the hash from that
    // we should only be calling calculateIndex once which is when you are resizing, right? otherwise should just use .getHash?
    /*
    public int calculateIndex(HashMapNodeData data) { //You'll need this for determining which bucket to go to when given a key.
        return data.getHash() & (array.length - 1);
        //         return object.getHash() & (array.length - 1);
        // shouldn't the formula be key.hashCode() not hashCode(key)?
        //Formula is: index = hashCode(key) & (n-1).
    }

     */

    public int calculateIndex(Key key) { //You'll need this for determining which bucket to go to when given a key.
        if(key == null){
            return 0;
        }
        return key.hashCode() & (array.length - 1);
        //         return object.getHash() & (array.length - 1);
        // shouldn't the formula be key.hashCode() not hashCode(key)?
        //Formula is: index = hashCode(key) & (n-1).
    }

private class HashMapNodeData<Key, Type> {

    private Key key;
    private int hash;
    private Type value;

    public HashMapNodeData(Key key, Type value){
        this.key = key;
        this.value = value;
        // must use .setHashCode and recalculate hashCode when you resize the array
        if(key != null){
            hash = key.hashCode();
        }
        else{
            hash = 0;
        }
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public Type getValue() {
        return value;
    }

    public void setValue(Type value) {
        this.value = value;
    }
}

}
