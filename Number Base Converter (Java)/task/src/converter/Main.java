package converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static int sourceBase, targetBase;
    static boolean isExit;
    static String[] numberAsArray;

    public static void main(String[] args) {
        do {
            configRadixes();
        } while (!isExit);
    }

    public static void configRadixes() {
        System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
        String command = scanner.nextLine().strip();
        if (command.equalsIgnoreCase("/exit")) isExit = true;
        else {
            String[] radixes = command.split("\\s+");
            sourceBase = Integer.parseInt(radixes[0]);
            targetBase = Integer.parseInt(radixes[1]);
            converter();
            scanner.nextLine();
        }
    }

    public static void converter() {
        do {
            System.out.printf("Enter number in base %d to convert to base %d (To go back type /back) "
                    , sourceBase, targetBase);
            String sourceNum = scanner.next().strip().toUpperCase();
            if (sourceNum.equalsIgnoreCase("/back")) break;
            convertToAny(sourceNum);
        } while (true);
    }

    public static void convertToAny(String sourceNum) {
        convertToDecimal(sourceNum);
        StringBuilder converted = new StringBuilder();
        if (sourceNum.contains(".")) {
            converted.append(convertInteger()).append(".").append(convertFractional());
            while (converted.length() <= converted.indexOf(".") + 5) converted.append(0);
        } else {
            converted.append(convertInteger());
        }
        System.out.println("Conversion result: " + converted + "\n");
    }

    public static String convertFractional() {
        int radix = targetBase;
        BigInteger A = new BigInteger("65");
        StringBuilder result = new StringBuilder();
        BigDecimal product =
                BigDecimal.valueOf(Double.parseDouble(numberAsArray[1]) / (Math.pow(10, numberAsArray[1].length())))
                        .multiply(BigDecimal.valueOf(radix));
        BigDecimal nonFractionalPart, fractionalPart;
        do {
            nonFractionalPart = product.setScale(0, RoundingMode.DOWN);
            fractionalPart = product.remainder(BigDecimal.ONE);
            if (radix > 10 && product.compareTo(BigDecimal.TEN) >= 0) {
                result.append((char) (Integer.parseInt(String.valueOf(
                        (nonFractionalPart.subtract(BigDecimal.TEN).toBigInteger()).add(A)))));
            } else {
                result.append(nonFractionalPart);
            }
            product = fractionalPart.multiply(BigDecimal.valueOf(radix));
        } while(fractionalPart.compareTo(BigDecimal.ZERO) > 0 && result.length() <= result.indexOf(".") + 5);
        return String.valueOf(result);
    }

    public static String convertInteger() {
        int radix = targetBase;
        BigInteger quotient = BigInteger.valueOf(Long.parseLong(numberAsArray[0]));
        BigInteger A = new BigInteger("65");
        StringBuilder result = new StringBuilder();
        do {
            BigInteger remainder = quotient.mod(BigInteger.valueOf(radix));
            quotient = quotient.divide(BigInteger.valueOf(radix));
            if (radix > 10 && remainder.compareTo(BigInteger.TEN) >= 0) {
                result.append((char) (Integer.parseInt(String.valueOf((remainder.subtract(BigInteger.TEN)).add(A)))));
            } else {
                result.append(remainder);
            }
        } while (quotient.compareTo(BigInteger.ZERO) > 0);
        return String.valueOf(result.reverse());
    }

    public static void convertToDecimal(String sourceNum) {
        int radix = sourceBase;
        BigInteger integerResult = BigInteger.ZERO;
        int digit;
        boolean isFractional = sourceNum.contains(".");
        int numTail = isFractional ? sourceNum.indexOf(".") : sourceNum.length();
        StringBuilder fullResult = new StringBuilder();
        for (int index = 0; index < sourceNum.length(); index++) {
            char digitChar = sourceNum.charAt(index);
            if (digitChar == '.') {
                break;
            }
            if (radix > 10 && (digitChar >= (char) 65 && digitChar <= (char) 90)) {
                digit = digitChar - 55;
            } else {
                digit = Integer.parseInt(String.valueOf(digitChar));
            }
            integerResult = integerResult
                    .add(BigInteger.valueOf((long) (digit * (Math.pow(radix, Math.abs(--numTail))))));
        }

        if (isFractional) {
            BigDecimal fractionalResult = BigDecimal.ZERO;
            int fractionalDigit;
            String fractional = sourceNum.split("\\.")[1];

            for (int i = 0; i < fractional.length(); i++) {
                char digitChar = fractional.charAt(i);
                if (radix > 10 && (digitChar >= (char) 65 && digitChar <= (char) 90)) {
                    fractionalDigit = (digitChar - 55);
                } else {
                    fractionalDigit = Integer.parseInt(String.valueOf(digitChar));
                }
                fractionalResult = fractionalResult
                        .add(BigDecimal.valueOf(fractionalDigit / Math.pow(radix, (i + 1))));
            }
            fullResult.append(integerResult).append(fractionalResult).deleteCharAt(fullResult.indexOf(".") - 1);
        } else {
            fullResult.append(integerResult);
        }
        numberAsArray = String.valueOf(fullResult).split("\\.");
    }
}