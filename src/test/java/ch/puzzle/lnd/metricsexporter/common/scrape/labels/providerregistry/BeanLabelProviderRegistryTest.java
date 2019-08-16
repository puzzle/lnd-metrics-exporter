package ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class BeanLabelProviderRegistryTest {

    private static final String EXISTING_LABEL_PROVIDER = "label-provider";

    @Mock
    ApplicationContext context;

    @Mock
    LabelProvider labelProvider;

    BeanLabelProviderRegistry registry;

    @Before
    public void setup() {
        doReturn(EXISTING_LABEL_PROVIDER).when(labelProvider).name();
        doReturn(Map.of("bean", labelProvider)).when(context).getBeansOfType(LabelProvider.class);

        registry = new BeanLabelProviderRegistry();
        registry.onApplicationEvent(new ContextRefreshedEvent(context));
    }

    @Test
    public void testLabelProviderExists() throws NoSuchLabelProviderException {
        var found = registry.lookup(EXISTING_LABEL_PROVIDER);
        assertSame(labelProvider, found);
    }

    @Test(expected = NoSuchLabelProviderException.class)
    public void testLabelProviderDoesNotExists() throws NoSuchLabelProviderException {
        registry.lookup("inexistent-label-provider");
    }

}
