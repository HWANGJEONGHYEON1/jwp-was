package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class IOUtils {

    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

    /**
     * @param BufferedReader 는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength 는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    /**
     *
     * @param is 는
     *          Request의 inputStream이며, Request 정보를 가지고 있다.
     */
    public static RequestLine getFirstLine(InputStream is) throws IOException {
        final RequestLine requestLine = RequestLine.getInstance();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return requestLine.parsing(br.readLine());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
