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
        String sourceString = "((({})))";
        String[] parseString = sourceString.split("");
        System.out.println(Arrays.toString(parseString));
        System.out.println(isCorrectParentheses(sourceString));
    }

    private static boolean isCorrectParentheses(String parentheses) {
//        Map<Character, Character> brackets = new HashMap<>();
//        brackets.put(')', '(');
//        brackets.put('}', '{');
//        brackets.put(']', '[');
//        brackets.put('>', '<');
//        ArrayList<String> leftBrackets =new ArrayList<>();
//        ArrayList<String> rightBrackets =new ArrayList<>();

        String [] leftBrackets = {"(", "[", "{", "<"};
        String [] rightBrackets = {"(", "[", "{", "<"};

        Deque<Character> stack = new LinkedList<>();
        for (char character : parentheses.toCharArray()) {
            if ((Arrays.toCharacter(leftBrackets).contains(character)) {
                stack.push(character);
            } else if ((Arrays.asList(rightBrackets).contains(character))) {
                if (stack.isEmpty() || stack.pop() != character) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
}