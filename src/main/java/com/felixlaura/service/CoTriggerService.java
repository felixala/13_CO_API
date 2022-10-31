package com.felixlaura.service;

import com.felixlaura.binding.PdfReport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface CoTriggerService {

    //public List<PdfReport> generatePdfPendingTrigger(String trgStatus);

    public void pdfReport(HttpServletResponse response) throws IOException;

}
