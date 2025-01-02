import java.io.*;
import org.json.*;
import java.math.BigInteger;
import java.util.*;

public class PolynomialSolver {

    public static void main(String[] args) throws IOException, JSONException {
        // Read JSON input files
        String testCase1 = readJSONFile("testcase1.json");
        String testCase2 = readJSONFile("testcase2.json");

        // Parse the JSON inputs
        JSONObject input1 = new JSONObject(testCase1);
        JSONObject input2 = new JSONObject(testCase2);

        // Solve for constant term 'c' for each test case
        BigInteger c1 = solveForConstantTerm(input1);
        BigInteger c2 = solveForConstantTerm(input2);

        // Print results
        System.out.println("Secret for TestCase 1: " + c1);
        System.out.println("Secret for TestCase 2: " + c2);
    }

    private static String readJSONFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    private static BigInteger solveForConstantTerm(JSONObject input) throws JSONException {
        // Extract n and k
        JSONObject keys = input.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Decode roots
        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        for (String key : input.keySet()) {
            if (!key.equals("keys")) {
                JSONObject root = input.getJSONObject(key);
                int x = Integer.parseInt(key);
                int base = root.getInt("base");
                String value = root.getString("value");

                BigInteger decodedY = new BigInteger(value, base);
                xValues.add(BigInteger.valueOf(x));
                yValues.add(decodedY);
            }
        }

        // Ensure we have enough points
        if (xValues.size() < k) {
            throw new IllegalArgumentException("Insufficient roots provided");
        }

        // Use Lagrange interpolation to find constant term
        return lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);
    }

    private static BigInteger lagrangeInterpolation(List<BigInteger> xValues, List<BigInteger> yValues, BigInteger x) {
        int size = xValues.size();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < size; i++) {
            BigInteger term = yValues.get(i);

            for (int j = 0; j < size; j++) {
                if (i != j) {
                    term = term.multiply(x.subtract(xValues.get(j)))
                                 .divide(xValues.get(i).subtract(xValues.get(j)));
                }
            }

            result = result.add(term);
        }

        return result;
    }
}
