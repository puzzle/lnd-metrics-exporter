package ch.puzzle.lnd.metricsexporter.scrapers.node.reachable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LnDaemonDetectorTest {

    private static final String URI = "039dc3829b2f3dea6037f16167ca5c1ad0941ad2af028f6357df3467ef72cc71f1@127.0.0.1:9735";

    private LnDaemonDetector detector;

    @Mock
    private Socket socket;

    @Mock
    private OutputStream out;

    @Mock
    private InputStream in;

    @Before
    public void setup() throws Exception {
        detector = new LnDaemonDetector(socket);

        doReturn(out).when(socket).getOutputStream();
        doReturn(in).when(socket).getInputStream();
    }

    @Test
    public void shouldDetectLndWhenResponseLengthMatches() throws IOException {
        doReturn(new byte[50]).when(in).readAllBytes();

        assertTrue(detector.isLnDaemonRunning(URI));
    }

    @Test
    public void shouldDetectNoLndWhenResponseLengthIsTooSmall() throws IOException {
        doReturn(new byte[49]).when(in).readAllBytes();

        assertFalse(detector.isLnDaemonRunning(URI));
    }

    @Test
    public void shouldDetectNoLndWhenResponseLengthIsTooLarge() throws IOException {
        doReturn(new byte[51]).when(in).readAllBytes();

        assertFalse(detector.isLnDaemonRunning(URI));
    }

    @Test
    public void shouldDetectNoLndWhenSocketConnectFails() throws IOException {
        doThrow(new IOException()).when(socket).connect(any());

        assertFalse(detector.isLnDaemonRunning(URI));
    }
}
