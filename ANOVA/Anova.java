
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;

import java.util.ArrayList;
import java.util.List;

public class Anova {
    private final double[][] values;
    private final double[] meanAlternatives;
    private final double[][] errors;
    private final double[] alphas;
    private double totalMeanAlternatives;
    private double SST;
    private double SSE;
    private double SSA;
    private double meanSquareAlternatives;
    private double meanSquareErrors;
    private double computedF;
    private double tableF;
    public List<String> contrastResults = new ArrayList<>();
    private final int n; // broj mjerenja
    private final int k; // broj alternativa

    public Anova(int n, int k, double[][] values) {
        this.n = n;
        this.k = k;
        this.values = values;
        meanAlternatives = new double[k];
        errors = new double[n][k];
        alphas = new double[k];
        calculateMeanOfAlternatives();
        calculateErrors();
        calculateTotalMeanAlternatives();
        calculateAlphas();
        calculateSSE();
        calculateSSA();
        calculateSST();
        calculateMeanSquareAlternatives();
        calculateMeanSquareErrors();
        calculateComputedF();
        calculateTableF();
        calculateCAll();
    }

    public void calculateCAll() {
        for (int i = 0; i < k - 1; i++) {
            for (int j = i + 1; j < k; j++) {
                double c = calculateC(i, j);
                double sc = calculateSC();
                TDistribution tDistribution = new TDistribution(getDegFreedomErrors(), 0.95);
                double x = 0.0, step = 0.0001;
                while (Math.abs(tDistribution.cumulativeProbability(x) - 0.95) > step)
                    x += step;
                double c1 = c - sc * x;
                double c2 = c + sc * x;
                if (c1 < 0.0 && c2 > 0.0)
                    contrastResults.add(String.format("Alternative %d i %d se ne razlikuju.", i, j));
                else
                    contrastResults.add(String.format("Alternative %d i %d se razlikuju.", i, j));
            }
        }
    }

    public double calculateSC() {
        double se = getMeanSquareErrors();
        return Math.sqrt(se * 2.0 / (k * n));
    }

    public double calculateC(int a1, int a2) {
        return alphas[a1] - alphas[a2];
    }

    public void calculateTableF() {
        // za interval povjerenja 95%
        FDistribution fDistribution = new FDistribution(getDegFreedomAlternatives(), getDegFreedomErrors(), 0.95);
        double x = 0.0, step = 0.000001;
        while (Math.abs(fDistribution.cumulativeProbability(x) - 0.95) > step)
            x += step;
        tableF = x;
    }

    public void calculateComputedF() {
        computedF = meanSquareAlternatives / meanSquareErrors;
    }

    public void calculateMeanSquareErrors() {
        meanSquareErrors = SSE / getDegFreedomErrors();
    }

    public void calculateMeanSquareAlternatives() {
        meanSquareAlternatives = SSA / getDegFreedomAlternatives();
    }

    public void calculateSST() {
        SST = SSA + SSE;
    }

    public void calculateSSA() {
        double sum = 0.0;
        for (int i = 0; i < k; i++) {
            sum += (alphas[i] * alphas[i]);
        }
        SSA = n * sum;
    }

    public void calculateSSE() {
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                sum += (errors[i][j] * errors[i][j]);
            }
        }
        SSE = sum;
    }

    public void calculateAlphas() {
        for (int i = 0; i < k; i++) {
            alphas[i] = meanAlternatives[i] - totalMeanAlternatives;
        }
    }

    public void calculateTotalMeanAlternatives() {
        double sum = 0.0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++) {
                sum += values[i][j];
            }
        totalMeanAlternatives = sum / (k * n);
    }

    public void calculateErrors() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++) {
                errors[i][j] = values[i][j] - meanAlternatives[j];
            }
    }

    private void calculateMeanOfAlternatives() {
        for (int i = 0; i < k; i++) {
            meanAlternatives[i] = getMeanOfAlternative(i);
        }
    }

    private double getMeanOfAlternative(int j) {
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += values[i][j];
        }
        return sum / n;
    }

    public int getDegFreedomAlternatives() {
        return k - 1;
    }

    public int getDegFreedomErrors() {
        return k * (n - 1);
    }

    public int getDegFreedomTotal() {
        return getDegFreedomAlternatives() + getDegFreedomErrors();
    }

    public double getSSE() {
        return SSE;
    }

    public double getSSA() {
        return SSA;
    }

    public double getSST() {
        return SST;
    }

    public double getMeanSquareAlternatives() {
        return meanSquareAlternatives;
    }

    public double getMeanSquareErrors() {
        return meanSquareErrors;
    }

    public double getComputedF() {
        return computedF;
    }

    public double getTableF() {
        return tableF;
    }
}
