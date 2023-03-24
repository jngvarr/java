package ru.gb.lesson6.proxy;

import ru.gb.lesson4.hw.Apple;
import ru.gb.lesson4.hw.Box;

import java.lang.reflect.*;

public class ProxyDemo {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Box<Apple> box = new Box();

        System.out.println(box.getClass().getGenericInterfaces());









        // Reflection API
//        Person person = new Person("name");
//        System.out.println(person.getName());

        Class<Person> personClass = Person.class;

        Constructor<Person> constructor = personClass.getConstructor(String.class);
        final Person person = constructor.newInstance("name");

        Method getName = personClass.getMethod("getName");
        Object getNameResult = getName.invoke(person); // person.getName()
        System.out.println(getNameResult); // name

        HasName personProxy = (HasName) Proxy.newProxyInstance(Person.class.getClassLoader(), new Class<?>[]{HasName.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // proxy = person
                // method = getName
                // args = []

                String result = (String) method.invoke(person, args);
                if ("name".equals(result)) {
                    return "BLABLA";
                } else {
                    return "aaaaa";
                }

//                return "proxy_result";
            }
        });

        // CGLib


        System.out.println(personProxy.getName());

    }

    interface HasName {
        String getName();
    }

    static class Person implements HasName {

        private String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
