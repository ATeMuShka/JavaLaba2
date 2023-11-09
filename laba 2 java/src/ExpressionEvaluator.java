import java.util.*;

public class ExpressionEvaluator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите выражение: ");
        String expression = scanner.nextLine();

// Запрашиваем значения переменных у пользователя
        Map<String, Double> variables = new HashMap<>();
        for (String variable : getVariables(expression)) {
            System.out.print("Введите значение для переменной " + variable + ": ");
            double value = scanner.nextDouble();
            variables.put(variable, value);
        }

// Проверяем корректность выражения и вычисляем значение
        try {
            double result = evaluateExpression(expression, variables);
            System.out.println("Результат: " + result);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Метод для получения списка переменных из выражения
    private static List<String> getVariables(String expression) {
        List<String> variables = new ArrayList<>();
        char[] chars = expression.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                StringBuilder variable = new StringBuilder();
                while (i < chars.length && Character.isLetter(chars[i])) {
                    variable.append(chars[i]);
                    i++;
                }
                variables.add(variable.toString());
            }
        }

        return variables;
    }

    // Метод для вычисления значения выражения
    private static double evaluateExpression(String expression, Map<String, Double> variables) {
        Deque<Double> values = new ArrayDeque<>();
        Deque<Character> operators = new ArrayDeque<>();

        char[] chars = expression.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];

            if (ch == ' ') {
                continue;
            }

            if (Character.isDigit(ch)) {
                StringBuilder number = new StringBuilder();
                while (i < chars.length && (Character.isDigit(chars[i]) || chars[i] == '.')) {
                    number.append(chars[i]);
                    i++;
                }
                values.push(Double.parseDouble(number.toString()));
            } else if (Character.isLetter(ch)) {
                StringBuilder variable = new StringBuilder();
                while (i < chars.length && Character.isLetter(chars[i])) {
                    variable.append(chars[i]);
                    i++;
                }
                if (!variables.containsKey(variable.toString())) {
                    throw new IllegalArgumentException("Неизвестная переменная: " + variable);
                }
                values.push(variables.get(variable.toString()));
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    calculate(values, operators);
                }
                operators.pop();
            } else if (isOperator(ch)) {
                while (!operators.isEmpty() && getPrecedence(ch) <= getPrecedence(operators.peek())) {
                    calculate(values, operators);
                }
                operators.push(ch);
            } else {
                throw new IllegalArgumentException("Неверный символ: " + ch);
            }
        }

        while (!operators.isEmpty()) {
            calculate(values, operators);
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Неверное выражение");
        }

        return values.pop();
    }

    // Метод для проверки, является ли символ оператором
    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    // Метод для получения приоритета оператора
    private static int getPrecedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        } else {
            return 0;
        }
    }

    // Метод для выполнения операции над двумя значениями
    private static void calculate(Deque<Double> values, Deque<Character> operators) {
        double num2 = values.pop();
        double num1 = values.pop();
        char operator = operators.pop();

        if (operator == '+') {
            values.push(num1 + num2);
        } else if (operator == '-') {
            values.push(num1 - num2);
        } else if (operator == '*') {
            values.push(num1 * num2);
        } else if (operator == '/') {
            if (num2 == 0) {
                throw new IllegalArgumentException("Деление на ноль");
            }
            values.push(num1 / num2);
        }
    }
}