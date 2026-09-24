package fr.istic.pra.myset;

import fr.istic.pra.bench.*;
import fr.istic.pra.util.L3Set;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Application Swing + XChart pour afficher les temps de chaque implémentation
 * de L3Set en fonction de la taille de l'ensemble.
 *
 * <p>Options de scénario :
 * <ul>
 *   <li>dense         → trait plein</li>
 *   <li>épars         → pointillés</li>
 *   <li>les deux      → dense en trait plein, épars en pointillés</li>
 * </ul>
 *
 * <p>Le benchmark tourne dans un SwingWorker (l'UI ne gèle pas), et la
 * sélection d'une opération / d'un scénario relance la mesure.
 */
public final class BenchmarkApp {

    // ------------------------------------------------------------------
    //  Configuration
    // ------------------------------------------------------------------
    private static final String[] IMPL_NAMES = {
            "hashSet", "treeSet", "sortedArrayList", "linkedList", "sortedLinkedList", "mySet"
    };

    /**
     * Opérations benchmarkables, filtrées par année : on ne mesure que les
     * opérations que {@code MySet} doit implémenter pour l'année choisie.
     * Le reste est retiré par la génération du squelette (marqueurs d'année),
     * comme pour {@code MySet}.
     */
    private static final Op[] BENCH_OPS = {
            Op.BUILD,
            Op.ADD_ONE,
            Op.CONTAINS,
            Op.REMOVE,
            Op.ITERATE,
            Op.COPY,


            Op.UNION,
            Op.SYMMETRIC_DIFFERENCE,
    };

    private static final int[] SIZES = {500, 1_000, 2_000, 5_000, 10_000, 20_000, 50_000, 100_000}; // , 200_000};
    private static final long SPARSE_DOMAIN = 4_000_000L; // domaine constant pour l'épars

    private static final long SEED = 12345L;

    /** Au-delà de 100 ms par mesure, on arrête de tester les tailles plus grandes. */
    private static final long TIME_LIMIT_NANOS = 500_000_000L;

    // Politique de mesure rapide (pour rester réactif dans l'UI)
    private static final AdaptiveTimer.Policy POLICY =
            AdaptiveTimer.Policy.millis(50, 3, 500_000, 1000);

    // ------------------------------------------------------------------
    //  Éléments de l'UI
    // ------------------------------------------------------------------
    private final JComboBox<Op> opSelector;
    private final JComboBox<String> scenarioSelector;
    private final JButton benchButton;
    private final JProgressBar progress;
    private final JLabel status;
    private final JCheckBox logAxes = new JCheckBox("Axes log", true);
    private final JSlider yScale = new JSlider(SwingConstants.VERTICAL, 1, 100, 100);
    private final JSlider xScale = new JSlider(SwingConstants.HORIZONTAL, 1, 100, 100);
    private JPanel chartArea;
    private XChartPanel<XYChart> chartPanel;
    private final JFrame frame;

    private ChartData lastData;
    private String lastScenario;

    private final L3SetBench bench = new L3SetBench(new AdaptiveTimer(POLICY), SEED);

    public BenchmarkApp() {
        // --- Contrôles ---
        opSelector = new JComboBox<>(BENCH_OPS);
        scenarioSelector = new JComboBox<>(new String[]{
                "dense", "épars", "les deux"
        });
        benchButton = new JButton("Benchmark");
        progress = new JProgressBar();
        progress.setStringPainted(true);
        status = new JLabel("Choisissez opération + scénario puis cliquez sur Benchmark.");

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Opération :"));
        controls.add(opSelector);
        controls.add(new JLabel("Scénario :"));
        controls.add(scenarioSelector);
        controls.add(benchButton);
        controls.add(logAxes);
        controls.add(status);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progress, BorderLayout.CENTER);
        bottom.add(progressPanel, BorderLayout.SOUTH);

        // --- Graphique (vide au départ) ---
        chartPanel = new XChartPanel<>(newChart());
        chartArea = new JPanel(new BorderLayout());
        chartArea.add(chartPanel, BorderLayout.CENTER);
        yScale.setToolTipText("Zoom vertical : bas = plus petite courbe entière, haut = étendue complète (échelle log)");
        yScale.setEnabled(false);
        chartArea.add(yScale, BorderLayout.EAST);
        xScale.setToolTipText("Zoom horizontal : bas = plus petite courbe entière, haut = étendue complète (échelle log)");
        xScale.setEnabled(false);
        chartArea.add(xScale, BorderLayout.SOUTH);

        // --- Fenêtre ---
        frame = new JFrame("Benchmark L3Set — temps par taille");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(controls, BorderLayout.NORTH);
        frame.add(chartArea, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setSize(1050, 700);
        frame.setLocationRelativeTo(null);

        benchButton.addActionListener(e -> runBenchmark());
        // Recharge aussi quand on change d'opération ou de scénario
        opSelector.addActionListener(e -> runBenchmark());
        scenarioSelector.addActionListener(e -> runBenchmark());
        logAxes.addActionListener(e -> {
            yScale.setEnabled(!logAxes.isSelected());
            xScale.setEnabled(!logAxes.isSelected());
            if (lastData != null) updateChart(lastData, lastScenario);
        });
        yScale.addChangeListener(e -> {
            if (lastData != null && !logAxes.isSelected()) {
                updateChart(lastData, lastScenario);
            }
        });
        xScale.addChangeListener(e -> {
            if (lastData != null && !logAxes.isSelected()) {
                updateChart(lastData, lastScenario);
            }
        });
    }

    // ------------------------------------------------------------------
    //  Démarrage
    // ------------------------------------------------------------------
    public void show() {
        frame.setVisible(true);
        runBenchmark();
    }

    // ------------------------------------------------------------------
    //  Lancement en arrière-plan
    // ------------------------------------------------------------------
    private void runBenchmark() {
        Op op = (Op) opSelector.getSelectedItem();
        String scenario = (String) scenarioSelector.getSelectedItem();
        status.setText("Mesure en cours…");
        benchButton.setEnabled(false);
        progress.setIndeterminate(false);
        progress.setValue(0);
        progress.setMaximum(IMPL_NAMES.length * SIZES.length * 2);

        new SwingWorker<ChartData, Void>() {
            @Override
            protected ChartData doInBackground() throws Exception {
                return compute(op);
            }

            @Override
            protected void done() {
                try {
                    ChartData data = get();
                    updateChart(data, scenario);
                    status.setText("Terminé : " + op + " — " + scenario);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    status.setText("Erreur : " + ex.getMessage());
                } finally {
                    benchButton.setEnabled(true);
                }
            }
        }.execute();
    }

    // ------------------------------------------------------------------
    //  Calcul du benchmark (toutes impls x tailles, dense + épars)
    // ------------------------------------------------------------------
    private ChartData compute(Op op) {
        int total = IMPL_NAMES.length * SIZES.length * 2; // 2 scénarios
        int[] done = {0};

        Map<String, Series> series = new LinkedHashMap<>();
        for (String implName : IMPL_NAMES) {
            // Warmup complet (stabilisation) UNE fois par implémentation,
            // sur la plus petite taille.
            L3Impl wImpl = new L3Impl(implName, () -> factory(implName));
            bench.warmup(wImpl,
                    new DataScenario("warmup", SIZES[0],
                            DataGens.dense(SIZES[0]), SIZES[0] * 1L),
                    List.of(op));

            Series s = new Series(implName);
            boolean denseStopped = false;
            boolean sparseStopped = false;
            for (int size : SIZES) {
                // --- scénario dense (domaine = taille → densité 1) ---
                if (!denseStopped) {
                    long t0 = System.nanoTime();
                    Measured m = measure(
                            implName, "dense-" + size, size,
                            // DataGens.dense(size * 2), size * 1L, op);
                            DataGens.clusters(size * 5, 15), size * 5L, op);
                    long elapsed = System.nanoTime() - t0;
                    if (m != null) {
                        s.denseX.add((double) m.size());
                        s.denseY.add(m.medianNanos());
                        if (elapsed > TIME_LIMIT_NANOS) denseStopped = true;
                    }
                }
                progressNotify(++done[0], total);

                // --- scénario épars (domaine constant → densité décroissante) ---
                if (!sparseStopped) {
                    long t0 = System.nanoTime();
                    Measured e = measure(
                            implName, "sparse-" + size, size,
                            DataGens.sparse(Math.toIntExact(SPARSE_DOMAIN)),
                            SPARSE_DOMAIN, op);
                    long elapsed = System.nanoTime() - t0;
                    if (e != null) {
                        s.sparseX.add((double) e.size());
                        s.sparseY.add(e.medianNanos());
                        if (elapsed > TIME_LIMIT_NANOS) sparseStopped = true;
                    }
                }
                progressNotify(++done[0], total);
            }
            series.put(implName, s);
        }
        return new ChartData(series);
    }

    private Measured measure(String implName, String scenarioName, int size,
                             DataGen gen, long domain, Op op) {
        L3Impl impl = new L3Impl(implName, () -> factory(implName));
        DataScenario sc = new DataScenario(scenarioName, size, gen, domain);
        List<Measured> list = bench.runSkippingWarmup(impl, sc, List.of(op));
        return list.isEmpty() ? null : list.get(0);
    }

    private void progressNotify(int done, int total) {
        SwingUtilities.invokeLater(() -> {
            progress.setMaximum(total);
            progress.setValue(done);
        });
    }

    // ------------------------------------------------------------------
    //  Création de l'implémentation
    // ------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private static L3Set<Integer> factory(String name) {
        switch (name) {
            case "hashSet":        return fr.istic.pra.util.L3Sets.hashSet();
            case "treeSet":        return fr.istic.pra.util.L3Sets.treeSet();
            case "sortedArrayList": return fr.istic.pra.util.L3Sets.sortedArrayList();
            case "linkedList":     return fr.istic.pra.util.L3Sets.linkedList();
            case "sortedLinkedList": return fr.istic.pra.util.L3Sets.sortedLinkedList();
            case "mySet":          return new fr.istic.pra.myset.MySet();
            default: throw new IllegalArgumentException(name);
        }
    }

    // ------------------------------------------------------------------
    //  Rendu du graphe selon l'option de scénario
    // ------------------------------------------------------------------
    private void updateChart(ChartData data, String scenario) {
        lastData = data;
        lastScenario = scenario;
        XYChart chart = newChart();
        chart.setTitle("Temps médian (ns) par taille d'ensemble — "
                + opSelector.getSelectedItem() + " — " + scenario);
        boolean showDense = scenario.equals("dense") || scenario.equals("les deux");
        boolean showSparse = scenario.equals("épars") || scenario.equals("les deux");

        int index = 0;
        for (Series s : data.series.values()) {
            String impl = s.implName;
            Color color = PALETTE[index % PALETTE.length];

            if (showDense) {
                String name = impl + " [dense]";
                chart.addSeries(name, toArray(s.denseX), toArray(s.denseY));
                chart.getSeriesMap().get(name).setLineStyle(SOLID);
                chart.getSeriesMap().get(name).setLineColor(color);
                chart.getSeriesMap().get(name).setMarker(SeriesMarkers.CIRCLE);
                chart.getSeriesMap().get(name).setMarkerColor(color);
            }
            if (showSparse) {
                String name = impl + " [épars]";
                chart.addSeries(name, toArray(s.sparseX), toArray(s.sparseY));
                chart.getSeriesMap().get(name).setLineStyle(DASHED);
                chart.getSeriesMap().get(name).setLineColor(color);
                chart.getSeriesMap().get(name).setMarker(SeriesMarkers.SQUARE);
                chart.getSeriesMap().get(name).setMarkerColor(color);
            }
            index++;
        }

        if (!logAxes.isSelected()) {
            applyLinearYScale(chart, data, showDense, showSparse);
            applyLinearXScale(chart, data, showDense, showSparse);
        }

        // Remplace le panneau central (à l'intérieur de la zone avec le curseur)
        chartArea.remove(chartPanel);
        chartPanel = new XChartPanel<>(chart);
        chartArea.add(chartPanel, BorderLayout.CENTER);
        chartArea.revalidate();
        chartArea.repaint();
        frame.repaint();
        this.frame.getContentPane().revalidate();
    }

    private void applyLinearYScale(XYChart chart, ChartData data,
                                   boolean showDense, boolean showSparse) {
        List<Double> maxima = new ArrayList<>();
        for (Series s : data.series.values()) {
            if (showDense && !s.denseY.isEmpty()) maxima.add(Collections.max(s.denseY));
            if (showSparse && !s.sparseY.isEmpty()) maxima.add(Collections.max(s.sparseY));
        }
        if (maxima.isEmpty()) return;

        double globalMax = Collections.max(maxima);
        double minCurveMax = Collections.min(maxima);

        double yMax = globalMax;
        if (minCurveMax > 0 && globalMax > minCurveMax) {
            // Interpolation logarithmique entre la plus petite courbe (bas)
            // et l'étendue complète (haut) du curseur.
            int pos = yScale.getValue();
            double t = (pos - yScale.getMinimum()) / (double) (yScale.getMaximum() - yScale.getMinimum());
            double logMin = Math.log(minCurveMax);
            double logMax = Math.log(globalMax);
            yMax = Math.exp(logMin + t * (logMax - logMin));
        }

        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(yMax);
    }

    private void applyLinearXScale(XYChart chart, ChartData data,
                                   boolean showDense, boolean showSparse) {
        List<Double> maxima = new ArrayList<>();
        double globalMin = Double.POSITIVE_INFINITY;
        for (Series s : data.series.values()) {
            if (showDense && !s.denseX.isEmpty()) {
                maxima.add(Collections.max(s.denseX));
                globalMin = Math.min(globalMin, Collections.min(s.denseX));
            }
            if (showSparse && !s.sparseX.isEmpty()) {
                maxima.add(Collections.max(s.sparseX));
                globalMin = Math.min(globalMin, Collections.min(s.sparseX));
            }
        }
        if (maxima.isEmpty()) return;

        double globalMax = Collections.max(maxima);
        double minCurveMax = Collections.min(maxima);

        double xMax = globalMax;
        if (minCurveMax > 0 && globalMax > minCurveMax) {
            // Interpolation logarithmique entre la plus petite courbe (bas)
            // et l'étendue complète (haut) du curseur.
            int pos = xScale.getValue();
            double t = (pos - xScale.getMinimum()) / (double) (xScale.getMaximum() - xScale.getMinimum());
            double logMin = Math.log(minCurveMax);
            double logMax = Math.log(globalMax);
            xMax = Math.exp(logMin + t * (logMax - logMin));
        }

        chart.getStyler().setXAxisMin(globalMin);
        chart.getStyler().setXAxisMax(xMax);
    }

    private static final Color[] PALETTE = {
            new Color(0x1f77b4), new Color(0xd62728), new Color(0x2ca02c),
            new Color(0xff7f0e), new Color(0x9467bd), new Color(0x8c564b),
            new Color(0xe377c2), new Color(0x7f7f7f), new Color(0xbcbd22),
    };

    private static final BasicStroke SOLID =
            new BasicStroke(2.0f);
    private static final BasicStroke DASHED =
            new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10.0f, new float[]{6f, 4f}, 0.0f);

    // ------------------------------------------------------------------
    //  Helpers
    // ------------------------------------------------------------------
    private XYChart newChart() {
        XYChart chart = new XYChartBuilder()
                .width(950).height(620)
                .title("Temps médian (ns) par taille d'ensemble")
                .xAxisTitle("Taille")
                .yAxisTitle("Temps (ns)")
                .build();
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setXAxisLogarithmic(logAxes.isSelected());
        chart.getStyler().setYAxisLogarithmic(logAxes.isSelected());
        return chart;
    }

    private static double[] toArray(List<Double> list) {
        double[] a = new double[list.size()];
        for (int i = 0; i < a.length; i++) a[i] = list.get(i);
        return a;
    }

    // ------------------------------------------------------------------
    //  Structures de données internes
    // ------------------------------------------------------------------
    private static final class Series {
        final String implName;
        final List<Double> denseX = new ArrayList<>();
        final List<Double> denseY = new ArrayList<>();
        final List<Double> sparseX = new ArrayList<>();
        final List<Double> sparseY = new ArrayList<>();

        Series(String implName) { this.implName = implName; }
    }

    private record ChartData(Map<String, Series> series) {}

    // ------------------------------------------------------------------
    //  Main
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BenchmarkApp().show());
    }
}