package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LabelProviderExecutorTest {

    @Mock
    private LabelProvider labelProviderOne;

    @Mock
    private LabelProvider labelProviderTwo;

    @Mock
    private LndApi lndApi;

    @Mock
    private ExecutorService executorService;

    private Labels labelsOne;

    private Labels labelsTwo;

    private LabelProviderExecutor executor;

    @Before
    public void setup() throws Exception {
        labelsOne = Labels.create().with("label-one", "one");
        labelsTwo = Labels.create().with("label-two", "two");

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(executorService).submit(any(Runnable.class));
        doReturn(labelsOne).when(labelProviderOne).provide(same(lndApi));
        doReturn(labelsTwo).when(labelProviderTwo).provide(same(lndApi));

        executor = new LabelProviderExecutor(List.of(labelProviderOne, labelProviderTwo), lndApi);
    }

    @Test
    public void testLabelProvidersAreExecuted() throws Exception {
        executor.execute(executorService);

        verify(labelProviderOne).provide(same(lndApi));
        verify(labelProviderTwo).provide(same(lndApi));
        assertEquals(labelsOne.merge(labelsTwo), executor.collect());
        assertFalse(executor.hasErrors());
    }

    @Test
    public void testLabelProviderFailed() throws Exception {
        doThrow(new Exception()).when(labelProviderOne).provide(lndApi);

        executor.execute(executorService);

        verify(labelProviderOne).provide(same(lndApi));
        verify(labelProviderTwo).provide(same(lndApi));
        assertEquals(labelsTwo, executor.collect());
        assertTrue(executor.hasErrors());
    }
}
