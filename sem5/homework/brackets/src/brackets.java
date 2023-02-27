/*
* Написать метод, определяющую правильность расстановки скобок в выражении.
* Могут содержаться следующие скобки: ()[]{}<>
* () -> true
* []{}((<>)) -> true
* ) -> false
* [) -> false
* []{}(<)> -> false
  private static boolean isCorrectParentheses(String parentheses) {
  // парсим текст на слова
  // печатаем слова в порядке возрастания длины
  throw new UnsupportedOperationException();
  }
  */

import java.util.*;

public class brackets {
    public static void main(String[] args) {
        System.out.println(isCorrectParentheses("()"));
        System.out.println(isCorrectParentheses("[]{}((<>))"));
        System.out.println(isCorrectParentheses(")"));
        System.out.println(isCorrectParentheses("[)"));
        System.out.println(isCorrectParentheses("[]{}(<)>"));
    }

    private static boolean isCorrectParentheses(String parentheses) {
//        Map<Character, Character> brackets = new HashMap<>();
//        brackets.put(')', '(');
//        brackets.put('}', '{');
//        brackets.put(']', '[');
//        brackets.put('>', '<');

        String[] leftBrackets = {"(","[","{","<"};
        String[] rightBrackets = {")","]","}",">"};
        Deque<Character> stack = new LinkedList<>();
        for (char character : parentheses.toCharArray()) {
            if (Arrays.toString(leftBrackets).contains("" + character)) {
//                System.out.println(Arrays.toString(leftBrackets));
//                System.out.println("" + character);
//                System.out.println(Arrays.toString(leftBrackets).contains("" + character));
                stack.add(character);
            } else if ((Arrays.toString(rightBrackets).contains("" + character))) {
                if (stack.isEmpty()) {
                    return false;
                } else if ((character == ')') && !(stack.pollLast().equals('('))) {
                    return false;
                } else if ((character == ']') && !(stack.pollLast()).equals('[')) {
                    return false;
                } else if ((character == '}') && !(stack.pollLast()).equals('{')) {
                    return false;
                } else if ((character == '>') && !(stack.pollLast()).equals('<')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}