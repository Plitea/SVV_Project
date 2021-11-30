package Tests;
import com.dragos.HttpServer;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;


import org.junit.Test;

public class ServerTest {

    HttpServer server = new HttpServer();


    @Test
    public void testGetContentType() {
        assertEquals("Function does not return text/html", "text/html", server.getContentType("home.html"));
        assertEquals("Function does not return other/css", "other/css", server.getContentType("home.css"));
        assertEquals("Function does not return text/plain", "text/plain", server.getContentType("text"));
    }

    @Test
    public void testReadFileData() throws IOException {
        File WEB_ROOT = new File("./HTML_files");
        File file = new File(WEB_ROOT, "index.html");
        assertNotNull("File was not found", server.readFileData(file, (int)file.length()));
    }


    @Test
    public void test_set_getStatus1() {
        server.setState(1);
        assertEquals(1,server.getStateServer());
    }

    @Test
    public void test_set_getStatus2() {
        server.setState(2);
        assertEquals(2,server.getStateServer());
    }

    @Test
    public void test_set_getStatus3() {
        server.setState(3);
        assertEquals(3,server.getStateServer());
    }

    @Test
    public void testListen() {
        server.setPort(9000);
        server.acceptServerPort();
        int connection = server.conection;
        assertEquals(0,connection);

    }

    @Test
    public void testInvalidPort1() {
        assertFalse(server.setPort(-1));
    }

    @Test
    public void testInvalidPort2() {
        assertFalse(server.setPort(100001));
    }


    @Test
    public void testAcceptServerPort()
    {
        server.setPort(9001);
        assertEquals(true,server.acceptServerPort());
    }

    @Test
    public void testNotAcceptServerPort()
    {
        server.setPort(10501);
        assertEquals(false,server.acceptServerPort());
    }

    @Test
    public void testNotAcceptServerPort2()
    {
        server.setPort(-2);
        assertEquals(false,server.acceptServerPort());
    }
}