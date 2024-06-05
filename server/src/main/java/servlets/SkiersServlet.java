/**
 * Copyright (C), 2015-2024, XXX有限公司
 * FileName: SkiersServlet
 * Author:   jiang
 * Date:     6/1/24 4:02 PM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package servlets;

import DTO.LiftRideDTO;
import DTO.NewLiftRideRequestDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author jiang
 * @create 6/1/24
 * @since 1.0.0
 */
@WebServlet(name = "SkiersServlet", urlPatterns = "/skiers/*")
@Slf4j
public class SkiersServlet extends HttpServlet {

    private Gson gson = new Gson();
    private Status status;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String pathInfo = req.getPathInfo();
//        if (pathInfo == null || pathInfo.matches("/\\d+/seasons/\\d+/days/\\d+/skiers/\\d+")) {
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            resp.getWriter().write("{\"error\": \"Endpoint not found\"}");
//            return;
//        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleNewLiftRide(req, resp);
    }

    private void handleNewLiftRide(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
        //1. verify request
        // Parse path parameters
        Status status = Status.builder()
                .status(HttpServletResponse.SC_CREATED)
                .build();
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || !pathInfo.matches("/\\d+/seasons/\\d+/days/\\d+/skiers/\\d+")) {
                throw new Exception("Invalid path");
            }


            String[] pathParts = pathInfo.split("/");
            log.info(gson.toJson(pathParts));

            if (pathParts.length != 8 ) {
                throw new Exception("Invalid inputs");
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = req.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            LiftRideDTO liftRide = gson.fromJson(sb.toString(), LiftRideDTO.class);

            int resortID = Integer.parseInt(pathParts[1]);
            String seasonID = pathParts[3];
            int dayID = Integer.parseInt(pathParts[5]);
            int skierID = Integer.parseInt(pathParts[7]);

            log.info("Parsed parameters - resortID: {}, seasonID: {}, dayID: {}, skierID: {}, liftRide:{}", resortID, seasonID, dayID, skierID, sb.toString());

            if(dayID < 1 || dayID > 366) {
                throw new Exception("Invalid inputs");
            }

            NewLiftRideRequestDTO newLiftRideRequestDTO = NewLiftRideRequestDTO.builder()
                    .liftRide(liftRide)
                    .resortID(resortID)
                    .dayID(dayID)
                    .seasonID(seasonID)
                    .skierID(skierID).build();
            log.info("Successfully create a new life ride: " + newLiftRideRequestDTO.toString());
            status.setDescription("Write successfully");
            status.setData(gson.toJson(newLiftRideRequestDTO));

        } catch (Exception ex){
            ex.printStackTrace();
            status.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            status.setDescription(ex.getMessage());
            log.error(ex.getMessage());
        } finally {
            out.print(gson.toJson(status));
            out.flush();
        }
    }


}
