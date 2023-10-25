package ru.gb;
 class GBox <T> {

    private T value;
    public void methodOne(){
      //  T data = new T();
    }

    public GBox(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void showType() {
        System.out.printf("Type is %s, with value %s \n",
                value.getClass().getName(), getValue());
    }

    public static void main(String[] args) {
        GBox<String> stringGBox = new GBox<>("Hello");
        stringGBox.showType();
        GBox<Integer> integerGBox = new GBox(12);
        integerGBox.showType();
        //integerGBox.setValue("World! ");
    }
}



