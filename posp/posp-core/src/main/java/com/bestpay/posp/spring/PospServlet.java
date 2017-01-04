package com.bestpay.posp.spring;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.remoting.transport.netty.HttpPospDecoder;
import com.bestpay.posp.remoting.transport.netty.ServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Created by HR on 2016/9/19.
 */
@Component
@Slf4j
public class PospServlet extends HttpServlet {

    private String realIp;
    private String opsInfo;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getRequestURI().contains("healthcheck.html")){
            createHealthcheck(resp);
        }else if(!req.getRequestURI().contains(".html.ico.jsp.htm.php")){
            req.setCharacterEncoding("UTF-8");
            realIp = req.getHeader("x-forwarded-for") == null ? req.getRemoteAddr() : req.getHeader("x-forwarded-for");
            byte[] reqBodyBytes = readBytes(req.getInputStream(), req.getContentLength());
            ByteBuf msg = Unpooled.buffer();
            msg.writeBytes(reqBodyBytes);
            ByteBuf destBuf = channelReadProcess(null, msg);
            byte[] buffer = new byte[destBuf.readableBytes()];
            destBuf.readBytes(buffer);
            OutputStream outputStream = resp.getOutputStream();
            resp.setHeader(CONTENT_TYPE, "x-ISO-TPDU/x-auth");
            resp.setHeader(CONTENT_LENGTH, String.valueOf(buffer.length));
            resp.setHeader(SERVER, "Access-Guard-1000-Software/1.0");
            resp.setHeader(CONNECTION, HttpHeaders.Values.CLOSE);
            outputStream.write(buffer);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void createHealthcheck(HttpServletResponse resp) throws ServletException, IOException{
        try {
            BufferedInputStream inputStream = new BufferedInputStream(PospServlet.class.getResourceAsStream("/ops.htm"));
            StringBuilder sb = new StringBuilder();
            byte[] line = new byte[2048];
            while (inputStream.read(line) != -1) {
                sb.append(new String(line));
            }
            opsInfo = sb.toString();
            inputStream.close();
        } catch (FileNotFoundException e) {
            opsInfo = "ops info not exist";
        } catch (IOException e) {
            opsInfo = "ops info read error";
        }
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
//        out.println("<!DOCTYPE html>");
//        out.println("<html lang=\"en\">");
//        out.println("  <head>");
//        out.println("  <meta charset=\"UTF-8\">");
//        out.println("  <title>healthcheck</title>");
//        out.println("  </head>");
//        out.println("  <body>");
        out.print(opsInfo);
//        out.println("  </body>");
//        out.println("</html>");
        out.flush();
        out.close();
    }

    public ByteBuf channelReadProcess(ChannelHandlerContext ctx, Object msg) {
        ServerHandler serverHandler = (ServerHandler) PospApplicationContext.getBean("ServerHandler");
        HttpPospDecoder httpPospDecoder = (HttpPospDecoder)PospApplicationContext.getBean("HttpPospDecoder");
        IsoMessage iso = new IsoMessage();
        ByteBuf destBuf = null;
        try {
            iso = httpPospDecoder.decode(ctx, (ByteBuf)msg);
            iso.setXRealIp(realIp);//记录终端IP
            destBuf = serverHandler.serviceProcess(ctx, iso);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[E N D]channelRead Exception:" + e.getMessage());
        }
        return destBuf;
    }

    public static final byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                    if (readLengthThisTime == -1) {
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return message;
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }
        return new byte[] {};
    }
}
