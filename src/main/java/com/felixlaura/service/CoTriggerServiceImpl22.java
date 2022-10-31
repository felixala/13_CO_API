package com.felixlaura.service;

import com.felixlaura.entity.CoTriggerEntity;
import com.felixlaura.entity.EligDtlsEntity;
import com.felixlaura.repository.CitizenAppRepository;
import com.felixlaura.repository.CoTriggerRepository;
import com.felixlaura.repository.DcCaseRepo;
import com.felixlaura.repository.EligDtlsRepository;
import com.felixlaura.utils.EmailUtils;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoTriggerServiceImpl22{

    @Autowired
    private CoTriggerRepository coTriggerRepository;

    @Autowired
    private EligDtlsRepository eligDtlsRepository;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private CitizenAppRepository citizenAppRepository;

    @Autowired
    private DcCaseRepo dcCaseRepo;

    Logger logger = LoggerFactory.getLogger(CoTriggerServiceImpl.class);

    public void pdfReport(HttpServletResponse response) throws IOException {

        CoTriggerEntity queryBuilder = new CoTriggerEntity();
        String status = "Pending";
        queryBuilder.setTrgStatus(status);

        Example<CoTriggerEntity> example = Example.of(queryBuilder);
        List<CoTriggerEntity> entities = coTriggerRepository.findAll(example);

        List<Document> listPdfs = new ArrayList<>();

        for (CoTriggerEntity entity : entities) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(Color.blue);

            Paragraph p = new Paragraph("Pending Report", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);

            //Number of columns
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{3.0f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
            table.setSpacingBefore(10);

            writeTableHeader(table);
            writeTableData(table, entity.getCaseNum());

            document.add(table);
            document.close();

        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA);
        fontHeader.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Name", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan Name", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan Status", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan Start Date", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan End Date", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Benefit Amount", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Denial Reason", fontHeader));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table , Long caseNum) {
        EligDtlsEntity eligDtlsEntity = eligDtlsRepository.findByCaseNum(caseNum);

        table.addCell(eligDtlsEntity.getHolderName());
        table.addCell(eligDtlsEntity.getPlanName());
        table.addCell(eligDtlsEntity.getPlanStatus());
        table.addCell(eligDtlsEntity.getPlanStartDate().toString());
        table.addCell(eligDtlsEntity.getPlanEndDate().toString());
        table.addCell(String.valueOf(eligDtlsEntity.getBenefitAmt()));
        table.addCell(eligDtlsEntity.getDenialReason());
    }

}
