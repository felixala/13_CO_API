package com.felixlaura.controller;

import com.felixlaura.service.CoTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class CoRestController {

    @Autowired
    private CoTriggerService coTriggerService;
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    @GetMapping("/pdf")
    public void pdfReport(HttpServletResponse response) throws IOException {
        String currentDateTime = dateFormatter.format(new Date());
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename = data-" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        coTriggerService.pdfReport(response);
    }
}
