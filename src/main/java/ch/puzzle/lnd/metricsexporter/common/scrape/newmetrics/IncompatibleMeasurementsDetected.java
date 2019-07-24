package ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics;

public class IncompatibleMeasurementsDetected extends Exception {

    private static final String MEASSAGE = "Trying to add values from incompatible measurement type %s to type %s. " +
            "This occurs when two different MetricsScraper share the same name.";

    public IncompatibleMeasurementsDetected(Class targetMeasurementClass, Class incompatibleMeasurementClass) {
        super(String.format(
                MEASSAGE,
                incompatibleMeasurementClass.getSimpleName(),
                targetMeasurementClass.getSimpleName()
        ));
    }
}
